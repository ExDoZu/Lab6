package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Super class for commands
 */
public abstract class Command {
    /**
     * Collection processed by a command
     */
    protected final Hashtable<String, Organization> collection;
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
     * @throws IOException Throws when there is problem with file reading or writing.
     */
    public abstract String execute(String arg, String savePath, Organization organization) throws IOException;

    /**
     * @return Information about the command.
     */
    public abstract String getHelp();
}
