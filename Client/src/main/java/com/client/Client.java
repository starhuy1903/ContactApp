package com.client;

import com.datamodel.Contact;
import com.datamodel.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
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

    public void sendContactIdToServer(String contactId) {
        try {
            // send phone number to server
            outputStream.writeObject(new Message(contactId));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending contact id to server");
            closeEverything();
        }
    }

    public void receiveContactListFromServer(TableView<Contact> contactsTable, ProgressBar progressBar) {
        Task<ObservableList<Contact>> task = new Task<ObservableList<Contact>>() {
            @Override
            protected ObservableList<Contact> call() throws Exception {
                if (socket.isConnected()) {
                    try {
                        List<Contact> contacts = (List<Contact>) inputStream.readObject();
                        Thread.sleep(100);
                        return FXCollections.observableArrayList(contacts);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving contact from the server");
                        closeEverything();
                    }
                }
//                Controller.displayNotifyMessage("Not found");
                return null;
            }
        };
        contactsTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(socket.isConnected()) {
//                    try {
//                        List<Contact> contacts = (List<Contact>) inputStream.readObject();
//                        for(Contact contact: contacts) {
//                            contactsTable.getItems().add(contact);
//                        }
////                        contactsTable.refresh();
//                    } catch(IOException | ClassNotFoundException e) {
//                        e.printStackTrace();
//                        System.out.println("Error receiving contact from the server");
//                        closeEverything();
//                    }
//                }
//            }
//        }).start();
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
