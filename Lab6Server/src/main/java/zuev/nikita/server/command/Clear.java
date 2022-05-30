package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Clears the collection.
 */
public class Clear extends Command {


    public Clear(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (arg != null) return "Команда не нуждается в аргументе.";
        if (!collection.isEmpty()) {
            collection.clear();
            return "Коллекция очищена.";
        } else {
            return "Коллекция уже пуста.";
        }

    }

    @Override
    public String getHelp() {
        return "clear : очистить коллекцию";
    }
}
