package com.task2_3.client;

public class Start {
    public static void main (String[] args) {
        System.out.println("Hello my baby!");
        Admin_Protocol_Client client = new Admin_Protocol_Client("localhost",2020);
        client.startAuthHandshake("admin","ciaccio");
        Admin_Protocol_Client client2 = new Admin_Protocol_Client("localhost",2020);
        Admin_Protocol_Client client3 = new Admin_Protocol_Client("localhost",2020);
        client2.startAuthHandshake("admin","ciaccio");
        client3.startAuthHandshake("admin","ciaccio");
        client.requestCheckout();
    }
}
