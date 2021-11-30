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
//    private Socket socket;
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

//    @Override
//    public void run() {
//        try {
//
//            receivePhoneNumberFromClient(vbox_messages);
//            sendContactListToClient(ContactData.getInstance().queryContacts());
//        } catch(IOException e) {
//            System.out.println("Error creating server");
//            e.printStackTrace();
//            closeEverything(socket, inputStream, outputStream);
//        }
//    }

    public void sendContactListToClient(List<Contact> contacts, ObjectOutputStream outputStream) {
        try {
            // send contacts to the client
            outputStream.writeObject(contacts);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Error sending contact list to client");
        }
    }

    public void receivePhoneNumberFromClient(ClientConnection clientConnection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(clientConnection.getSocket().isConnected()) {
                    try {
                        // Read phone number from the client to search
                        // Bug here
                        Message phoneNumber = (Message) clientConnection.getInputStream().readObject();
                        if(phoneNumber.getText().equals(CLOSE_CONNECT_MSG)){
                            clientConnection.closeConnect();
                            break;
                        }
                        Contact contact = ContactData.getInstance().queryContactByPhoneNumber(phoneNumber.getText());
                        List<Contact> contacts = new ArrayList<>();
                        contacts.add(contact);

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
