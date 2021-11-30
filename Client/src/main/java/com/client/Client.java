package com.client;

import com.datamodel.Contact;
import com.datamodel.Message;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.out.println("Error creating client.");
            e.printStackTrace();
            closeEverything();
        }
    }

    public void sendPhoneNumberToServer(String phoneNumber) {
        try {
            // send phone number to server
            outputStream.writeObject(new Message(phoneNumber));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending phone number to server");
            closeEverything();
        }
    }

//    public void receiveContactFromServer(TableView<Contact> contactsTable) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(socket.isConnected()) {
//                    try {
//                        Contact contact = (Contact) inputStream.readObject();
//                        contactsTable.refresh();
//                        contactsTable.getItems().add(contact);
////                        System.out.println(contact.getName());
//                    } catch(IOException | ClassNotFoundException e) {
//                        e.printStackTrace();
//                        System.out.println("Error receiving contact from the server");
//                        closeEverything(socket, inputStream, outputStream);
//                        break;
//                    }
//                }
//            }
//        }).start();
//    }

    public void receiveContactListFromServer(TableView<Contact> contactsTable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(socket.isConnected()) {
                    try {
                        List<Contact> contacts = (List<Contact>) inputStream.readObject();
                        for(Contact contact: contacts) {
                            contactsTable.getItems().add(contact);
                        }
                    } catch(IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving contact from the server");
                        closeEverything();
                    }
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
