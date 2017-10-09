package domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class Client 
{
	private String firstName;
	private String lastName;
	private String address;
	private String phoneNumber;
	private String accountNumber;
	
	private BranchID branchID;
	private double balance;
	private String filename;
	private Logger logger = null;
	
	private static final int ACCOUNT_TYPE_POS = 2;
	private static Pattern ACCOUNT_NUMER_PATTERN = java.util.regex.Pattern.compile("^(BC|MB|NB|QC)(C|M)(\\d{4})$");
	private static Pattern PHONE_PATTERN = java.util.regex.Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
	
	public Client(String firstName, String lastName, String address, String phoneNumber, String accountNumber, BranchID branchID) throws Exception
	{
		//If pass verification test ...
		if(verify(firstName, lastName, accountNumber, phoneNumber))
		{
			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
			this.phoneNumber = phoneNumber;
			this.accountNumber = accountNumber;
			this.branchID = branchID;
			
			this.balance = 0.0;
		}
		
		char accountType = Character.toUpperCase(accountNumber.charAt(ACCOUNT_TYPE_POS));
		
		this.logger = logFile(accountType);
		this.logger.info("Client " + accountNumber + "created successfully.");
		
		System.out.println("Account created successed.");
		
	}

	private Logger logFile(char accountType) throws Exception
	{
		if(accountType == 'C')
		{
			System.out.println("Account Type : " + accountType + " | Branch ID: " + this.branchID + " | Account Number: " + this.accountNumber);
			this.filename = "Clients Logs/" + this.branchID + " - " + this.accountNumber;
			System.out.println("File Path: " + this.filename);
		}
		else if (accountType == 'M')
		{
			System.out.println("Account type is M.");
			this.filename = "managers/" + this.branchID + " - " + this.accountNumber; 		
		}
		else
		{
			throw new Exception ("Error: Account mismatch.");
		}
		
		Logger logger = Logger.getLogger(this.filename);
		FileHandler fh;
		
		try
		{
			fh = new FileHandler(this.filename + ".log");
			
			logger.setUseParentHandlers(false);
			logger.addHandler(fh);
			
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		}
		catch (SecurityException e)
		{
			System.err.println("Logging Error: Security Exception "+ e);
		}
		catch (IOException e)
		{
			System.err.println("Logging Error: IO Exception " + e);
		}
		
		System.out.println("Client Log: Logger Initialization Success.");
		
		return logger;
			
	}

	private boolean verify(String firstName, String lastName, String accountNumber, String phoneNumber) throws Exception
	{	
		if (firstName.isEmpty())
		{
			throw new Exception ("Error: Client missing first name.");
		}
		
		if (lastName.isEmpty())
		{
			throw new Exception ("Error: Client missing last name.");
		}
		
		if (!ACCOUNT_NUMER_PATTERN.matcher(accountNumber).matches())
		{
			throw new Exception ("Error: Client account number format error.");
		}
		
		if (!PHONE_PATTERN.matcher(phoneNumber).matches())
		{
			throw new Exception ("Error: Client phone number format error.");
		}
		
		return true;
	}

}
