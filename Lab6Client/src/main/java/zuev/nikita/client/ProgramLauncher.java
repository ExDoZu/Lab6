package zuev.nikita.client;

import zuev.nikita.message.ServerResponse;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.client.command.*;
import java.io.IOException;
import java.util.Scanner;


/**
 * Launch the program and starts command processing.
 *
 * @author Nikita Zuev
 */
public class ProgramLauncher {
    /**
     * Launches program
     *
     * @param path path to file for reading and writing.
     */
    public void launch(String path, SocketIO socketIO) throws IOException, ClassNotFoundException {
        Scanner inputScanner = new Scanner(System.in);
        Invoker invoker = new Invoker(socketIO);
        invoker.register("execute_script", new ExecuteScript(socketIO, invoker.getRegisteredCommands()));
        invoker.register("help", new Help(socketIO, invoker.getRegisteredCommands()));
        invoker.register("insert", new Insert(socketIO));
        invoker.register("remove_lower", new RemoveLower(socketIO));
        invoker.register("update", new Update(socketIO));
        String[] fullCommand;
        String bufPath = "";
        //'while' statement completes after inputting  the 'exit' command
        boolean exitFlag = true;
        String invokerResponse;
        while (exitFlag) {
            fullCommand = inputScanner.nextLine().trim().split("\\s+", 2);
            if (!fullCommand[0].equals(""))
                while (true) {
                    try {
                        invokerResponse = invoker.invoke(fullCommand, path);

                        if (invokerResponse.equals("exit")) {
                            exitFlag = false;
                            break;
                        } else {
                            try {
                                int statusCode = Integer.parseInt(invokerResponse);

                                switch (statusCode) {
                                    case ServerResponse.NO_ACCESS_TO_FILE:
                                        System.out.println("Нехватка прав доступа. Укажите путь к новому файлу.\n" +
                                                "cancel - отменить команду сохранения.");
                                        bufPath = inputScanner.nextLine().trim();
                                        break;
                                    case ServerResponse.WRONG_FILE_PATH:
                                        System.out.println("Файл не найден.");
                                        bufPath = "cancel";
                                        break;
                                }
                                if (bufPath.equals("cancel")) {
                                    break;
                                } else path = bufPath;
                            } catch (NumberFormatException numberFormatException) {
                                //if this exception is got response is ok
                                System.out.println(invokerResponse);
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ID должен быть целым числом.");
                        break;
                    }
                }
        }
    }
}
