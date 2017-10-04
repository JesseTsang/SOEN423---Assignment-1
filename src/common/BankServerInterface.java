package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface BankServerInterface extends Remote
{
	public Boolean createAccount(String firstName, String lastName, String address, int phone, int branch) throws RemoteException;
	public Boolean editRecord(String customerID, String fieldName, String newValue) throws RemoteException;
	public HashMap<String, Integer> getAccountCount() throws RemoteException;
	
	public void deposit(String customerID, int amt) throws RemoteException;
	public void withdraw(String customerID, int amt) throws RemoteException;
	public void getBalance(String customerID) throws RemoteException;
	
	public void login(String customerID, String password) throws RemoteException;
	public void getUDPIPData(int port) throws RemoteException;
}
