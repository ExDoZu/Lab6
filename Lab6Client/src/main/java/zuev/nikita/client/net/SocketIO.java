package zuev.nikita.client.net;

import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;
import zuev.nikita.structure.Organization;

import java.io.*;

/**
 * Instrument to send and get serialized objects using socket
 */
public class SocketIO {

    private final Connection connection;


    public SocketIO(Connection connection) throws IOException {
        this.connection = connection;
    }

    public void write(String[] fullCommand, Organization organization, String path) throws IOException {
        ClientRequest clientRequest = new ClientRequest(fullCommand, organization, path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objOutputStream = new ObjectOutputStream(bos);
        objOutputStream.writeObject(clientRequest);
        objOutputStream.flush();
        connection.getSocket().getOutputStream().write(bos.toByteArray());
        connection.getSocket().getOutputStream().flush();
        objOutputStream.close();
        bos.close();
    }

    public ServerResponse read() throws IOException, ClassNotFoundException {
        byte[] arr = new byte[16384];
        //Magic bytes for ObjectInputStream. They are unnecessary to initialize ObjectInputStream
        arr[0] = -84;
        arr[1] = -19;
        arr[2] = 0;
        arr[3] = 5;
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInputStream objInputStream = new ObjectInputStream(bis);
        connection.getSocket().getInputStream().read(arr);
        ServerResponse serverResponse = (ServerResponse) objInputStream.readObject();
        objInputStream.close();
        bis.close();
        return serverResponse;
    }
}
