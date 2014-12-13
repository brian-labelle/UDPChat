import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPSocketServer {
    DatagramSocket socket = null;
    static int port = 6000;
    boolean connected = false;
    static Scanner myScan = new Scanner(System.in);
    boolean newConnectionMessage;
    boolean initial_connection = true;

    public UDPSocketServer() {

      	Thread connectionChecker = new Thread()
          {
          	public void run()
          	{
          		while(!isInterrupted())
          		{
          			try{
          				if(connected)
          				{
          					get_command_and_execute();
          				}

		          		if(!connected && !initial_connection)
		          		{
		          			boolean valid_input = false;
		          			while(!valid_input)
		          			{
		          				System.out.print("Server is not connected. Do you wish to reconnect? Enter yes or no: ");
		          				String input = myScan.nextLine();
		              			if(input.equals("yes"))
		              			{
		              				valid_input = true;
		              				createAndListenSocket();
		              				Thread.sleep(1000);
		              			}
		              			else if(input.equals("no"))
		              			{
		              				System.exit(0);
		              			}
		              			else
		              			{
		              				System.out.println("Invalid input.");
		              			}
		          				
		          			}
		          		}
          			} catch (InterruptedException e) {System.out.println(e.getMessage());}
          		}
          	}
          };
          if(!connected)
          {
        	  connectionChecker.start();
          }
    	
    }

    public void createAndListenSocket() {
      
    	 try{	
        	System.out.print("Trying to open a socket on port " + port + "...");
            socket = new DatagramSocket(port);
            System.out.println("connected.");
            connected = true;
            initial_connection = false;
            Thread.sleep(200);
            byte[] incomingData = new byte[1024];
            
            while (true) {

                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());
                System.out.println("Received message from client: " + message);
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                String reply = "Thank you for the message";
                byte[] data = reply.getBytes();
                DatagramPacket replyPacket =
                        new DatagramPacket(data, data.length, IPAddress, port);
                socket.send(replyPacket);
                get_command_and_execute();
                Thread.sleep(2000);
            }

        } catch (SocketException e) {
        	System.out.println("Couldn't connect.");
        	System.out.println(e.getMessage());
        	port+=1;
        	initial_connection = false;
        	connected = false;
        } catch (IOException i) {
            i.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	int user_port = 0;
    	System.out.print("Please enter the port to start the chat server: ");
    	
    	while(user_port == 0)
    	{
	    	try{
	    		user_port = Integer.parseInt(myScan.nextLine());
	    	} catch(NumberFormatException e)
	    	{
	    		user_port = 0;
	    		System.out.print("Please enter a valid port number: ");
	    	}
    	}
    	
    	port = user_port;
    	
    	System.out.println("Starting Server....\n");
    	
        UDPSocketServer server = new UDPSocketServer();
        server.createAndListenSocket();
    }
    
    public void get_command_and_execute() throws InterruptedException
    {
    	
    	boolean valid_input = false;
    	while(!valid_input)
    	{
    		System.out.print("Chat Server> ");
    		String command = myScan.nextLine();
    		
        	if(command.equals("exit"))
        	{
        		System.out.print("Exiting the chat server...");
        		connected = false;
        		valid_input = true;
        		socket.close();
        		System.out.println("done.");
        		Thread.sleep(200);
        	}
        	else{
        		System.out.println("Invalid input: " + command);
        	}
    	}
    	
    }
    
    class ChatUser
    {
    	InetAddress user_ip;
    	
    	public ChatUser(InetAddress ip)
    	{
    		user_ip = ip;
    	}
    }

}

