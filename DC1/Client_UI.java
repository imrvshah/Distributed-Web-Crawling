import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client_UI {
	public static void main(String[] args)
	{
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try 
		
		{
		    Socket cliSocket = new Socket(hostName, portNumber);
		    
		    PrintWriter out = new PrintWriter(cliSocket.getOutputStream(), true);
		    
		    InputStreamReader is = new InputStreamReader(cliSocket.getInputStream());
		    BufferedReader in = new BufferedReader(is);
		    
		    String fromServer = "";

		    // read line from server
		    while ((fromServer = in.readLine()) != null) 
		    {
		    	
	    		System.out.println("Server: " + fromServer);
		        
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
			        Scanner s = new Scanner(System.in);
			        String fromUser = s.nextLine();
			        
			        if (fromUser != null) 
			        {
			            System.out.println("Client: " + fromUser);
			            // send message to server
			            out.println(fromUser);
			        }
		        }
		        else
		        {
	
		        	Scanner s = new Scanner(System.in);
			        String fromUser = s.nextLine();
			        
			        if (fromUser != null) 
			        {
			            System.out.println("Client: " + fromUser);
			            // send message to server
			            out.println(fromUser);

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
