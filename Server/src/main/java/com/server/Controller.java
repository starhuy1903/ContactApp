package com.server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Server server;

    private static int SERVER_PORT = 8000;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void startServer() {
        try {
            server = new Server(new ServerSocket(SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating server");
        }
    }

}