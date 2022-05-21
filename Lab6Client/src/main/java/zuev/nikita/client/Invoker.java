package zuev.nikita.client;

import zuev.nikita.client.command.Command;
import zuev.nikita.client.command.Exit;
import zuev.nikita.client.command.History;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.message.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Invokes commands.
 */
public class Invoker {

    /**
     * Contains current executing scripts.
     */
    private final Set<File> scripts;
    private final List<String> commandsHistory = new ArrayList<>();
    private final HashMap<String, Command> registeredCommands;
    private final SocketIO socketIO;

    public HashMap<String, Command> getRegisteredCommands() {
        return registeredCommands;
    }


    public Invoker(SocketIO socketIO) {
        this.socketIO = socketIO;
        registeredCommands = new HashMap<>();
        scripts = new HashSet<>();
        for (int i = 0; i < 10; i++) commandsHistory.add(null);

        registeredCommands.put("exit", new Exit(socketIO));
        registeredCommands.put("history", new History(commandsHistory));

    }

    public Invoker(HashMap<String, Command> commands, Set<File> scripts, SocketIO socketIO) {
        this.socketIO = socketIO;
        registeredCommands = new HashMap<>(commands);
        this.scripts = scripts;
        for (int i = 0; i < 10; i++) commandsHistory.add(null);

        registeredCommands.put("exit", new Exit(socketIO));
        registeredCommands.put("history", new History(commandsHistory));
    }

    /**
     * Registers a new command
     */
    public void register(String commandName, Command command) {
        registeredCommands.put(commandName, command);
    }

    /**
     * @param fullCommand command with/without argument
     */
    public String invoke(String[] fullCommand, String path) throws IOException, ClassNotFoundException {
        if (registeredCommands.containsKey(fullCommand[0])) {
            commandsHistory.remove(0);
            commandsHistory.add(fullCommand[0]);
            if (fullCommand.length == 1)
                return registeredCommands.get(fullCommand[0]).execute(null, scripts);
            else
                return registeredCommands.get(fullCommand[0]).execute(fullCommand[1], scripts);
        } else {
            socketIO.write(fullCommand, null, path);
            ServerResponse serverResponse = socketIO.read();
            if (serverResponse.getStatusCode() != ServerResponse.OK) {
                if (serverResponse.getStatusCode() == ServerResponse.WRONG_COMMAND) {
                    return "Команда " + fullCommand[0] + " не существует.";
                } else {
                    return String.valueOf(serverResponse.getStatusCode());
                }
            } else {
                commandsHistory.remove(0);
                commandsHistory.add(fullCommand[0]);
                return serverResponse.getResponse();
            }
        }

    }
}

