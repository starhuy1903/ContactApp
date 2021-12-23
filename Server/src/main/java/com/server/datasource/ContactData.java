package com.server.datasource;

import com.datamodel.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactData {

    public static final String DB_NAME = "contact";
    public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + DB_NAME;

    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "75184641";

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";

    // SELECT * FROM contacts
    private static final String QUERY_CONTACT = "SELECT * FROM " + TABLE_CONTACTS;

    // SELECT * FROM contacts WHERE contact_id = ?
    private static final String QUERY_CONTACT_BY_ID = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_ID + " = ?";

    // SELECT * FROM contacts WHERE phone_number = ?
    private static final String QUERY_CONTACT_BY_PHONE_NUMBER = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_PHONE_NUMBER + " = ?";

    private Connection conn;

    private PreparedStatement queryContact;
    private PreparedStatement queryContactByContactId;
    private PreparedStatement queryContactByPhoneNumber;

    // Apply Singleton pattern
    private static ContactData instance = new ContactData();

    private ContactData() {

    }

    public static ContactData getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING, DB_USERNAME, DB_PASSWORD);
            queryContact = conn.prepareStatement(QUERY_CONTACT);
            queryContactByContactId = conn.prepareStatement(QUERY_CONTACT_BY_ID);
            queryContactByPhoneNumber = conn.prepareStatement(QUERY_CONTACT_BY_PHONE_NUMBER);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
     }

     public void close() {
        try {
            if(queryContact != null) {
                queryContact.close();
            }
            if(queryContactByContactId != null) {
                queryContactByContactId.close();
            }
            if(queryContactByPhoneNumber != null) {
                queryContactByPhoneNumber.close();
            }
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
     }

     public List<Contact> queryContacts() {
        try {
            ResultSet results = queryContact.executeQuery();

            List<Contact> contacts = new ArrayList<>();

            while (results.next()) {
                Contact contact = new Contact();

                contact.setId(results.getString(COLUMN_CONTACT_ID));
                contact.setName(results.getString(COLUMN_NAME));
                contact.setPhoneNumber(results.getString(COLUMN_PHONE_NUMBER));
                contact.setEmail(results.getString(COLUMN_EMAIL));
                Blob imageBlob = results.getBlob(COLUMN_PROFILE_IMAGE);
                contact.setProfileImage(imageBlob.getBytes(1, (int)imageBlob.length()));

                contacts.add(contact);
            }
            return contacts;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
     }

     public Contact queryContactByContactId(String contactId) {
        try {
            queryContactByContactId.setString(1, contactId);
            ResultSet result = queryContactByContactId.executeQuery();

            Contact contact = new Contact();
            while(result.next()) {

                contact.setId(result.getString(COLUMN_CONTACT_ID));
                contact.setName(result.getString(COLUMN_NAME));
                contact.setPhoneNumber(result.getString(COLUMN_PHONE_NUMBER));
                contact.setEmail(result.getString(COLUMN_EMAIL));

                Blob imageBlob = result.getBlob(COLUMN_PROFILE_IMAGE);
                contact.setProfileImage(imageBlob.getBytes(1, (int)imageBlob.length()));

                System.out.println(contact.getProfileImage());

            }

            return contact;

        } catch(SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
     }
}
