package zuev.nikita.server.socket;

import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

public class SocketChannelIO {
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;


    public SocketChannelIO(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(true);
        objectOutputStream = new ObjectOutputStream(socketChannel.socket().getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(socketChannel.socket().getInputStream());
    }

    public ClientRequest read() throws IOException, ClassNotFoundException {
        return (ClientRequest) objectInputStream.readObject();
    }

    public void write(String response, int statusCode) throws IOException {
        ServerResponse serverResponse = new ServerResponse(response, statusCode);
        objectOutputStream.writeObject(serverResponse);
        objectOutputStream.flush();
    }

    public void close() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
    }
}
