# Demo_Project_Chatting_Service
Small-showcase-project written in Java 8

# README #

### What is this repository for? ###

It is for showcase of code that I wrote. The code contains two classes - one server and one client. The server
maintains multiple clients connected to localhost on port 8000. The clients can then procced to chat using the server. The project is quite small, but is done as form of exercise so if you have some remarks, critics or advices, don't hesitate to hit me up! 

You can write to me at : mihailmilkov94@gmail.com

### How do I get set up? ###

1. Start the server.
2. Start MySQL server (It is simply used to save all incoming messages):
    * Runs on local host
    * Contains DB "chatDB"
    * "chatDB" has one table "Entry", which has 4 coloumns - inexing (PrimaryKey), name (VARCHAR), text(VARCHAR), date (TIME_STAMP)
    Note: If you want to run the server without the DB, remove the constructor of MySQLDBHANDLER in Server class and then clean the code             of any references. 
3. Start client one.
4. Start another client. 
5. Write name and message in client 1's view.
6. Write name and message in client 2's view.
7. Observer results at both views.

### Where can i find the classes? ###

The classes are in folder "src/MultiChat"

The other folder out/.../MultiChat contains the compiled classes 
