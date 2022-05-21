package zuev.nikita.server.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Connector {
    ServerSocketChannel serverSocketChannel;

    public Connector(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
    }

    public SocketChannel accept() throws IOException {
        return serverSocketChannel.accept();
    }

    public void close() throws IOException {
        serverSocketChannel.close();
    }
}
