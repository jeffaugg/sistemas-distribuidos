package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ComputerOutputStream extends OutputStream {
    private final Computer[] computers;
    private final int quantity;
    private final OutputStream destination;

    public ComputerOutputStream(Computer[] computers, int quantity, OutputStream destination) {
        this.computers = computers;
        this.quantity = quantity;
        this.destination = destination;
    }

    public void writeObjects() throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(destination);
        dataOutputStream.writeInt(quantity);

        for (int index = 0; index < quantity && index < computers.length; index++) {
            Computer computer = computers[index];
            writeSizedString(dataOutputStream, computer.getCategory());
            writeSizedString(dataOutputStream, computer.getCode());
            writeSizedString(dataOutputStream, computer.getManufacturer());
            writeSizedString(dataOutputStream, computer.getModel());
            dataOutputStream.writeInt(computer.getRamGb());
            dataOutputStream.writeInt(computer.getStorageGb());
            dataOutputStream.writeDouble(computer.getPrice());
        }

        dataOutputStream.flush();
    }

    private void writeSizedString(DataOutputStream outputStream, String value) throws IOException {
        byte[] bytes = value == null ? new byte[0] : value.getBytes(StandardCharsets.UTF_8);
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
    }

    @Override
    public void write(int b) throws IOException {
        destination.write(b);
    }

    @Override
    public void flush() throws IOException {
        destination.flush();
    }

    @Override
    public void close() throws IOException {
        destination.close();
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new ComputerOutputStream(computers, quantity, buffer).writeObjects();
        return buffer.toByteArray();
    }
}