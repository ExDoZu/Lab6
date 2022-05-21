package zuev.nikita.client.command;


import zuev.nikita.client.socket.SocketIO;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Gives a command to exit the program.
 */
public class Exit extends Command {
public Exit(SocketIO socketIO){
    super(socketIO);
}
    @Override
    public String execute(String arg, Set<File> scripts) throws IOException {
        if (arg != null) return "Команда не нуждается в аргументе.";
        return "exit";
    }

    @Override
    public String getHelp() {
        return "exit : завершить программу (без сохранения в файл)";
    }
}