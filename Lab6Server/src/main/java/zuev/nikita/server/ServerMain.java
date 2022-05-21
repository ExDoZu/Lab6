package zuev.nikita.server;

import zuev.nikita.server.command.Save;
import zuev.nikita.server.net.Connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerMain {

    private static int tryToGetPort(String[] args) {
        int port = 52300;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }
        return port;
    }

    private static Selector tryToOpenSelector() {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ServerSocketChannel tryToGetServerSocketChannel(Selector selector, int port) {
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

    public static void main(String[] args) {
        int port = tryToGetPort(args);
        Selector selector = tryToOpenSelector();
        ServerSocketChannel serverSocketChannel = tryToGetServerSocketChannel(selector, port);
        if (serverSocketChannel == null) return;
        System.out.println("Server started.");
        HashMap<SocketChannel, Connection> connections = new HashMap<>();
        boolean serverIsOn = true;
        while (serverIsOn) {
            try {
                selector.select(10);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            serverIsOn = consoleServerRead(connections);
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) {
                    selectorOPAccess(key, connections, selector);
                } else if (key.isWritable()) {
                    selectorOPReadWrite(key, connections);
                }
            }
        }
        try {
            serverSocketChannel.close();
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void selectorOPAccess(SelectionKey key, HashMap<SocketChannel, Connection> connections, Selector selector) {
        ServerSocketChannel servSockChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = servSockChannel.accept();
            socketChannel.configureBlocking(false);
            connections.put(socketChannel, new Connection(socketChannel));
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void selectorOPReadWrite(SelectionKey key, HashMap<SocketChannel, Connection> connections) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            boolean flag = connections.get(socketChannel).clientHandle();
            if (!flag) {
                throw new IOException();
            }
        } catch (IOException e) {
            connections.remove(socketChannel);
            try {
                socketChannel.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            key.cancel();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean consoleServerRead(HashMap<SocketChannel, Connection> connections) {
        Scanner scanner = new Scanner(System.in);
        boolean serverIsOn = true;
        try {
            if (System.in.available() > 0) {
                String input = scanner.nextLine().trim();
                if (input.equals("exit") || input.equals("save")) {
                    for (SocketChannel key : connections.keySet()) {
                        try {
                            new Save(connections.get(key).getCollection()).execute(null, connections.get(key).getFilePath(), null);
                        } catch (FileNotFoundException e) {
                            if (e.getMessage().equals("Нет доступа к файлу из-за нехватки прав доступа.")) {
                                System.out.println("Access denied.");
                            } else {
                                System.out.println("One of files is not found to save a collection.");
                            }
                        }
                    }
                    if (input.equals("exit")) serverIsOn = false;
                } else {
                    System.out.println("Unknown command '" + input +
                            "'\nAvailable commands:\n" +
                            "-- exit\n-- save");
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
