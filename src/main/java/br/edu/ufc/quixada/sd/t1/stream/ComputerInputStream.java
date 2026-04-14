package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Mainframe;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Microcomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Minicomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Notebook;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ComputerInputStream extends InputStream {
    private final InputStream source;

    public ComputerInputStream(InputStream source) {
        this.source = source;
    }

    public Computer[] readObjects() throws IOException {
        DataInputStream inputStream = new DataInputStream(source);
        int quantity = inputStream.readInt();
        Computer[] computers = new Computer[quantity];

        for (int index = 0; index < quantity; index++) {
            String category = readSizedString(inputStream);
            String code = readSizedString(inputStream);
            String manufacturer = readSizedString(inputStream);
            String model = readSizedString(inputStream);
            int ramGb = inputStream.readInt();
            int storageGb = inputStream.readInt();
            double price = inputStream.readDouble();

            computers[index] = createComputer(category, code, manufacturer, model, ramGb, storageGb, price);
        }

        return computers;
    }

    private String readSizedString(DataInputStream inputStream) throws IOException {
        int size = inputStream.readInt();
        byte[] bytes = new byte[size];
        inputStream.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Computer createComputer(String category, String code, String manufacturer, String model, int ramGb, int storageGb, double price) {
        if ("Mainframe".equalsIgnoreCase(category)) {
            return new Mainframe(code, manufacturer, model, ramGb, storageGb, price);
        }
        if ("Minicomputador".equalsIgnoreCase(category)) {
            return new Minicomputador(code, manufacturer, model, ramGb, storageGb, price);
        }
        if ("Notebook".equalsIgnoreCase(category)) {
            return new Notebook(code, manufacturer, model, ramGb, storageGb, price);
        }
        return new Microcomputador(code, manufacturer, model, ramGb, storageGb, price);
    }

    @Override
    public int read() throws IOException {
        return source.read();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}