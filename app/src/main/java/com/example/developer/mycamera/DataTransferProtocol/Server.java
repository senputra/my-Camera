package com.example.developer.mycamera.DataTransferProtocol;

/**
 * Created by developer on 11/28/2017.
 */
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server {

    Inet4Address IPAddress = null;
    int port;
    DatagramSocket serverSocket;

    public void setIPAddress (String host) throws UnknownHostException {
        IPAddress = (Inet4Address) Inet4Address.getByName(host);
    }


    public void startServer(int port) throws SocketException {
        this.port = port;
        serverSocket = new DatagramSocket(port);
    }

    //send buffer should be in byte
    public void sendBuffer(byte[] mBuffer){

        DatagramPacket sendPacket = new DatagramPacket(mBuffer, mBuffer.length, IPAddress, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            Log.d("fail to send", e.getMessage());
        }
    }
}