package servers;

import common.BankServerImpl;
import domain.BranchID;

public class serverDriver {

	public static void main(String[] args) 
	{
		try
		{
			new BankServerImpl(BranchID.QC, 2964);
		}
		catch (Exception e)
		{
			System.err.println("Server Driver Log: Error: Server initialization failure.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
