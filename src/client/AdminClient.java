package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

import common.BankServerInterface;
import domain.BranchID;

public class AdminClient implements Runnable
{
	//RMI Variables
	private static final String BANK_HOST = "localhost";
	private static final int BANK_PORT = 1099;
	private Registry registry = null;
	private String customerID;
	private BranchID branchID;
	
	public AdminClient (String customerID, BranchID branchID) throws RemoteException, NotBoundException
	{
		this.customerID = customerID;
		this.branchID = branchID;
		registry = LocateRegistry.getRegistry(BANK_HOST, BANK_PORT);
	}
	
	
	@Override
	public void run()
	{
		BankServerInterface bankServer = null;
		
		try
		{
			bankServer = (BankServerInterface) registry.lookup(this.branchID.toString());
		}
		catch (RemoteException | NotBoundException e)
		{
			e.getMessage();
		}		
	}

}
