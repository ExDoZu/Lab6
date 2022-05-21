package zuev.nikita.client.net;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    public Connection(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        socket.close();
    }
}
