package zuev.nikita.server;

import zuev.nikita.server.command.Save;
import zuev.nikita.message.ServerResponse;
import zuev.nikita.server.socket.Connector;
import zuev.nikita.server.socket.SocketChannelIO;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        Connector connector = null;
        Scanner scanner = new Scanner(System.in);
        int port = 52300;

        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }

        boolean stop = false;
        while (true) {
            try {
                connector = new Connector(port);
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
        System.out.println("Server started.");
        SocketChannel socketChannel;
        while (!stop) { // The Great Server While
            socketChannel = null;
            while (socketChannel == null) { // The Little Server While
                try {
                    socketChannel = connector.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Client connected.");
            SocketChannelIO socketChannelIO = null;
            try {
                socketChannelIO = new SocketChannelIO(socketChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            boolean flag = true;
            String path = null, response;
            Hashtable<String, Organization> collection = null;

            try {
                path = socketChannelIO.read().getPath();
                while (flag) {
                    File file = new File(path);
                    if (path.equals("exit")) break;
                    else if (file.isDirectory()) {
                        socketChannelIO.write("Укажите имя файла, а не директории.", ServerResponse.WRONG_FILE_PATH);
                        path = socketChannelIO.read().getPath();
                    } else if (file.exists()) {
                        if (!(file.canRead() && file.canWrite())) {
                            socketChannelIO.write(null, ServerResponse.NO_ACCESS_TO_FILE);
                            path = socketChannelIO.read().getPath();
                        } else {
                            try {
                                collection = JsonDataHandler.parseFile(path);
                                socketChannelIO.write(null, ServerResponse.OK);
                                flag = false;
                            } catch (WrongDataException e) {
                                socketChannelIO.write(e.getMessage(), ServerResponse.WRONG_DATA);
                            } catch (Exception e) {
                                socketChannelIO.write(null, ServerResponse.WRONG_FILE_STRUCTURE);
                            }
                            if (flag) {
                                response = socketChannelIO.read().getPath();
                                if (response.equalsIgnoreCase("exit") || response.equalsIgnoreCase("no") || response.equalsIgnoreCase("0")) {
                                    flag = false;
                                } else if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("1")) {
                                    collection = new Hashtable<>();
                                    socketChannelIO.write(null, ServerResponse.OK);
                                    flag = false;
                                } else {
                                    path = response;
                                }
                            }
                        }
                    } else {
                        socketChannelIO.write("Файл не обнаружен.", ServerResponse.WRONG_FILE_PATH);
                        path = socketChannelIO.read().getPath();
                    }
                }
            } catch (ClassNotFoundException | IOException rwException) {
                System.out.println("Read/Write error. Trying to get message.");
            }

            ProgramLauncher programLauncher = new ProgramLauncher(socketChannelIO);
            try {
                programLauncher.launch(collection, path);
                Save save = new Save(collection);
                save.execute(null, path, null);
            } catch (IOException | ClassNotFoundException ignored) {
                Save save = new Save(collection);
                try {
                    save.execute(null, path, null);
                } catch (IOException ignored1) {
                }
            }
            try {
                socketChannelIO.close();
                socketChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Client disconnected.\n" +
                    "Input 'exit' to stop or anything to continue.");
            if (scanner.nextLine().trim().equals("exit")) {
                stop = true;
            }
        }
        try {
            connector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
