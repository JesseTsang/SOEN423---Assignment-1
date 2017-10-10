package client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.logging.Logger;

import common.BankServerInterface;
import domain.BranchID;
import domain.EditRecordFields;

public class AdminClient
{
	//RMI Variables
	private static final String BANK_HOST = "localhost";
	private static final int BANK_PORT = 1099;
	private Registry registry = null;
	private String customerID;
	private BranchID branchID;
	
	private static final int ACCOUNT_TYPE_POS = 2;
	
	public AdminClient(String customerID, BranchID branchID) throws RemoteException, NotBoundException
	{
		char accountType = (char)customerID.charAt(ACCOUNT_TYPE_POS);
		if(accountType != 'M')
		{
			throw new RemoteException ("Login Error: This client is for managers only.");
		}
		
		this.branchID = branchID;
		registry = LocateRegistry.getRegistry(BANK_HOST, BANK_PORT);
		
		System.out.println("Login Sucessed. | Customer ID: " + this.customerID + " | Branch ID: " + this.branchID.toString());
	}
	
	public synchronized void createAccountRecord(String firstName, String lastName, String address, String phone, String customerID, BranchID branch) 
			throws AccessException, RemoteException, NotBoundException
	{	
		BankServerInterface bankServer = (BankServerInterface) registry.lookup(this.branchID.toString());
		boolean result = bankServer.createAccount(firstName, lastName, address, phone, customerID, this.branchID);
		
		if(result)
		{
			System.out.println("Account Successfully Created. | Customer ID: " + customerID);
		}
		else
		{
			System.out.println("Account Creation Error: Account Unable to Create. Please consult server log.");
		}
	}
	
	public synchronized void editRecord(String customerID, EditRecordFields fieldName, String newValue) throws AccessException, RemoteException, NotBoundException
	{
		BankServerInterface bankServer = (BankServerInterface) registry.lookup(this.branchID.toString());
		boolean result = bankServer.editRecord(customerID, fieldName, newValue);
		
		if(result)
		{
			System.out.println("Account Successfully Modified. | Customer ID: " + customerID);
		}
		else
		{
			System.out.println("Account Edit Error: Account Unable to Modify. Please consult server log.");
		}
	}
	
	public synchronized HashMap<String, String> getAccountCount() throws AccessException, RemoteException, NotBoundException
	{
		BankServerInterface bankServer = (BankServerInterface) registry.lookup(this.branchID.toString());
		HashMap<String, String> result = bankServer.getAccountCount();
		return result;
	}
	
	public static void main(String args[])
	{
		try
		{	
			String testAdminID = "BCMJ1234";
			AdminClient testClient1 = new AdminClient(testAdminID, BranchID.BC);
		}
		catch (RemoteException | NotBoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
