package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import domain.BranchID;
import domain.Client;
import domain.EditRecordFields;

public interface BankServerInterface extends Remote
{
	//ManagerOperationsInterface
	public Boolean createAccount(String firstName, String lastName, String address, String phone, String customerID, BranchID branch) throws RemoteException;
	public Boolean editRecord(String customerID, EditRecordFields fieldName, String newValue) throws RemoteException;
	public HashMap<String, String> getAccountCount() throws RemoteException;
	
	//ClientOperationsInterface
	public void deposit(String customerID, double amt) throws RemoteException;
	public void withdraw(String customerID, double amt) throws RemoteException;
	public double getBalance(String customerID) throws RemoteException;
	
	//MiscOperations
	public int getLocalAccountCount() throws RemoteException;
	
	//CoreOperations
	public Client retrieveAccount(String customerID) throws RemoteException;
	public void getUDPData(int port) throws RemoteException;
}
