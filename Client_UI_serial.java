import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client_UI {

	static String hostName = null;
	static int portNumber = 0;
	static String query = "campus map";
	static long total_time = 0L;	
	public static void main(String[] args)
	{
		final int NO_CLIENTS = Integer.parseInt(args[2]);
		hostName = args[0];
		portNumber = Integer.parseInt(args[1]);
		
		for(int i =0; i < NO_CLIENTS ;i ++)
		{
			call_query();
		}
		
		double avg = (double)total_time / (double)NO_CLIENTS;
		System.out.println("Average Query Time:"+avg+"nano-Sec");			

	}
	public static void call_query()
	{
		try 
	
		{
		    Socket cliSocket = new Socket(hostName, portNumber);
		    
		    PrintWriter out = new PrintWriter(cliSocket.getOutputStream(), true);
		    
		    InputStreamReader is = new InputStreamReader(cliSocket.getInputStream());
		    BufferedReader in = new BufferedReader(is);
		   
		    long StartTime = 0L;
		    long EndTime=0L;
		    long diff=0L;
		    String fromServer = "";

		    // read line from server
		    if((fromServer = in.readLine()) != null) 
		    {
			
	  //  		System.out.println("Server: " + fromServer);
			
	    		// if server says WelCome, user successfully logged in
	    		if (fromServer.equals("Exit."))
			{
				//close the socket and return
				cliSocket.close();
				return;
			}
	    		// if server says exit, return from the function end of client program
			else if(fromServer.equals("Please Enter Query String:"))
			{
	    		
		    		// read line from user to reply the server's question
				//Scanner s = new Scanner(System.in);
				//String fromUser = s.nextLine();
				String fromUser = query;	
				if (fromUser != null) 
				{
	//			    System.out.println("Client: " + fromUser);
				    // send message to server
				    
					StartTime = System.nanoTime();
					out.println(fromUser);
					if((fromServer = in.readLine()) != null)
						EndTime = System.nanoTime();
					diff = EndTime - StartTime;
					total_time += diff;
				}
			}

		    }
		}
		catch(Exception e)
		{
		
		}
		finally {}

	}

}
