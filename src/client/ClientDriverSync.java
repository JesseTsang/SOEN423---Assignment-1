package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import domain.BranchID;

public class ClientDriverSync implements Runnable
{

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[])
	{
		String customer1 = "BCCA1234";
		String customer2 = "BCCA1234";
		String customer3 = "BCCA1234";
		String customer4 = "BCCA1234";
		
		BranchID branch1 = BranchID.BC;
		BranchID branch2 = BranchID.BC;
		BranchID branch3 = BranchID.BC;
		BranchID branch4 = BranchID.BC;
		
		try 
		{
			CustomerClient test1 = new CustomerClient (customer1, branch1);
			CustomerClient test2 = new CustomerClient (customer2, branch2);
			CustomerClient test3 = new CustomerClient (customer3, branch3);
			CustomerClient test4 = new CustomerClient (customer4, branch4);
		} 
		catch (RemoteException | NotBoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
