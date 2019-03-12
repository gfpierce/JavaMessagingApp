package server;

import java.net.*;
import java.io.*;
import java.util.*;

public class MessageLibraryTCPJsonRPCServer extends Thread {
    private Socket conn;
    private int id;
    private MessageLibrarySkeleton skeleton;

    public MessageLibraryTCPJsonRPCServer(Socket sock, int id, MessageLibrary msgLib) {
        this.conn = sock;
        this.id = id;
        skeleton = new MessageLibrarySkeleton(msgLib);
    }

    public void run() {
        try {
            OutputStream outSock = conn.getOutputStream();
            InputStream inSock = conn.getInputStream();
            byte clientInput[] = new byte[1024];
            int numr = inSock.read(clientInput,0,1024);
            if (numr != -1) {
                String request = new String(clientInput,0,numr);
                System.out.println("Request is: " + request);
                String response = skeleton.callMethod(request);
                byte clientOut[] = response.getBytes();
                outSock.write(clientOut,0,clientOut.length);
                System.out.println("Response is: " + response);
            }
            inSock.close();
            outSock.close();
            conn.close();
        } catch(IOException e) {
            System.out.println("I/O exception occurred for the connection: \n" + e.getMessage());
        }
    }

    public static void main (String args[]) {
        Socket sock;
        MessageLibrary msgLib = new MessageLibrary();
        int id = 0;
        int portNo = 8080;
        try {
            if (args.length >= 1) {
                portNo = Integer.parseInt(args[1]);
            }
            if (portNo <= 1024) portNo=8080;
            ServerSocket serv = new ServerSocket(portNo);
            // accept client requests. For each request create a new thread to handle
            while (true) {
                System.out.println("Student server waiting for connects on port " + portNo);
                sock = serv.accept();
                System.out.println("Student server connected to client: " + id);
                MessageLibraryTCPJsonRPCServer myServerThread = new MessageLibraryTCPJsonRPCServer(sock, id++, msgLib);
                myServerThread.start();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
