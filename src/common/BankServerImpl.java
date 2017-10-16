package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import domain.BranchID;
import domain.Client;
import domain.EditRecordFields;

/*
 * To-do:
 * 1. Rewrite duplicate methods, such as verify if an account exist with a customerID.
 */

public class BankServerImpl extends UnicastRemoteObject implements BankServerInterface 
{
	private static final long serialVersionUID = 1L;
	
	//Variables for RMI Registry
	private static final String BANK_HOST = "localhost";
	private static final int    BANK_PORT = 1099;
	private Registry registry = LocateRegistry.getRegistry(BANK_HOST, BANK_PORT);
		
	//Variable for each separate bank server
	private Map<String, ArrayList<Client>> clientList = new HashMap<String, ArrayList<Client>>();
	private BranchID branchID;
	private Logger logger = null;
	private static int UDPPort;
	
	private static final int    CLIENT_NAME_INI_POS = 3;

	//1. Each branch will have its separate server
	public BankServerImpl(String branchID, int UDPPort) throws RemoteException, AlreadyBoundException 
	{
		super();
		
		
		this.branchID = BranchID.valueOf(branchID);
		BankServerImpl.UDPPort = UDPPort;
		
		//1.1 Logging Initiation
		this.logger = this.initiateLogger();
		this.logger.info("Initializing Server ...");
		
		//serverInitialization();
		
		//1.2 Bind to the local server to the RMI Registry
		registry = LocateRegistry.createRegistry(UDPPort);
		registry.bind(this.branchID.toString(), this);
		
		System.out.println("Server: " + branchID + " initialization success.");
		System.out.println("Server: " + branchID + " port is : " + UDPPort);
	}	

	private Logger initiateLogger() 
	{
		Logger logger = Logger.getLogger("Server Logs/" + this.branchID + "- Server Log");
		FileHandler fh;
		
		try
		{
			//FileHandler Configuration and Format Configuration
			fh = new FileHandler("Server Logs/" + this.branchID + " - Server Log.log");
			
			//Disable console handling
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			
			//Formatting configuration
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		}
		catch (SecurityException e)
		{
			System.err.println("Server Log: Error: Security Exception " + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("Server Log: Error: IO Exception " + e);
			e.printStackTrace();
		}
		
		System.out.println("Server Log: Logger initialization success.");
		
		return logger;
	}

	@Override
	public synchronized Boolean createAccount(String firstName, String lastName, String address, String phone, String customerID, BranchID branchID) 
			throws RemoteException 
	{
		this.logger.info("Initiating user account creation for " + firstName + " " + lastName);
		
		//If the user IS at the right branch ... we start the operation.
		if (branchID == this.branchID)
		{
			//Each character will be a key, each key will starts with 10 buckets.
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = new ArrayList<Client>(10);
			
			//If a key doesn't exist ... for example no client with last name Z ... then ...
			if(!clientList.containsKey(key))
			{
				//Adds a key and 10 buckets
				clientList.put(key, values);
			}
			
			//This is to test if the account is already exist.
			//1. Extract the buckets of the last Name ... for example, we will get all the clients with last name start with Z
			values = clientList.get(key);
			
			//2. After extracted the buckets, we loops to check if the customerID is already exist.
			for (Client client: values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					this.logger.severe("Server Log: | Account Creation Error: Account Already Exists | Customer ID: " + customerID);
					throw new RemoteException ("Server Error: | Account Creation Error: Account Already Exists | Customer ID: " + customerID);
				}
			}
			
			//3. If no existing account found, then we create a new account.
			Client newClient;
			
			try
			{
				newClient = new Client(firstName, lastName, address, phone, customerID, branchID);
				values.add(newClient);
				clientList.put(key, values);
				
				this.logger.info("Server Log: | Account Creation Successful | Customer ID: " + customerID);
				this.logger.info(newClient.toString());
			}
			catch (Exception e)
			{
				this.logger.severe("Server Log: | Account Creation Error. | Customer ID: " + customerID + " | " + e.getMessage());
				throw new RemoteException(e.getMessage());
			}	
		}//end if clause ... if not the same branch
		else
		{
			this.logger.severe("Server Log: | Account Creation Error: BranchID Mismatch | Customer ID: " + customerID);
			throw new RemoteException("Server Error: | Account Creation Error: BranchID Mismatch | Customer ID: " + customerID);		
		}
		
		return true;
	}

	@Override
	public synchronized Boolean editRecord(String customerID, EditRecordFields fieldName, String newValue) throws RemoteException 
	{
		//1. Check if such client exist.
		String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
		ArrayList<Client> values = clientList.get(key);
		
		for (Client client: values)
		{
			//1.1 Client Found
			if (client.getCustomerID().equals(customerID))
			{		
				switch(fieldName)
				{
					case address:
						client.setAddress(newValue);
						this.logger.info("Server Log: | Edit Record Log: Address Record Modified Successful | Customer ID: " + client.getCustomerID());
						break;
					
					case phone:
						try
						{
							if(client.verifyPhoneNumber(newValue))
							{
								client.setPhoneNumber(newValue);
								this.logger.info("Server Log: | Edit Record Log: Phone Record Modified Successful | Customer ID: " + client.getCustomerID());
								break;
							}
						}
						catch (Exception e)
						{
							this.logger.severe("Server Log: | Edit Record Error: Invalid Phone Format | Customer ID: " + client.getCustomerID());
							e.printStackTrace();
							break;
						}
					
					case branch:
						try
						{
							for(BranchID enumName : BranchID.values())
							{
								if(enumName.name().equalsIgnoreCase(newValue))
								{
									client.setBranchID(enumName);
									this.logger.info("Server Log: | Edit Record Log: Branch ID Modified Successful | Customer ID: " + client.getCustomerID());
									break;
								}
								else
								{
									this.logger.severe("Server Log: | Edit Record Error: Invalid Branch ID | Customer ID: " + client.getCustomerID());
									break;
								}
							}
						}
						catch (Exception e)
						{
							this.logger.severe("Server Log: | Edit Record Error: Unknow Branch Error | Customer ID: " + client.getCustomerID());
							break;
						}					
				}//end switch statements
			}//end if clause (customer found)
			else
			{
				this.logger.severe("Server Log: | Record Edit Error: Account Not Found | Customer ID: " + customerID);
				throw new RemoteException ("Server Log: | Edit Record Error: Account Not Found | Customer ID: " + customerID);
			}
		}
			
		
		return null;
	}

	@Override
	public synchronized HashMap<String, String> getAccountCount() throws RemoteException 
	{
		HashMap<String, String> totalAccountCount = new HashMap<String, String>();
		
		this.logger.info("Acquiring local account count.");
		String localAccountCount = Integer.toString(getLocalAccountCount());
		totalAccountCount.put(this.branchID.toString(), localAccountCount);
		
		DatagramSocket socket = null;
		
		//1. Create UDP Socket 
		try
		{
			this.logger.info("Creating UDP socket to receive data from other servers.");
			socket = new DatagramSocket(BankServerImpl.UDPPort);
			
			String[] bankServers = registry.list();
		    
		    //2. Get RMI Registry List of other servers.
		    for(String bankServer : bankServers)
		    {
		    	if(bankServer.equals(this.branchID.toString()))
		    	{
		    		continue;
		    	}
		    	
		    	BankServerInterface otherServer = null;
		    	
		    	try
		    	{
		    		otherServer = (BankServerInterface) registry.lookup(bankServer);
		    	}
		    	catch (NotBoundException e)
		    	{
		    		this.logger.severe("Server Log: | getAccountCount() Error: " + bankServer + " Not Bound.");
		    		throw new RemoteException(e.getMessage());
		    	}
		    	
		    	//3. For each server we will ask for their local total count.
		    	this.logger.info("Receiving UDP data from server " + bankServer);
		    	
		    	Boolean recv = false;
		    	String rData = null;
		    	
		    	while(!recv)
		    	{
		    		otherServer.getUDPData(BankServerImpl.UDPPort);
		    		byte[] buffer = new byte[1024];
		    		DatagramPacket request = new DatagramPacket(buffer, buffer.length);
		    		
		    		try
		    		{
		    			socket.receive(request);
		    		}
		    		catch (IOException e)
		    		{
		    			this.logger.severe("Server Log: | getAccountCount() Error: IO Exception at receiving reply.");
		    			throw new RemoteException (e.getMessage());
		    		}
		    		
		    		rData = new String(request.getData());
		    		this.logger.info("Received data from " + bankServer);
		    		
		    		if(request.getPort() > 9000)
		    		{
		    			recv = true;
		    		}
		    	}
		    	
		    	totalAccountCount.put(bankServer, rData);
		    }//end for loop (bankServer : bankServers)
		    
		    socket.close();
		}
		catch (SocketException e)
		{
			this.logger.severe("Server Log: | getAccountCount() Error | " + e.getMessage());
			throw new RemoteException(e.getMessage());
		}
    
		return totalAccountCount;
	}

	@Override
	public synchronized void deposit(String customerID, double amount) throws RemoteException 
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Deposit Error: Attempted to deposit incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			throw new RemoteException ("Server Log: | Deposit Error: Attempted to deposit incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
		}
		
		//1. Verify the customerID is valid.
		try
		{
			//Maybe move the verification process to a separate method
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = clientList.get(key);
			
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					client.deposit(amount);
					double newBalance = client.getBalance();
					this.logger.info("Server Log: | Deposit Log: | Deposit: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
			throw new RemoteException("Server Log: | Deposit Error: | Unable to locate account. | Customer ID: " + customerID);
		}
	}

	@Override
	public synchronized void withdraw(String customerID, double amount) throws RemoteException 
	{
		if (amount <= 0)
		{
			this.logger.info("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
			throw new RemoteException ("Server Log: | Withdrawl Error: Attempted to withdraw incorrect amount. | Amount: " + amount + " | Customer ID: " + customerID);
		}
		
		//1. Verify the customerID is valid.
		try
		{
			//Maybe move the verification process to a separate method
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = clientList.get(key);
			
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					double oldBalance = client.getBalance();
					double newBalance = oldBalance - amount;				
					
					if (newBalance < 0 )
					{
						System.out.println("Error: Negative withdrawal.");
						this.logger.severe("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);
						
						throw new RemoteException("Server Log: | Withdrawl Error: Attempted to withdraw more than current balance. | Amount: " 
								+ amount + " | Customer Balance: " + oldBalance + " | Customer ID: " + customerID);			
					}
					else
					{
						client.withdraw(amount);
						
						this.logger.info("Server Log: | Withdrawl Log: | Withdrawl: " + amount + " | Balance: " + newBalance + " | Customer ID: " + customerID);					
					}					
				}
			}
		}
		catch (Exception e)
		{
			throw new RemoteException(e.getMessage());
		}

	}

	@Override
	public synchronized double getBalance(String customerID) throws RemoteException 
	{
		double newBalance = 0;
		
		//1. Verify the customerID is valid.
		try
		{
			//Maybe move the verification process to a separate method
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			
			ArrayList<Client> values = clientList.get(key);
					
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					newBalance = client.getBalance();
							
					this.logger.info("Server Log: | Balance Log: | Balance: " + newBalance + " | Customer ID: " + customerID);
					
					return newBalance;
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Withdrawl Error: | Unable to locate account. | Customer ID: " + customerID);
			throw new RemoteException("Server Log: | Withdrawl Error: | Unable to locate account. | Customer ID: " + customerID);
		}
		
		return newBalance;
	}
	
	@Override
	public synchronized Client retrieveAccount(String customerID) throws RemoteException
	{
		try
		{
			String key = Character.toString((char)customerID.charAt(CLIENT_NAME_INI_POS));
			ArrayList<Client> values = clientList.get(key);
			
			for (Client client : values)
			{
				if (client.getCustomerID().equals(customerID))
				{
					return client;
				}
			}
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | Retrieve Account Error: Account Not Found. | Customer ID: " + customerID + " | " + e.getMessage());
			throw new RemoteException(e.getMessage());		
		}
		
		return null;
	}

	@Override
	public synchronized int getLocalAccountCount() throws RemoteException
	{
		int totalLocalAccountCount;
		
		totalLocalAccountCount = clientList.values().size();
		
		return totalLocalAccountCount;
	}
	
	@Override
	//This will create data-gram socket to connect to other servers.
	//Necessary in order to give total account count to other servers.
	public synchronized void getUDPData(int portNum) throws RemoteException
	{
		DatagramSocket dataSocket;
		
		try
		{
			this.logger.info("Initialiating a datagram with port " + portNum);
			dataSocket = new DatagramSocket();
			
			this.logger.info("Converting total local account count data to ByteArrayOutPutStream.");
			byte[] message = ByteBuffer.allocate(4).putInt(getLocalAccountCount()).array();
			
			//Acquire local host
			InetAddress hostAddress = InetAddress.getByName("localhost");

			this.logger.info("Generating datagram and sending packet to port" + portNum);
			DatagramPacket request = new DatagramPacket(message, message.length, hostAddress, portNum);
			dataSocket.send(request);
			
			dataSocket.close();
		}
		catch (Exception e)
		{
			this.logger.severe("Server Log: | getUDPData Error: " + e.getMessage());
			throw new RemoteException(e.getMessage());
		}	
	}
	
	public static void main(String args[])
	{
		DatagramSocket dataSocket = null;
		
		//Start the process to listen on UDPPort, so we can accept request from other servers.
		try
		{
			//1. Bind the UDPPort to the data gram socket.
			dataSocket = new DatagramSocket(UDPPort);
			
			System.out.println("Listening Process - DataSocket Created.");
			
			//2. Create a byte buffer to receive data.
			byte[] buffer = new byte[1000];
			
			System.out.println("Listening Process - Buffer Array Created.");
			
			//3. Start listening on port
			while(true)
			{
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				dataSocket.receive(request);
				String reply = "ack";
				buffer = reply.getBytes();
				DatagramPacket replyPackey = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
				dataSocket.send(replyPackey);
				System.out.println("UDP Server on port " + UDPPort + " is now listening.");
			}
		}
		catch (Exception e)
		{
			System.err.println("Main Error: Unable to start port listening");
			System.err.println(e.getMessage());
		}
		finally
		{
			dataSocket.close();	
			System.out.println("Listening Process - DataSocket Closed.");
		}
	}	
}