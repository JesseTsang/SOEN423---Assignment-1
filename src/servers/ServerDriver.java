package servers;

import common.BankServerImpl;
import domain.BranchID;
import domain.EditRecordFields;

public class ServerDriver 
{
	public static void main(String[] args) 
	{		
		try
		{
			String firstname1 = "Paul";
			String lastName1 = "Netto";
			String address1 = "4965, Heritage Drive";
			String phone1 = "450-123-4567";
			String customerID1 = "BCCN4567";
			BranchID branch1 = BranchID.QC;
			
			EditRecordFields editAddress1 = EditRecordFields.address;
			String newAddressValue1 = "3921 rue Ellice";
			EditRecordFields editPhone1 = EditRecordFields.phone;
			String newPhoneValue1 = "438-123-123"; //Should produce error
			
			BankServerImpl serverTest1 = new BankServerImpl(branch1.toString(), 1099);
			
			//serverTest1.createAccount(firstname1, lastName1, address1, phone1, customerID1, branch1);
			//serverTest1.deposit(customerID1, 1000);
			//serverTest1.editRecord(customerID1, editAddress1, newAddressValue1);
			//serverTest1.editRecord(customerID1, editPhone1, newPhoneValue1);			
		}
		catch (Exception e)
		{
			System.err.println("Server Driver Log: Error: Server initialization failure.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
