package zuev.nikita.client.command;

import zuev.nikita.client.net.SocketIO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Super class for commands
 */
public abstract class Command {
    protected final SocketIO socketIO;
    protected final List<String> history;
    protected final HashMap<String, Command> commandList;
    protected final String savePath;

    public Command(SocketIO socketIO, HashMap<String, Command> commandList, String savePath) {
        this.commandList = commandList;
        this.socketIO = socketIO;
        history = null;
        this.savePath = savePath;
    }

    public Command(List<String> history) {
        socketIO = null;
        this.history = history;
        commandList = null;
        savePath = null;
    }

    public Command(SocketIO socketIO, HashMap<String, Command> commandList) {
        this.commandList = commandList;
        this.socketIO = socketIO;
        history = null;
        savePath = null;
    }

    public Command(SocketIO socketIO) {
        this.socketIO = socketIO;
        history = null;
        commandList = null;
        savePath = null;
    }


    /**
     * @param arg     Command argument
     * @param scripts Executing scripts
     * @return result/report of command execution
     * @throws IOException Throws when there is problem with file reading or writing.
     */
    public abstract String execute(String arg, Set<File> scripts) throws IOException, ClassNotFoundException;

    /**
     * @return Information about the command.
     */
    public abstract String getHelp();
}
