package zuev.nikita.server.command;


import zuev.nikita.structure.Organization;

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
}
