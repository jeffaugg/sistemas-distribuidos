package br.edu.ufc.quixada.sd.t1.voting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastNoticeListener implements AutoCloseable {
    private final MulticastSocket socket;
    private final InetAddress group;

    public MulticastNoticeListener(String multicastAddress, int multicastPort) throws IOException {
        this.socket = new MulticastSocket(multicastPort);
        this.group = InetAddress.getByName(multicastAddress);
        this.socket.joinGroup(group);
    }

    public String receiveOnce() throws IOException {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }
}