package com.company;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int PACKET_SIZE = 1024;
    private static final int PORT = 52522;

    public static void main(String[] args) {
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(PORT);
            while (true) {
                ArrayList<byte[]> datagram;
                byte[] size = new byte[1];
                DatagramPacket packet = new DatagramPacket(size, size.length);
                datagramSocket.receive(packet);
                int length = intFromBytes(size);
                byte[] buffer = new byte[length];
                packet = new DatagramPacket(buffer, length);
                datagramSocket.receive(packet);
                int packetsNum = intFromBytes(packet.getData());

                datagram = new ArrayList<byte[]>();
                for (int i = 0; i < packetsNum; i++) {
                    byte[] p = new byte[PACKET_SIZE];
                    packet = new DatagramPacket(p, PACKET_SIZE);
                    datagramSocket.receive(packet);
                    datagram.add(packet.getData());
                }

                FileOutputStream output = new FileOutputStream(new File("target-file.mp4"));
                IOUtils.write(createFile(datagram), output);

                System.out.println("received");

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] createFile(List<byte[]> packets) {
        for (int i = 1; i < packets.size(); i++) {
            if (packets.size() > 1) {
                byte[] a = ArrayUtils.addAll(packets.get(0), packets.get(i));
                packets.set(0, a);
                packets.remove(i);
                i--;
            } else break;
        }
        return packets.get(0);
    }

    private static int intFromBytes(byte[] data) {
        return new BigInteger(data).intValue();
    }
}
