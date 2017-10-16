package client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import common.BankServerInterface;
import domain.BranchID;

public class CustomerClient
{
	//RMI Variables
	private static final String BANK_HOST = "localhost";
	private static final int BANK_PORT = 1099;
	private Registry registry = null;
	private String customerID;
	private BranchID branchID;
		
	public CustomerClient(String customerID, BranchID branchID) throws RemoteException, NotBoundException
	{
		this.customerID = customerID;
		this.branchID = branchID;
		
		registry = LocateRegistry.getRegistry(BANK_HOST, BANK_PORT);
		
		
		System.out.println("Login Sucessed. | Customer ID: " + this.customerID + " | Branch ID: " + this.branchID.toString());	
	}
	
	public synchronized void deposit(double amount) throws AccessException, RemoteException, NotBoundException
	{
		BankServerInterface bankServer = (BankServerInterface)registry.lookup(this.branchID.toString());
		
		bankServer.deposit(this.customerID, amount);
		//double balance = bankServer.getBalance(this.customerID);
		
		//System.out.println("Deposit Successful. | Customer ID: " + this.customerID + " | Deposit Amount: " + amount + " | Account Balance: " + balance);		
	}
	
	public synchronized void withdraw(double amount) throws AccessException, RemoteException, NotBoundException
	{
		BankServerInterface bankServer = (BankServerInterface)registry.lookup(this.branchID.toString());
		
		bankServer.withdraw(this.customerID, amount);
		//double balance = bankServer.getBalance(this.customerID);
		
		//System.out.println("Withdrawal Successful. | Customer ID: " + this.customerID + " | Withdraw Amount: " + amount + " | Account Balance: " + balance);		
	}
	
	public synchronized void getBalance() throws AccessException, RemoteException, NotBoundException
	{
		BankServerInterface bankServer = (BankServerInterface)registry.lookup(this.branchID.toString());
		
		double balance = bankServer.getBalance(this.customerID);
		
		System.out.println("Operation Sucessed. | Customer ID: " + this.customerID + " | Account Balance: " + balance);		
	}

	public static void main(String[] args)
	{
		/*String customer1 = "BCCA1234";
		BranchID branch1 = BranchID.QC;
		
		try
		{
			CustomerClient testClient1 = new CustomerClient(customer1, branch1);
			testClient1.deposit(1000);
		}
		catch (RemoteException | NotBoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	

}
