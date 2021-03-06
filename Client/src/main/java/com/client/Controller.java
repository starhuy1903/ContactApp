package com.client;


import com.datamodel.Contact;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;

public class Controller implements Initializable {

    @FXML
    private TextField tf_search;
    @FXML
    private TextField tf_port;
    @FXML
    private ImageView profileImageView;
//    @FXML
//    private ImageView contactLogo;
    @FXML
    private Button buttonConnect;
    @FXML
    private Button buttonDisconnect;
    @FXML
    private Button buttonSaveImage;
    @FXML
    private Button buttonSend;
    @FXML
    private Label notifyLabel;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private TableView<Contact> contactsTable;
    @FXML
    private ProgressBar progressBar;

//    private boolean disableButtonDisconnect;

    private Client client;

    public static String CLOSE_CONNECT_MSG = "close";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonDisconnect.setDisable(true);
        buttonSaveImage.setVisible(false);
        buttonSend.setDisable(true);
        contactsTable.setVisible(false);
        buttonConnect.setDisable(false);

    }

    @FXML
    public void connectServer() {
        String portText = tf_port.getText();
        if (!portText.isEmpty()) {
            int portNumber = Integer.parseInt(portText);
            try {
                client = new Client(new Socket("localhost", portNumber));
                System.out.println("Connected to server.");
                buttonConnect.setDisable(true);
                buttonDisconnect.setDisable(false);
                buttonSend.setDisable(false);
                profileImageView.setVisible(true);
//                contactLogo.setVisible(false);
                contactsTable.setVisible(true);

                displayNotifyMessage("Connected to server!");
            } catch (IOException e) {
                e.printStackTrace();
                displayNotifyMessage("Failed connected to the server!");
                tf_port.clear();
            }
            contactsTable.getItems().clear();
            client.receiveContactListFromServer(contactsTable, progressBar);
        }
    }

    @FXML
    public void disconnectServer() {
        contactsTable.getItems().clear();
        profileImageView.setVisible(false);
        buttonSaveImage.setVisible(false);
        contactsTable.setVisible(false);
//        contactLogo.setVisible(true);

        tf_port.clear();
        client.sendContactIdToServer(CLOSE_CONNECT_MSG);
        client.closeEverything();

        buttonConnect.setDisable(false);
        buttonDisconnect.setDisable(true);
        buttonSend.setDisable(true);

        displayNotifyMessage("Disconnected to server!");
    }

    @FXML
    public void sendContactIdToServer() {
        String messageToSend = tf_search.getText();
        if (!messageToSend.isEmpty()) {
            contactsTable.getItems().clear();
            profileImageView.setVisible(false);
            buttonSaveImage.setVisible(false);
            tf_search.clear();
            client.sendContactIdToServer(messageToSend);
            client.receiveContactListFromServer(contactsTable, progressBar);
        }
    }

    @FXML
    public void showProfileImage() {

        Contact contact = contactsTable.getSelectionModel().getSelectedItem();
        if (contact != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(contact.getProfileImage());
            Image image = new Image(bis);
            profileImageView.setImage(image);
            buttonSaveImage.setVisible(true);
            profileImageView.setVisible(true);
        }
    }

    @FXML
    public void saveImage() throws IOException {
        Contact contact = contactsTable.getSelectionModel().getSelectedItem();

        InputStream inputStream = new ByteArrayInputStream(contact.getProfileImage());

        String imageName = contact.getName().split(" ")[0];

        BufferedImage bufferedImage = ImageIO.read(inputStream);
        File saveFile = new File("/home/huy/Downloads/ContactProject/" + imageName + ".png");
        ImageIO.write(bufferedImage, "png", saveFile);
        displayNotifyMessage("Saved image successfully!");

    }

    public void displayNotifyMessage(String message) {
        notifyLabel.setText(message);
    }

}