package zuev.nikita.client.command;


import zuev.nikita.client.net.SocketIO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Return help for available commands.
 */
public class Help extends Command {

    public Help(SocketIO socketIO, HashMap<String, Command> commandList) {
        super(socketIO, commandList);
    }

    @Override
    public String execute(String arg, Set<File> scripts) throws IOException, ClassNotFoundException {
        if (arg != null) return "Команда не нуждается в аргументе.";
        StringBuilder response = new StringBuilder();
        response.append("Все команды должны вводиться в нижнем регистре по одной команде на строке!\n");
        for (String command : commandList.keySet())
            response.append(commandList.get(command).getHelp()).append('\n');
        String clientCommands = response.toString();
        socketIO.write(new String[]{"help"}, null, null);
        String serverCommands = socketIO.read().getResponse();

        return clientCommands + serverCommands;
    }

    @Override
    public String getHelp() {
        return "help : вывести справку по доступным командам";
    }
}
