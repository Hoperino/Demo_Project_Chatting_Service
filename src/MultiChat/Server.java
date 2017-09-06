package MultiChat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mihail Milkov on 22/08/2017.
 * This project is the answer to :
 *
 *          31.10 (Multiple client chat) Write a program that enables any number of clients to
 *          chat. Implement one server that serves all the clients, as shown in Figure 31.22.
 *          Name the client Exercise31_10Client and the server Exercise31_10Server.
 *          (In our case simply "Server" and "Client")
 *
 * From "Introduction to Java Programming, Comprehensive Edition 10" - Y.Daniel Liang
 *
 * The aim of this server is to facilitate multiple clients that can chat among them.
 * The clients run client-side software that serves as a view/controller
 * The server is build by providing a new Thread to each incoming client.
 * Whenever input is provided from one client, it is distributed
 * to the output stream of the others. This means that the client-side should implement format check before sending.
 * The server is simply built, therefore restraints and complexity are left for the client.
 *
 * NOTES: To keep track of clients, ArrayList is used. The method that accesses the list is synchronized. In addition,
 * I have included small MySQL server that can be used to store all of the chatting data. To handle connection and
 * interface with the DB server, I have created MySQLDBHandler.java that is used in the ClientHandler class to first record
 * all of the received data and then disseminate it to other clients.
 *
 * The server listens on port 8000
 *
 * For further questions please write to mihailmilkov94@gmail.com
 *
 * This Application is meant to be taken as an example and a small demonstration of how a simple, back-end server
 * can be used for chatting service.
 *
 * All Rights Reserved.
 */
public class Server extends Application {


    //Set up the database handler
    /*
     *  Assume that a MySQL server is running on localhost and has a database called "chatDB"
     *  The username is "root" and there is no password
     *
     *  Note: I used WAMPSERVER
     */

    private MySQLDBHandler dbHandler = new MySQLDBHandler("chatDB","root","");

    private ArrayList<ChatClientHandler> clients = new ArrayList<>();       // Create list of all clients

    private TextArea txArea; //this it the area if the server terminal

    @Override
    public void start(Stage primaryStage) {

        /*
         *      Alternatively, the GUI can be skipped and use the
         *      terminal, however, I find this easier to read.
         *
         */

        txArea = new TextArea();
        Scene scene = new Scene(new ScrollPane(txArea),450,200);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(()->{            //Anonymous Runnable object

            try {
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(()->{
                    txArea.appendText("Server started at: "+ new Date()+"  Listening on port: "+ serverSocket.getLocalPort()+"\n");
                });

                dbHandler.cleanDB();                    //reset DB

                while (true){
                    Socket socket = serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ExecutorService service = Executors.newCachedThreadPool();
                    InetAddress address = socket.getInetAddress();
                    Platform.runLater(()->{
                        txArea.appendText("Client: " + address.getHostAddress() +" connected on port: " + socket.getPort()+"\n");
                        try {

                            clients.add(new ChatClientHandler(in,out));     //Add the new client to the list

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        service.execute(clients.get(clients.size()-1));
                        service.shutdown();

                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /*
     *      Inner class, used to handle the client's server connection
     *      Each handler, handles one client and is responsible for receiving input from it.
     *      When input is received from the corresponding client, other client receive it as well
     *      to update their view.
     */

    private class ChatClientHandler implements Runnable{
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private String name;


         ChatClientHandler(ObjectInputStream in,ObjectOutputStream out) throws IOException {
            inputStream = in;
            outputStream = out;

        }

        /*
         * Expect input from Client:
         * 1.Name
         * 2.String to display
         */

        @Override
        public void run() {

            //Waiting for input of name and text!
            while (true){
                try {

                    String input1 = (String)inputStream.readObject(); //get name

                    name = input1;
                    String input2 = (String) inputStream.readObject();//get text
                    System.out.println("Got text  "+ name + ": "+input2);

                    String sql = "INSERT INTO Entry(name,text) VALUES('" +name+"'" +",'"+input2+"');";
                    System.out.println(sql);
                    //First Insert into DB the received text
                     dbHandler.connect();
                     dbHandler.setterQuerrie(sql);                          //add entry to table
                     dbHandler.closeConnection();

                    //Second Update other clients

                    updateBoardAll(name+" : "+input2); // Update

                    Platform.runLater(()->{
                        txArea.appendText("Client "+ name +"  sent text!"+"\n");    //Write on server that a message was passed
                    });

                } catch (IOException e) {
                    System.out.println("ClientHandler IOException");
                    break;

                } catch (ClassNotFoundException e) {
                    System.out.println("ClientHandler ClassNotFoundException");
                    break;
                } catch (SQLException e) {
                    System.out.println("ClientHandler SQL connection exception");
                    break;
                }
            }

        }
        public String getName(){
            return name;
        }

        public ObjectOutputStream getDataOutputStream(){
            return outputStream;
        }

    }


    /*
     *      Sends a string input to every single OutputStream of every client
     *      that is part of the chat.
     *
     *      Method is synced since ArrayList is not Thread-safe
     *
     *      NOTE: The ArrayList is not accessed anywhere else in the server, hence no need to sync the list itself
     *
     */

    private synchronized void updateBoardAll(String newString) throws IOException {
        for (ChatClientHandler x: clients
             ) {

            ObjectOutputStream out = x.getDataOutputStream();
            out.writeObject(newString);
            out.flush();

        }

    }
}
