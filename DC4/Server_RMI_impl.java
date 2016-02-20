import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server_RMI_impl extends UnicastRemoteObject implements Server_RMI{

        public static Map<String, Integer> MyTopWords = new HashMap<String, Integer>();
	int Server_ID;
	Spider spider;
			
	protected Server_RMI_impl(int id,Spider s) throws RemoteException {
		super();
		Server_ID = id;
		spider = s ;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int ping() throws RemoteException {
		// TODO Auto-generated method stub
		return Server_ID;
	}

	@Override
	public Map<String, Integer>  get_topWords() throws RemoteException {
		// TODO Auto-generated method stub
		 if(!MyTopWords.isEmpty()) { 
                        return MyTopWords;
                 }
                 else
                         return null;
	}

	@Override
	public String[] search(String query) throws RemoteException {
		// TODO Auto-generated method stub

		return spider.search(query);
	}
}
