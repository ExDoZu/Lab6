package zuev.nikita.client.command;


import zuev.nikita.client.net.SocketIO;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Gives a command to exit the program.
 */
public class Exit extends Command {
    public Exit(SocketIO socketIO) {
        super(socketIO);
    }

    @Override
    public String execute(String arg, Set<File> scripts) {
        if (arg != null) return "Команда не нуждается в аргументе.";
        return "exit";
    }

    @Override
    public String getHelp() {
        return "exit : завершить программу";
    }
}
