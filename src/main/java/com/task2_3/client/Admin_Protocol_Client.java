package com.task2_3.client;
import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.*;

/**
 * This class implements an Admin-Protocol client, making available all the services of the protocol
 */
public class Admin_Protocol_Client implements AutoCloseable {
    private String host;
    private int port;
    SSLSocket socket;
    PrintWriter out;
    BufferedReader in;

    /**
     * Generate an SSL socket to the host and port specified in the constructor.
     * @return the generated socket
     */
    private SSLSocket getSocket() {
            try {
                SSLContext context;
                KeyManagerFactory kmf;
                KeyStore ks;
                char[] storepass = "password".toCharArray();
                char[] keypass = "password".toCharArray();
                String storeName = "./keychain/truststore.jks";
                context = SSLContext.getInstance("TLS");
                kmf = KeyManagerFactory.getInstance("SunX509");
                FileInputStream fin = new FileInputStream(storeName);
                ks = KeyStore.getInstance("JKS");
                ks.load(fin, storepass);

                kmf.init(ks, keypass);
                context.init(kmf.getKeyManagers(), null, null);
                SSLSocketFactory factory = context.getSocketFactory();

                //SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();

                SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
                //initiating SSL handshake
                socket.startHandshake();
                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream()
                                )
                        )
                );

                in = new BufferedReader(
                     new InputStreamReader(
                             socket.getInputStream()
                     )
                );

             return socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initiate the login handshake with the server.
     * Note that if this doesn't return 0, the instance of this class must be closed and reinitialized because
     * the server will close its side of the socket.
     * @param user username
     * @param psw password
     * @return 0 if authentication was successful, 1 if credentials were wrong, 2 if admin is already logged,
     * -1 if zan unknown error occurred
     */
    public int startAuthHandshake(String user, String psw) {
        String request = "Auth " + user + " " + psw;
        if(!sendMessage(request))
            return -1;

        /* read response */
        String inputLine;

        try {
            //blocking read
            inputLine = in.readLine();
            if (inputLine.equals("Auth successful"))
                return 0;
            else if (inputLine.startsWith("Auth denied errcode")) {
                if (inputLine.endsWith("1"))
                        return 1;
                if (inputLine.endsWith("2"))
                        return 2;
            }
            else return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Send a checkout request to the server.
     * @return true if the request was correctly sent and acknowledged by the server
     */
    public boolean requestCheckout() {
        if(!sendMessage("Request checkout")) {
            return false;
        }
        if(!readAndCheck("Ack checkout")) {
            System.err.println("Something went wrong with checkout request");
            return false;
        }
        return true;
    }

    /**
     * Send a credentials request to the server.
     * @param user the new admin username
     * @param psw the new admin password
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestCredentials(String user, String psw) {
        if(user.contains(",") || psw.contains(",")) {
            System.err.println("credentials format not valid.");
            return false;
        }
        if(!sendMessage("Request credentials "+user+","+psw)) {
            return false;
        }
        if(!readAndCheck("Ack credentials")) {
            System.err.println("Something went wrong with credentials request");
            return false;
        }
        return true;
    }

    /**
     * Send an update request to the server.
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestUpdate() {
        if(!sendMessage("Request update")) {
            return false;
        }
        if(!readAndCheck("Ack update")) {
            System.err.println("Something went wrong with update request");
            return false;
        }
        return true;
    }

    /**
     * Send a scrape request to the server.
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestScrape() {
        if(!sendMessage("Request scrape")) {
            return false;
        }
        if(!readAndCheck("Ack scrape")) {
            System.err.println("Something went wrong with scrape request");
            return false;
        }
        return true;
    }

    /**
     * Send a replicas request to the server.
     * @param level the replication level to be set (values between 1 and 3)
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestReplicas(int level) {
        if(level < 1 || level > 3) {
            System.err.println("level not in the correct range");
            return false;
        }
        if(!sendMessage("Request replicas "+level)) {
            return false;
        }
        if(!readAndCheck("Ack replicas")) {
            System.err.println("Something went wrong with replicas request");
            return false;
        }
        return true;
    }

    /**
     * Send a year request to the server.
     * @param year the starting year of the database.
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestYear(int year) {
        if(year < 1970 || year > 2019) {
            System.err.println("year not in the correct range");
            return false;
        }
        if(!sendMessage("Request year "+year)) {
            return false;
        }
        if(!readAndCheck("Ack year")) {
            System.err.println("Something went wrong with year request");
            return false;
        }
        return true;
    }

    /**
     * Send a limit request to the server.
     * @param limit the storage limit (in Gigabytes) of the database.
     * @return true if the request was correctly sent and acknowledged by the server.
     */
    public boolean requestLimit(int limit) {
        if(limit < 1) {
            System.err.println("limit cannot be lower than 1 GB");
            return false;
        }
        if(!sendMessage("Request limit "+limit)) {
            return false;
        }
        if(!readAndCheck("Ack limit")) {
            System.err.println("Something went wrong with limit request");
            return false;
        }
        return true;
    }


    /**
     * Send a message through the socket.
     * @param msg the message to be sent
     * @return
     */
   private boolean sendMessage(String msg) {
       out.println(msg.toCharArray());
       out.flush();
       if (out.checkError()) {
           System.err.println(
                   "SSLSocketClient:  java.io.PrintWriter error");
           return false;
       }
       return true;
   }

    /**
     * Read a line from the socket and check if it's equal to the parameter.
     * Always true if the parameter is null.
     * @param comparison the check string
     * @return true if the string read is equal to the parameter
     */
   private boolean readAndCheck(String comparison) {
        String inputLine;
       try {
           //blocking read
           inputLine = in.readLine();
           if ((comparison == null) || inputLine.equals(comparison))
               return true;
           else return false;
       } catch (IOException e) {
           e.printStackTrace();
       }
        return false;
   }

    /**
     * Close an Admin_Protocol_Client.
     */
   public void close() {
       try {
           in.close();
           out.close();
           socket.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


    /**
     * constructor for the class.
     * @param host the host name of the server
     * @param port the port of the server
     */
    Admin_Protocol_Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = getSocket();
    }
}
