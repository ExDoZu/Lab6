package zuev.nikita.client.command;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Returns the last 10 commands.
 */
public class History extends Command {
    public History(List<String> history) {
        super(history);
    }

    @Override
    public String execute(String arg, Set<File> scripts) throws IOException {
        if (arg != null) return "Команда не нуждается в аргументе.";
        StringBuilder response = new StringBuilder();
        response.append("История команд:\n");
        for (String command : history)
            if (command != null) response.append("-- ").append(command).append('\n');
        return response.toString();
    }

    @Override
    public String getHelp() {
        return "history : вывести последние 10 команд (без их аргументов).";
    }
}
