package zuev.nikita.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zuev.nikita.server.command.Exit;
import zuev.nikita.server.command.Help;
import zuev.nikita.server.command.Save;
import zuev.nikita.server.net.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Base class of Server. Prepares port and handles connections via Selector
 */
public class Server {
    private final Logger log = LoggerFactory.getLogger(ServerMain.class);
    /**
     * Trys to get a port from program arguments.
     * @param args Port should be the first argument (0-index)
     * @return port got from program arguments or 52300 as default
     */
    private int tryToGetPort(String[] args) {
        int port = 52300;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }
        return port;
    }

    private Selector tryToOpenSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ServerSocketChannel tryToGetServerSocketChannel(Selector selector, int port) {
        Scanner scanner = new Scanner(System.in);
        ServerSocketChannel serverSocketChannel = null;
        boolean stop = false;
        while (!stop) {
            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(port));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                break;
            } catch (IOException e) {
                System.out.println("Port " + port + " is not available.\n" +
                        "Input a new port");
                while (true) {
                    try {
                        port = Integer.parseInt(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Input a correct port.");
                    } catch (NoSuchElementException noSuchElementException) {
                        stop = true;
                        break;
                    }
                }
            }
        }
        return serverSocketChannel;
    }
    private void serverInvokerRegisterCommands(ServersCommandsInvoker serversCommandsInvoker){
        serversCommandsInvoker.register("exit", new Exit());
        serversCommandsInvoker.register("save", new Save());
        serversCommandsInvoker.register("help", new Help(serversCommandsInvoker.getRegisteredCommands()));

    }
    public void start(String[] args) {
        ServersCommandsInvoker serversCommandsInvoker =new ServersCommandsInvoker();
        serverInvokerRegisterCommands(serversCommandsInvoker);

        log.info("Application started");
        int port = tryToGetPort(args);
        log.info("Port is set as " + port);
        Selector selector = tryToOpenSelector();
        ServerSocketChannel serverSocketChannel = tryToGetServerSocketChannel(selector, port);
        if (serverSocketChannel == null) return;
        log.info("Server started");
        HashMap<SocketChannel, Connection> connections = new HashMap<>();
        boolean serverIsOn = true;
        while (serverIsOn) {
            try {
                selector.select(10);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            serverIsOn = consoleServerRead(connections, serversCommandsInvoker);
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) {
                    selectorOPAccess(key, connections, selector);
                } else if (key.isWritable()) {
                    selectorOPReadWrite(key, connections, serversCommandsInvoker);
                }
            }
        }
        try {
            serverSocketChannel.close();
            selector.close();
            log.info("Server stopped");
        } catch (IOException e) {
            log.error("Unexpected error " + e);
            throw new RuntimeException(e);

        }
    }

    /**
     * Handles access operation for selector.
     * @param connections Map of existing connections.
     */
    private void selectorOPAccess(SelectionKey key, HashMap<SocketChannel, Connection> connections, Selector selector) {
        ServerSocketChannel servSockChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = servSockChannel.accept();
            log.info("New connection is accepted");
            socketChannel.configureBlocking(false);
            try {
                connections.put(socketChannel, new Connection(socketChannel));
                socketChannel.register(selector, SelectionKey.OP_WRITE);
                log.info("New connection is established.");
            } catch (NullPointerException nullPointerException) {
                socketChannel.close();
                key.cancel();
                log.info("New connection is canceled");
            }
        } catch (IOException e) {
            log.error("Unexpected error " + e);
            throw new RuntimeException(e);
        }
    }
    /**
     * Handles read and write operations for selector.
     * @param connections Map of existing connections.
     */
    private void selectorOPReadWrite(SelectionKey key, HashMap<SocketChannel, Connection> connections, ServersCommandsInvoker serversCommandsInvoker) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            boolean flag = connections.get(socketChannel).clientHandle();
            if (!flag) {
                throw new IOException();
            }
        } catch (IOException e) {
            serversCommandsInvoker.invoke(new String[]{"save"}, connections);
            connections.remove(socketChannel);
            try {
                socketChannel.close();
            } catch (IOException ex) {
                log.error("Unexpected error " + ex);
                throw new RuntimeException(ex);
            }
            key.cancel();
            log.info("Connection is down");
        } catch (ClassNotFoundException e) {
            log.error("Invalid data transmitted over the network");
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles server console input.
     * @param connections Map of existing connections.
     */
    private boolean consoleServerRead(HashMap<SocketChannel, Connection> connections, ServersCommandsInvoker serversCommandsInvoker) {
        Scanner scanner = new Scanner(System.in);
        boolean serverIsOn = true;
        try {
            if (System.in.available() > 0) {
                String input = scanner.nextLine().trim();
                String response = serversCommandsInvoker.invoke(input.split("\\s+", 2),connections);
                if (response.equals("exit")) serverIsOn = false;
                else{
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            serverIsOn = false;
        }
        return serverIsOn;
    }
}
