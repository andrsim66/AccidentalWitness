package com.company;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
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
                byte[] bytesSize = new byte[1];
                DatagramPacket packet = new DatagramPacket(bytesSize, bytesSize.length);
                datagramSocket.receive(packet);
                byte[] bytes = new byte[intFromBytes(bytesSize)];
                packet = new DatagramPacket(bytes, bytes.length);
                datagramSocket.receive(packet);
                bytes = packet.getData();
                String filename = new String(bytes, "UTF-8");

                ArrayList<byte[]> datagram;
                byte[] size = new byte[1];
                packet = new DatagramPacket(size, size.length);
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

                String folder1 = "/home/voronsky/IdeaProjects/hackathon-frontent/video/";
//                FileOutputStream output = new FileOutputStream(new File("target-file.mp4"));
                FileOutputStream output = new FileOutputStream(new File(folder1 + filename));
                IOUtils.write(createFile(datagram), output);


                String folder = "/home/voronsky/IdeaProjects/hackathon-frontent/video/";
                String json = "/home/voronsky/IdeaProjects/hackathon-frontent/src/js/main.json";
                String start = "{\"videos\":[";
                String end = "],\"link\":\"#\"}";
                File vid = new File(folder);
                File[] files = vid.listFiles();
                String newJson = start;
                for (int i = 0; i < files.length; i++) {
                    newJson += "\"" + files[i].getName() + "\"";
                    if (i < files.length - 1)
                        newJson += ",";
                }
                newJson += end;
                System.out.println(newJson);

                PrintWriter out = new PrintWriter(json);
                out.println(newJson);
                out.flush();
                out.close();
                System.out.println("received");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readJson(String path) {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();

            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
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
