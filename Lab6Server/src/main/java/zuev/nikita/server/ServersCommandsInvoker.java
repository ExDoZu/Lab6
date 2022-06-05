package zuev.nikita.server;

import zuev.nikita.message.ServerResponse;
import zuev.nikita.server.command.Command;
import zuev.nikita.server.net.Connection;
import zuev.nikita.structure.Organization;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Hashtable;

public class ServersCommandsInvoker {
    private final HashMap<String, Command> registeredCommands;

    public ServersCommandsInvoker() {
        registeredCommands = new HashMap<>();
    }
    public HashMap<String, Command> getRegisteredCommands() {
        return registeredCommands;
    }
    public void register(String commandName, Command command) {
        registeredCommands.put(commandName, command);
    }

    public String invoke(String[] fullCommand,  HashMap<SocketChannel, Connection> connections) {
        if (registeredCommands.containsKey(fullCommand[0])) {
            if (fullCommand.length == 1)
                return registeredCommands.get(fullCommand[0]).serverExecute(null,connections);
            else
                return registeredCommands.get(fullCommand[0]).serverExecute(fullCommand[1],  connections);
        } else {
            return "Unknown command '" + fullCommand[0] + "'";
        }
    }
}
