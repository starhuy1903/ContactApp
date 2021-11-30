package com.server;

import com.server.datasource.ContactData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread{
    private Socket socket;

    private Server server;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


    public ClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            server.sendContactListToClient(ContactData.getInstance().queryContacts(), outputStream);
            server.receivePhoneNumberFromClient(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error running client connection");
            closeConnect();
        }
    }

    public void closeConnect() {
        try {
            if(this.outputStream != null) {
                this.outputStream.close();
            }
            if(this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }


}
