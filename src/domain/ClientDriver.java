package domain;

public class ClientDriver
{

	public static void main(String[] args) throws Exception
	{
		String firstName1 = "James";
		String lastName1 = "Castillo";		
		String address1 = "Rue HelloWorld";
		String phoneNum1 = "514-123-1234";
		String clientNumber1 = "BCCC1234";
		BranchID branch1 = BranchID.BC;
		
		Client client1 = new Client(firstName1, lastName1, address1, phoneNum1, clientNumber1, branch1);
		client1.deposit(100);
		client1.withdraw(90);
		//client1.withdraw(20);
		System.out.println(client1.toString());
	}
}
