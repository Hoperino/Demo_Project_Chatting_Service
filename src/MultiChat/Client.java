package MultiChat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
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
 * From "Introduction to Java Programming, Comprehensive Edition 10 - Y.Daniel Liang"
 *
 * This is a Client side application that is meant to be used
 * with the Server side to simulate a chatting service.
 * Each instance of this class is a Standalone app.
 * Every new instance is automatically going to connect to the Server, hence
 * before launching this app, start the server. There is no pre-set limit to the amount
 * of clients that can be accommodated at the same time. However, each client is given
 * a thread from the server. The Server-Client simulate a room where
 * many clients can communicate with each other.
 *
 * NOTE: MVC is intentionally not used, since there is only one view.
 */
public class Client extends Application{

    //set variables for connection for localhost on port 8000
    private String host = "localhost";
    private int port = 8000;
    private String name;

    //GUI elements:
    private TextField username = new TextField();                           //holds the username
    private TextField message = new TextField();                            //holds the text to send to others
    private TextArea txArea = new TextArea();                               //chat board
    private Button submit = new Button("Submit");                      //button for submit

        /*
        * Small note, the initialization is done here for one reason - ActionListener.
        * The streams of the socket are required by the constructor of the Listener.
        * Thus, this allows us to have the socket ready when the listener's constructor
        * is created.
        *
        */

    private Socket socket = new Socket(host,port);
    private ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
    private ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

    public Client() throws IOException{}                                    //This line is mandatory since the initialization throws IOException


    @Override
    public void start(Stage primaryStage) {

        /*
        * Initialize the GUI and listener for button
        * */
        GridPane pane = new GridPane();
        pane.add(new Label("Name"),0,1);
        pane.add(username,1,1);
        pane.add(new Label("Chat"),0,2);
        pane.add(message,1,2);
        pane.add(submit,1,3);
        pane.add(txArea,1,0);
        pane.setAlignment(Pos.CENTER);

        txArea.setEditable(false);                                          //The chat board should be read-only
        username.setMaxWidth(100);

        submit.setOnAction(new ButtonListener(toServer));                   //Add the listener to the button

        //GUI start
        Scene scene = new Scene(new ScrollPane(pane),550,350);
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        ExecutorService service = Executors.newCachedThreadPool();          //Execute UpdateTask in separate Thread
        Platform.runLater(()->{
                if (toServer == null)System.out.print("NIL!!!");
                service.execute(new UpdateTask(fromServer));
                service.shutdown();
        });
    }

    /*
    *   UpdateTask is a class that constantly checks the input buffer for messages from other people
    *   It essentially listens all the time while the client is connected to the server
    *   It takes as argument the InputStream of the socket
    *
    */

    private class UpdateTask implements Runnable{
       private ObjectInputStream fromServer;

        UpdateTask(ObjectInputStream in){
            this.fromServer = in;
        }

        @Override
        public void run() {
            while (true){
                try {
                    String input = (String) fromServer.readObject();        //Get update from server

                    Platform.runLater(()->{
                        txArea.appendText(input+"\n");                      //Add the update to the board
                    });

                    try {
                        Thread.sleep(1000);                            //Sleep one second
                    } catch (InterruptedException e) {
                        System.out.println("Exception InterruptedException");
                    }

                } catch (IOException e) {
                    System.out.println("Exception IOException");
                    break;                                                    //break the loop if Exception occurs
                } catch (ClassNotFoundException e) {
                    System.out.println("Exception IOException");              //break the loop if Exception occurs
                    break;
                }
            }
        }
    }

    /*
    *
    *   ButtonListener is inner class that is invoked when button Submit is clicked
    *
    */

    private class ButtonListener implements EventHandler<ActionEvent>{

        private ObjectOutputStream outputStream;

        ButtonListener(ObjectOutputStream out){

            this.outputStream = out;

        }
        @Override
        public void handle(ActionEvent e){

            /*
             * Send firs the name of the Client
             * Then send the actual text
             * After it is sent, clear the message textFields and retain username
             *
             */

            try{

                name = username.getText().trim();
                String text = message.getText().trim();
                System.out.print(name +" " +text);
                outputStream.writeObject(name);
                outputStream.flush();
                outputStream.writeObject(text);
                outputStream.flush();

                Platform.runLater(()->{
                    message.clear();
                });

            }catch (InputMismatchException ex){
                System.out.println("Exception Handler Task");
                ex.printStackTrace();
            }catch (IOException e1){
                System.out.println("Exception Handler Task");
                e1.printStackTrace();
            }
        }
    }

}
