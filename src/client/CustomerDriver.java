package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import domain.BranchID;

public class CustomerDriver 
{

	public static void main(String[] args) 
	{
		String customer1 = "BCMJ1234";
		BranchID branch1 = BranchID.QC;
		
		try
		{
			CustomerClient testClient1 = new CustomerClient(customer1, branch1);
			
			testClient1.getBalance();
			testClient1.deposit(1000);
			testClient1.withdraw(4000);
		}
		catch (RemoteException | NotBoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
