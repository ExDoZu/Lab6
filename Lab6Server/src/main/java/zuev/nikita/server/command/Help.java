package zuev.nikita.server.command;


import zuev.nikita.server.net.Connection;
import zuev.nikita.structure.Organization;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * Return help for available commands.
 */
public class Help extends Command {
    public Help(HashMap<String, Command> commandList) {
        super(commandList);
    }

    @Override
    public String execute(String arg,String savePath, Organization organization) {
        StringBuilder response = new StringBuilder();
        for (String command : commandList.keySet())
            response.append(commandList.get(command).getHelp()).append('\n');
        return response.toString();
    }

    @Override
    public String getHelp() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public String serverExecute(String arg, HashMap<SocketChannel, Connection> connections) {
        return execute(null, null, null);
    }
}
