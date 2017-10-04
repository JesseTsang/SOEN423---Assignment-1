package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientOperationInterface extends Remote
{
	//ClientOperationsInterface
	public void deposit(String customerID, int amt) throws RemoteException;
	public void withdraw(String customerID, int amt) throws RemoteException;
	public void getBalance(String customerID) throws RemoteException;
}
