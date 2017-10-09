package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import domain.BranchID;

public interface BankServerInterface extends Remote
{
	//ManagerOperationsInterface
	public Boolean createAccount(String firstName, String lastName, String address, String phone, String customerID,BranchID branch) throws RemoteException;
	public Boolean editRecord(String customerID, String fieldName, String newValue) throws RemoteException;
	public HashMap<String, Integer> getAccountCount() throws RemoteException;
	
	//ClientOperationsInterface
	public void deposit(String customerID, int amt) throws RemoteException;
	public void withdraw(String customerID, int amt) throws RemoteException;
	public void getBalance(String customerID) throws RemoteException;
}
