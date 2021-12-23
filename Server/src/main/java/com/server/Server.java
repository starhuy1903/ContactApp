package com.server;

import com.datamodel.Contact;
import com.datamodel.Message;
import com.server.datasource.ContactData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;
    private static int numberClient = 0;

    public static String CLOSE_CONNECT_MSG = "close";

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        addNewConnection();
    }

    public void addNewConnection() {
        while(true) {
            try {
                new ClientConnection(serverSocket.accept(), this).start();
                numberClient++;
            } catch (IOException e){
                System.out.println("Error adding client connection");
                e.printStackTrace();
            }
        }
    }

    public void sendContactListToClient(List<Contact> contacts, ObjectOutputStream outputStream) {
        try {
            // send contacts to the client
            outputStream.writeObject(contacts);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Error sending contact list to client");
        }
    }

    public void receiveContactIdFromClient(ClientConnection clientConnection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(clientConnection.getSocket().isConnected()) {
                    try {
                        // Read phone number from the client to search
                        // Bug here
                        Message contactId = (Message) clientConnection.getInputStream().readObject();
                        if(contactId.getText().equals(CLOSE_CONNECT_MSG)){
                            clientConnection.closeConnect();
                            break;
                        }
                        // take contact by phone number
                        Contact contact = ContactData.getInstance().queryContactByContactId(contactId.getText());
                        List<Contact> contacts = new ArrayList<>();
                        contacts.add(contact);

                        // return contact to client
                        sendContactListToClient(contacts, clientConnection.getOutputStream());
//                        Controller.addLabel("Client searched " + phoneNumber.getText(), vBox);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Receive message from client error ");
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if(this.serverSocket != null) {
                serverSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
