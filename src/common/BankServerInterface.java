package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface BankServerInterface extends Remote
{
	//ManagerOperationsInterface
	public Boolean createAccount(String firstName, String lastName, String address, int phone, int branch) throws RemoteException;
	public Boolean editRecord(String customerID, String fieldName, String newValue) throws RemoteException;
	public HashMap<String, Integer> getAccountCount() throws RemoteException;
	
	//ClientOperationsInterface
	public void deposit(String customerID, int amt) throws RemoteException;
	public void withdraw(String customerID, int amt) throws RemoteException;
	public void getBalance(String customerID) throws RemoteException;
}
