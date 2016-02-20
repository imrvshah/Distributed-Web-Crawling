import java.util.*;
public interface Server_RMI extends java.rmi.Remote{
	int ping() throws java.rmi.RemoteException;
	Map<String, Integer> get_topWords() throws java.rmi.RemoteException;
	String[] search(String query) throws java.rmi.RemoteException;
}
