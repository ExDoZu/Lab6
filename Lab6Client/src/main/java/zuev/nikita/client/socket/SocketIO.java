package zuev.nikita.client.socket;

import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;
import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SocketIO {
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;


    public SocketIO(Connection connection) throws IOException {
        objectOutputStream = new ObjectOutputStream(connection.getSocket().getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(connection.getSocket().getInputStream());
    }

    public void write(String[] fullCommand, Organization organization, String path) throws IOException {
        ClientRequest clientRequest = new ClientRequest(fullCommand, organization, path);
        objectOutputStream.writeObject(clientRequest);
        objectOutputStream.flush();
    }

    public ServerResponse read() throws IOException, ClassNotFoundException {
        return (ServerResponse) objectInputStream.readObject();
    }

    public void close() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
    }
}
