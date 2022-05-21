package zuev.nikita.client;


import zuev.nikita.message.ServerResponse;
import zuev.nikita.client.socket.Connection;
import zuev.nikita.client.socket.SocketIO;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ClientMain {
    public static void main(String[] args) {

        Scanner inputScanner = new Scanner(System.in);
        Connection connection = null;
        SocketIO socketIO = null;
        int port = 52300;
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception ignored) {
        }
        String host = "helios.se.ifmo.ru";
        boolean flag = true;
        while (flag) {
            try {
                connection = new Connection(host, port);
                socketIO = new SocketIO(connection);
                break;
            } catch (IOException e) {
                System.out.println("Сервер выключен или порт " + port + " недоступен.\n" +
                        "Укажите порт сервера.");
                while (true) {
                    try {
                        String inp = inputScanner.nextLine().trim();
                        if (inp.equals("exit")) {
                            flag = false;
                            break;
                        }
                        port = Integer.parseInt(inp);
                        break;
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Укажите корректный порт.");
                    } catch (NoSuchElementException noSuchElementException) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        System.out.println("Соединение установлено.");
        while (flag) {
            try {
                //Sends the path to save a file with collection on the server.
                socketIO.write(null, null, args[0]);
                ServerResponse serverResponse = socketIO.read();

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
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Не указан путь к файлу.\n" +
                        "Укажите путь к файлу или введите exit для выхода из программы.");
                args = new String[1];
                args[0] = inputScanner.nextLine().trim();
                if (args[0].equals("exit")) break;
            } catch (NoSuchElementException e) {
                break;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Сервер стал не доступен.\n" +
                        "Введите 'exit' чтобы выйти. Или что-нибудь, чтобы попробовать подключиться снова.");
                if (inputScanner.nextLine().trim().equals("exit")) {
                    break;
                } else {
                    while (flag) {
                        try {
                            connection = new Connection(host, port);
                            socketIO = new SocketIO(connection);
                            break;
                        } catch (IOException ec) {
                            System.out.println("Сервер выключен или порт " + port + " недоступен.\n" +
                                    "Укажите порт сервера.");
                            while (true) {
                                try {
                                    String inp = inputScanner.nextLine().trim();
                                    if (inp.equals("exit")) {
                                        flag = false;
                                        break;
                                    }
                                    port = Integer.parseInt(inp);
                                    break;
                                } catch (NumberFormatException numberFormatException) {
                                    System.out.println("Укажите корректный порт.");
                                } catch (NoSuchElementException noSuchElementException) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                    }

                }

            }
        }
        if (socketIO != null)
            try {
                socketIO.close();
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
