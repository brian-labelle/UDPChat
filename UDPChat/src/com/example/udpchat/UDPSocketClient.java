package com.example.udpchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSocketClient {
    DatagramSocket Socket;
    Buffer packet_buffer;

    public UDPSocketClient() {

    	packet_buffer = new Buffer(12000);
    	
    	Thread receiveThread = new Thread()
	    {
	    	public void run(){
	    		long starting_size = packet_buffer.getSizeInBytes();
	    		byte[] incomingData = new byte[1024];
	    		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
	            try {
					Socket.receive(incomingPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            String response = new String(incomingPacket.getData());
	            long new_buffer_size = packet_buffer.addPacket(incomingPacket, 0);
	            if(new_buffer_size > starting_size)
	            {
	            	incomingData = null;
	            	incomingPacket = null;
	            	starting_size = 0;
	            	response = "";
	            	new_buffer_size = 0;
	            }
	    	}
	    };
	    receiveThread.start();
    }

    public void createAndListenSocket() {
        try {

            Socket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("192.168.1.9");
            byte[] incomingData = new byte[1024];
            String sentence = "This is a message from client";
            byte[] data = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 6000);
            Socket.send(sendPacket);
            System.out.println("Message sent from client");
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            Socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData());
            System.out.println("Response from server:" + response);
            Socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public static void main(String[] args) {
        
    }
}
