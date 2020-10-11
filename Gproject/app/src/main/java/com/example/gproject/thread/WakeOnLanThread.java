package com.example.gproject.thread;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class WakeOnLanThread extends Thread {
    String ipAddress; // ex) 127.0.0.1
    String macAddress; // ex) a1:32:53:b2
    int port;

    public WakeOnLanThread(String ipAddress, String macAddress, int port) {
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.port = port;
        Log.d(getClass().getName(), "ip: " + ipAddress);
        Log.d(getClass().getName(), "mac: " + macAddress);
    }

    @Override
    public void run() {
        byte[] macAddressByte = addressToByte(this.macAddress);
        byte[] wolPacket = new byte[6 + 16 * 6]; // 전송할 packet
        // wol packet의 맨 앞 6bytes는 ff 로 채워야함
        for (int i = 0; i < 6; i++) {
            wolPacket[i] = (byte) 0xff;
        }
        // wol packet에 7번째 byte부터 mac address를 16번 반복하여 붙여넣는다
        for (int i = 6; i < wolPacket.length; i += 6) {
            System.arraycopy(macAddressByte, 0, wolPacket, i, macAddressByte.length);
        }

        // 패킷을 전송할 주소를 구한다
        InetAddress address = null;
        try {
            address = InetAddress.getByName(this.ipAddress);
        } catch (UnknownHostException e) {
            // ipAddress 올바르지 않은 경우에 대한 에러 처리
            e.printStackTrace();
        }

        // UDP 통신으로 패킷을 전송한다.
        DatagramPacket packet = new DatagramPacket(wolPacket, wolPacket.length, address, this.port);
        try {
            DatagramSocket socket = new DatagramSocket();
            Log.d("data", Arrays.toString(packet.getData()));
            Log.d("port", String.valueOf(packet.getPort()));
            Log.d("address", String.valueOf(packet.getAddress()));
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // String으로 입력받은 mac address를 byte로 변환
    private byte[] addressToByte(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split(":");
        if (hex.length != 6) {
            Log.d(getClass().getName(), macStr);
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}