package MultiChat;

import java.sql.*;

/**
 * This class is responsible for MYSQL DB connection handling.
 * It connects to the DB and allows for execution of querries
 */
public class MySQLDBHandler {

    //Declare db parameters
    private String username;
    private String password;
    private String dbURL;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public MySQLDBHandler(){
        dbURL = "jdbc:mysql://localhost/test";                                      //Generic db connection
        username = "root";
        password = "";
        connection = null;
        resultSet = null;
    }

    public MySQLDBHandler(String dbName, String username, String password){
        dbURL = "jdbc:mysql://localhost/"+dbName;                                   //Specific constructor
        this.username = username;
        this.password = password;
        connection = null;
        resultSet = null;
    }

    //get the result
    public ResultSet getResultSet(){
        return resultSet;                                                           //Access the result
    }

    //Establish connection to the database
    public void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");     //load driver
            System.out.println("Driver loaded!");
            connection = DriverManager.getConnection(dbURL,username,password);
            System.out.println("Connection established!");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Select Querrie
    public void selectQuerrie(String querrie) throws SQLException {
        resultSet = statement.executeQuery(querrie);
        System.out.println("Results have been updated!");

    }

    //Update,Insert Querrie
    public int setterQuerrie(String querrie) throws SQLException{
        return statement.executeUpdate(querrie);
    }


    //is there an active connection
    public boolean isConnected(){
        if (connection != null)return true;
        else return false;
    }

    //flush clean the DB
    public void cleanDB() throws SQLException{
        this.connect();
        DatabaseMetaData dbMetaData = connection.getMetaData();
        ResultSet resultSetMeta = dbMetaData.getTables(null,null,null, new String[]{"TABLE"});

        //For every table found, truncate it!
        while (resultSetMeta.next()){
            String nameTable = resultSetMeta.getString("TABLE_NAME");
            System.out.println("Cleaned table : "+nameTable);
            setterQuerrie("truncate"+" "+nameTable +";");
        }
        this.closeConnection();
    }

    //close connection
    public void closeConnection() throws SQLException {
        connection.close();
        connection = null;
        resultSet = null;
        statement = null;
        System.out.println("Connection closed!");
    }

}
