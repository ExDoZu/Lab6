package zuev.nikita.server.command;

import zuev.nikita.server.net.Connection;
import zuev.nikita.structure.Organization;


import java.nio.channels.SocketChannel;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Super class for commands
 */
public abstract class Command {
    /**
     * Collection processed by a command
     */
    protected Hashtable<String, Organization> collection;
    protected final HashMap<String, Command> commandList;

    public Command() {
        collection = new Hashtable<>();
        commandList = null;
    }

    public Command(Hashtable<String, Organization> collection) {
        this.collection = collection;
        commandList = null;
    }

    public Command(HashMap<String, Command> commandList) {
        this.commandList = commandList;
        collection = new Hashtable<>();
    }

    /**
     * @param arg Command argument
     * @return result/report of command execution
     */
    public abstract String execute(String arg, String savePath, Organization organization);

    public String serverExecute(String arg, HashMap<SocketChannel, Connection> connections) {
        return null;
    }

    /**
     * @return Information about the command.
     */
    public abstract String getHelp();
}
