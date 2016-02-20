import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;
import java.util.Map.*;

public class Server_1 {
	// number of threads in the pool
	private static final int NO_THREADS = Integer.MAX_VALUE;
	//public static String[] MyTopWords = new String[50];
	Map<String, Integer> TopWords_s2 = new HashMap<String, Integer>();
	int serverno=4;
	int noOfServer=5;
	static int servernum=4;
	static Server_RMI_impl server1 = null;
	static List<Server_RMI> remoteServers = new ArrayList<Server_RMI>();
	static ArrayList<HashMap<String, Integer>> wordsArray = new ArrayList<HashMap<String, Integer>>();

	public Server_1(){};
	public void Fill_Index(Spider spider)
	{
		String dir = "www.washington.edu";

		Map<String, Integer > word_count =new HashMap<String, Integer>();
		try
		{
			File folder = new File(dir);
			/*	
			if(!folder.exists())
				System.out.println(dir+ " File/Folder doesnt exist.");
			if(folder.isDirectory())
				System.out.println(dir+" is Folder.");
			if(folder.isFile())
				System.out.println(dir+" is File.");
			*/			
			File[] listOfFiles = folder.listFiles();
			/*
			if(listOfFiles == null)
				System.out.println("No " + dir+"... at: "+ System.getProperty("user.dir"));
			else
				System.out.println(dir + " at: "+ System.getProperty("user.dir"));
			*/
		    for (int i = 0; i < listOfFiles.length; i++) 
		    {
		    	if (listOfFiles[i].isFile()) 
		    	{
		    		String filename = listOfFiles[i].getName();
		    		String Fullname = dir+"/" + filename;
		    		BufferedReader br = new BufferedReader(new FileReader(Fullname));
		    		String line = "";
		    		int line_no = 0;
		    		String url = "";
		    		while ((line = br.readLine()) != null) 
		    		{
		    			if(line_no == 0)
		    			{
		    				url = line;
		    				line_no ++;
		    				continue;
		    			}
		    			line_no++;
		    			String[] words = line.split("\\t");
					
					if (words.length > 1) {		    			
		    			if (word_count.containsKey(words[0]))
		    			{
		    				Integer fr = word_count.get(words[0]);
		    				Integer c = Integer.parseInt(words[1]);
		    				Integer total = fr + c;
		    				word_count.put(words[0], total);
		    			}
		    			else
		    			{
		    				Integer c = Integer.parseInt(words[1]);
		    				word_count.put(words[0], c);
		    			}
		    			
		    			StringBuilder sb = new StringBuilder(url);
					sb.append("|"+words[1]+";");
		    			if (spider.searchResult.containsKey(words[0]))
		    			{
		    				StringBuilder sb1 = spider.searchResult.get(words[0]);
						sb1.append(sb);
						spider.searchResult.put(words[0], sb1);
//						System.out.println(words[0]);
	//					System.out.println(sb1);
		    			}
		    			else
		    			{
		    				spider.searchResult.put(words[0], sb);
		    			}
		    			}

		    		}
		    	} 
		    	else if (listOfFiles[i].isDirectory()) 
		    	{	//System.out.println("Directory " + listOfFiles[i].getName());
		    	}
		    }
			System.out.println("Index Filled...");
			Comparator<WordCount> comparator = new WordCountComparator();
            		PriorityQueue<WordCount> queue = new PriorityQueue<WordCount>(word_count.size(), comparator);
            		for (Entry<String, Integer> entry : word_count.entrySet()) 
            		{
                		WordCount wc = new WordCount(entry.getKey().toString(), entry.getValue());
                		queue.add(wc);
            		}
            		for (int c  = 0; c < 50; c++ ) 
            		{
                		WordCount wc = queue.poll();
                		server1.MyTopWords.put(wc.word, c);
				System.out.println(wc.word + "-"+c);
            		}

			System.out.println("Top50 found...");
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException |NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			
		}
		

	}
 	public class WordCount
    	{
        	String word;
        	int count;
        		WordCount(String s,Integer c)
        	{
            	word = s;
            		count = c;
        	}
    	}
    	public class WordCountComparator implements Comparator<WordCount>
    		{
        	@Override
        	public int compare(WordCount x, WordCount y)
        	{
            		if (x.count <= y.count)
            		{
                		return 1;
            		}
            		else
            		{
                		return -1;
            		}
        	}
    	}    	
	public class ServerRunnable implements Runnable 
	{
		Spider spider;
		
		public ServerRunnable(Spider s) {
			spider = s;
		}

		public void run()
		{
			//System.out.println("Server Communication Thread...");
			try
			{
//				System.out.println("Creating a Server1!");
				String name = "//127.0.0.1:9002/Server_"+serverno;
				server1 = new Server_RMI_impl(serverno,spider);

				// filling index and finding top50
				Server_1 this_s = new Server_1();
				this_s.Fill_Index(spider);

				//System.out.println("Server"+serverno+": binding it to name: " +name);
				Naming.rebind(name, server1);
				System.out.println("Server"+serverno+" Ready!");
			}
			catch (Exception e){
				//System.out.println("Exception: " + e.getMessage());
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (int i = 1; i <noOfServer+1; i++) {
				
				if(i!=serverno)
				{
					Server_RMI rmi=null;
					try
					{
						
						String name2 ="//127.0.0.1:9002/Server_"+i;
						
						rmi= (Server_RMI) Naming.lookup(name2);
						//System.out.println("Server"+i+": binding it to name: " +name2);
						// I think it is for pinging to other server
						if(rmi.ping() != i)
							System.out.println("Erorr: wrong server Id...");
						else
							System.out.println("Connected to server"+i+".");
						System.out.println("Got object of server"+i);
						remoteServers.add(rmi);
					}
					catch (Exception  e){
						//System.out.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
						try
						{
							Map<String, Integer> TopWords_s2;
							while(true)
							{
								TopWords_s2 = rmi.get_topWords();
										
						
								if(TopWords_s2 == null)
								{
								Thread.sleep(5000);
								}
								else
								{
									//System.out.println("Received Top50 words of server"+i);
									//System.out.println(wordsArray.size());
									wordsArray.add( (HashMap<String, Integer>) TopWords_s2);
									//System.out.println(wordsArray.size());

									break;
								}
					}
				}
				catch(Exception e)
				{
					//System.out.println("Exception: " + e.getMessage());
					e.printStackTrace();

				}
				// RMI to send MyTopwords
			}
			}
		}
	}


	public class ClientRunnable implements Runnable {
		// client socket
		Spider spider;
	
		public ClientRunnable(Spider s) {
			spider = s;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//System.out.println("Client Communication Thread...");
	    
			// get port number		
			int portNumber = 4000+servernum;
			ServerSocket serverSocket;
			
			ExecutorService executor = Executors.newFixedThreadPool(NO_THREADS);

			try
			{
				// create server socket 
				serverSocket = new ServerSocket(portNumber);
				int count= 0;
				while(true)
				{
					//System.out.println("Waiting for Client Query.");
					// wait for client to connect to server socket
					Socket clientSocket = serverSocket.accept();
					// create Runnable class object with new client socket
					Runnable r = new MyRunnable(clientSocket, spider);

					//Thread myT = new Thread(r);
					//myT.start();
					
					// execute a thread for new client
					executor.execute(r);
					
					count++;
					if(count >= 1000)
						break;
					
				}

				executor.shutdown();

				// Wait until all threads are finish
				while (!executor.isTerminated()) {}
				
				// close server socket
				serverSocket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}		
			finally{ 
				//do nothing
			}

		}
	}
	public void init(Spider spider)
	{

		final CyclicBarrier gate = new CyclicBarrier(3);
		
		Runnable r = new ClientRunnable(spider);
		Thread ServerComm = new Thread(new ServerRunnable(spider));
	    Thread ClientThread = new Thread(r);
		
//	    Server_1 this_s = new Server_1();
	//	this_s.Fill_Index(spider);
	    
	    // both threads will start at same time
		ServerComm.start();
		ClientThread.start();
	}
	public class MyRunnable implements Runnable {
		// client socket
		Socket clientSocket;
		Spider spider;
		Map<String, Integer> duplicate = new HashMap<String, Integer>();
		Map<String,PriorityQueue<WordCount>> results = new HashMap<String, PriorityQueue<WordCount>>();

	
		public MyRunnable(Socket clSoc, Spider spider) {
			clientSocket = clSoc;
			this.spider = spider;
		}
		public void run()
		{
			String line = "";
			try
			{
				OutputStream os = clientSocket.getOutputStream();
				PrintWriter out = new PrintWriter(os, true);
	
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
				// ask for log in or Registration
				String initLine = "Please Enter Query String:";
				out.println(initLine);
				String[] words={};
				while(true)
				{
					if((line = in.readLine()) != null )
					{
						words = line.split(" ");
						//System.out.println("Query: "+words.length);
						break;
					}
					else
						out.println("Enter valid Query: ");
				}
// *
                                int n = words.length;
                                int N=1;
                                if (n>2)
                                        N = n + (n-1) + (n-2);
                                else if (n==2)
                                        N = n + (n-1);
                                String[] QueryStrings = new String[N];
                                int i=0;

                                for(int j = 0;j < n-2 ;j++)
                                {
                                        QueryStrings[i] = words[j]+" "+words[j+1]+" "+words[j+2];
                                        i++;
                                }
                                for(int j = 0;j < n-1 ;j++)
                                {
                                        QueryStrings[i] = words[j]+" "+words[j+1];
                                        i++;
                                }

                                for(int j = 0; j < n ;j++)
                                {
                                        QueryStrings[i] = words[j];
                                        i++;
                                }
				//ArrayList missed_servers = new ArrayList();
				for(int j = 0 ;j <= (QueryStrings.length-1);j++)
				{
                                        String query = QueryStrings[j];
                                        query = query.toLowerCase();
					if (query.length() < 4) continue;
					LinkedList ll = LookUp_servers(query);
                                        //missed_servers.add(ll);
					String[] resultm = spider.search(query);
					if (resultm!=null)
						maintainResults(resultm,query);
                                        else
                                                Broadcast_query(query,ll);
				}
				StringBuilder sb=new StringBuilder();
				for(int j = 0 ;j <= (QueryStrings.length-1);j++)
				{
                                        String query = QueryStrings[j].toLowerCase();
					if (results.containsKey(query)) {
						PriorityQueue<WordCount> queue = results.get(query);
						WordCount wc=null;
						while ( (wc = queue.poll()) != null ) {
							sb.append("Word = "+query+", Link = "+wc.word+ ", count = "+wc.count+ ";");
						}
					}
				}

				
				// t(th) result
				int t = 0;

				String[] string_results = sb.toString().split(";");
				if(!sb.toString().isEmpty() )
                                        out.println(string_results[0]);
                                else
				{
                                        out.println("No results found for the Search Query!");
					line = in.readLine();
					out.println("Exit.");
				}
				while((line = in.readLine()) != null )
				{
					t++;
					if(line.equals("y") || line.equals("yes") || line.equals("Yes") || line.equals("Y") || line.equals("YES"))
					{
						if(t < string_results.length)
							out.println(string_results[t]);
						else
							out.println("No more results found!");
					}
					else
						break;
					// there are 5 pages of answers
				//	if(t > 5)
				//		break;
				}
				out.println("Exit.");
			}
			catch (IOException e) {
				e.printStackTrace();
			}		
			finally{ 
				//do nothing
				results.clear();
				duplicate.clear();
			}
		}

		private boolean Broadcast_query(String query, LinkedList ll) throws RemoteException
        	{
                	try{
				for(int i=0;i<ll.size();i++)
				{
					int id =(int) ll.getFirst();
					ll.removeFirst();
		
					Server_RMI rmiobject= remoteServers.get(id);
					String[] resultm= rmiobject.search(query);
	
					if(resultm != null)
							maintainResults(resultm,query);
		
				}
			}
	
			catch (Exception e){
					//System.out.println("Exception: " + e.getMessage());
				e.printStackTrace();
			}
			return true;
        	}

		private LinkedList LookUp_servers(String query) throws RemoteException
        	{
					// Need to define priority
				int rank=51;
			LinkedList ll = new LinkedList();
				try{
	
					if (server1.MyTopWords.containsKey(query))
					rank= server1.MyTopWords.get(query);
				
				for(int i=0;i<wordsArray.size();i++)
				{
					
					Map<String,Integer> serverMap= wordsArray.get(i);
					if(serverMap.containsKey(query) && rank>=serverMap.get(query))
						{
					 	Server_RMI rmiobject= remoteServers.get(i);
					 		String[] resultm= rmiobject.search(query);
					 	maintainResults(resultm,query);
					}
						else
					{
						ll.addFirst(i);
					}
						
				}
			}
					
			catch (Exception e){
				//System.out.println("Exception: " + e.getMessage());
				e.printStackTrace();
				}
			return ll;
		}
		
		public void maintainResults(String[] resultm , String query)
		{
			try{
				for (String  str : resultm) {
					String[] s=str.split("\\|");
						if(!duplicate.containsKey(s[0]))
					{
						try {
						String[] s1=query.split(" ");
							duplicate.put(s[0], s1.length);
						if(!results.containsKey(query))
						{
							Comparator< WordCount> comparator= new Server_1().new WordCountComparator();
							PriorityQueue<WordCount> queue = new PriorityQueue<WordCount>(10,comparator);
								int c1=Integer.parseInt(s[1]);
							queue.add(new Server_1().new WordCount(s[0], c1));
							results.put(query, queue);
						}
						else
							{
							PriorityQueue<WordCount> queue = results.get(query);
							int c1=Integer.parseInt(s[1]);
							queue.add(new Server_1().new  WordCount(s[0], c1));
							results.put(query, queue);
							}
						}
						catch (NumberFormatException e) {}
						catch (ArrayIndexOutOfBoundsException e) {}
						}
				}
			}
			catch (Exception e){
				//System.out.println("Exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
	}

}
