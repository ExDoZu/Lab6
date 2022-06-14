package zuev.nikita.client;

import zuev.nikita.client.net.Connection;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.message.ServerResponse;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Base class of program. Prepares port, host, file path and start a session.
 */
public class Client {


    /**
     * Trys to get a port from program arguments.
     * @param args Port should be the second argument
     * @return port got from program arguments or 52300 as default
     */
    private int tryToGetPort(String[] args) {
        int port = 52300;
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception ignored) {
        }
        return port;
    }

    /**
     * Trys to get a connection using the host and the port.
     * @param host host
     * @param port port
     * @return Connection or null
     */
    private Connection tryToConnect(String host, int port) {
        Scanner inputScanner = new Scanner(System.in);
        while (true) {
            try {
                return new Connection(host, port);
            } catch (IOException e) {
                System.out.println("Сервер выключен или порт " + port + " недоступен.\n" +
                        "Укажите порт сервера.");
                while (true) {
                    try {
                        String inp = inputScanner.nextLine().trim();
                        if (inp.equals("exit")) {
                            return null;
                        }
                        port = Integer.parseInt(inp);
                        break;
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Укажите корректный порт.");
                    } catch (NoSuchElementException noSuchElementException) {
                        return null;
                    }
                }
            }
        }
    }

    public void start(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        SocketIO socketIO = null;
        int port = tryToGetPort(args);
        String host = "localhost";
        Connection connection = tryToConnect(host, port);
        boolean flag = false;
        if (connection != null) {
            try {
                socketIO = new SocketIO(connection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            port = connection.getSocket().getPort();
            flag = true;
            System.out.println("Соединение установлено.");
        }
        while (flag) {
            try {
                //Sends the path to save a file with collection on the server.
                socketIO.write(null, null, args[0]);
                ServerResponse serverResponse = socketIO.read();
                flag = handleResponseOnPath(serverResponse, socketIO, args);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Не указан путь к файлу.\n" +
                        "Укажите путь к файлу или введите exit для выхода из программы.");
                args = new String[1];
                try {
                    args[0] = inputScanner.nextLine().trim();
                } catch (NoSuchElementException elementException) {
                    break;
                }
                if (args[0].equals("exit")) break;
            } catch (NoSuchElementException e) {
                break;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Сервер стал не доступен.\n" +
                        "Введите 'exit' чтобы выйти. Или что-нибудь, чтобы попробовать подключиться снова.");
                if (inputScanner.nextLine().trim().equals("exit")) {
                    break;
                } else {
                    connection = tryToConnect(host, port);
                    if (connection != null) {
                        try {
                            socketIO = new SocketIO(connection);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        port = connection.getSocket().getPort();
                        System.out.println("Соединение восстановлено.");
                    }
                }
            }
        }
        if (connection != null)
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Trys to get information by server about the collection file and to start real session of collection editing
     * @param serverResponse Contains status code of server response
     * @param socketIO Instruments for getting and sending data
     * @param args contains the path to file in 0 element (the first program argument)
     * @return status of continuation of work
     */
    private boolean handleResponseOnPath(ServerResponse serverResponse, SocketIO socketIO, String[] args) throws IOException, ClassNotFoundException {
        Scanner inputScanner = new Scanner(System.in);
        boolean flag = true;
        if (serverResponse.getStatusCode() != ServerResponse.OK) {
            switch (serverResponse.getStatusCode()) {
                case ServerResponse.WRONG_FILE_STRUCTURE:
                    System.out.println("Структура файла некорректна или файл поврежден.\n" +
                            "Введите путь к другому файлу.\n" +
                            "Продолжить с данным файлом без данных из него (Предыдущие данные будут потеряны)? yes/no. (no = exit)");
                    break;
                case ServerResponse.WRONG_DATA:
                    System.out.println("В файле содержатся некорректные данные. " + serverResponse.getResponse() +
                            "\nВведите путь к другому файлу.\n" +
                            "Продолжить с данным файлом без данных из него (Предыдущие будут потеряны)? yes/no. (no = exit)");
                    break;
                case ServerResponse.WRONG_FILE_PATH:
                    System.out.println(serverResponse.getResponse() +
                            "\nВведите путь к другому файлу.");
                    break;
                case ServerResponse.NO_ACCESS_TO_FILE:
                    System.out.println("Нехватка прав доступа. Укажите новое имя файла.");
                    break;
            }
            String response = inputScanner.nextLine().trim();
            if (response.equalsIgnoreCase("exit") || response.equalsIgnoreCase("no") || response.equalsIgnoreCase("0")) {
                flag = false;
                args[0] = "exit";
                socketIO.write(null, null, args[0]);
            } else if ((response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("1"))
                    && serverResponse.getStatusCode() != ServerResponse.WRONG_FILE_PATH && serverResponse.getStatusCode() != ServerResponse.NO_ACCESS_TO_FILE) {
                args[0] = "yes";
            } else {
                args[0] = response;
            }
        } else {
            ProgramLauncher programLauncher = new ProgramLauncher();
            programLauncher.launch(args[0], socketIO);
            flag = false;
        }
        return flag;
    }
}
