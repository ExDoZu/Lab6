package zuev.nikita.server.net;

import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelIO {
    private final SocketChannel socketChannnel;


    public SocketChannelIO(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);
        this.socketChannnel = socketChannel;
    }

    public ClientRequest read() throws IOException, ClassNotFoundException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
        int gotBytes = socketChannnel.read(byteBuffer);
        if (gotBytes == -1) return new ClientRequest(new String[]{"exit"}, null, null);
        else if (gotBytes == 0) return null;
        else {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteBuffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(bis);
            ClientRequest clientRequest = (ClientRequest) objectInputStream.readObject();
            objectInputStream.close();
            bis.close();
            return clientRequest;
        }
    }

    public void write(String response, int statusCode) throws IOException {
        ServerResponse serverResponse = new ServerResponse(response, statusCode);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(serverResponse);
        objectOutputStream.flush();

        ByteBuffer byteBuffer = ByteBuffer.wrap(bos.toByteArray());
        socketChannnel.write(byteBuffer);
        objectOutputStream.flush();

        objectOutputStream.close();
    }

    public void block() throws IOException {
        socketChannnel.configureBlocking(true);
    }

    public void unblock() throws IOException {
        socketChannnel.configureBlocking(false);
    }
}
