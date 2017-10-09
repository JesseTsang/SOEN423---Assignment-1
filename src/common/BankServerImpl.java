package common;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
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

	//1. Each branch will have its separate server
	public BankServerImpl(BranchID branchID, int UDPPort) throws RemoteException, AlreadyBoundException 
	{
		super();
		
		this.branchID = branchID;
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
	public synchronized Boolean createAccount(String firstName, String lastName, String address, int phone, BranchID branchID) throws RemoteException 
	{
		this.logger.info("Initiating user account creation for " + firstName + " " + lastName);
		
		//If the user IS at the right branch ... we start the operation.
		if(branchID == this.branchID)
		{
			String key = Character.toString((char)lastName.charAt(0));
			//ArrayList<Client>
			
		}
		
		return null;
	}

	@Override
	public Boolean editRecord(String customerID, String fieldName, String newValue) throws RemoteException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Integer> getAccountCount() throws RemoteException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deposit(String customerID, int amt) throws RemoteException 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void withdraw(String customerID, int amt) throws RemoteException 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getBalance(String customerID) throws RemoteException 
	{
		// TODO Auto-generated method stub
	}

}
