package common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ManagerOperationImpl extends UnicastRemoteObject implements ManagerOperationInterface 
{
	private HashMap<String, ClientOperationInterface> totalAccountCount = new HashMap<String, ClientOperationInterface>();
	
	protected ManagerOperationImpl() throws RemoteException 
	{
		super();
	}

	@Override
	public Boolean createAccount(String firstName, String lastName, String address, int phone, int branch) throws RemoteException 
	{
		return null;		
	}

	@Override
	public Boolean editRecord(String customerID, String fieldName, String newValue) throws RemoteException 
	{
		
		return null;
	}

	@Override
	public HashMap<String, Integer> getAccountCount() throws RemoteException 
	{
		return null;
	}
}
