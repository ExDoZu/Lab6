package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Removes an element from the collection by its key.
 */
public class RemoveKey extends Command {
    public RemoveKey(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization){
        if (arg == null) return "Не был указан Ключ.";
        if (collection.containsKey(arg)) {
            collection.remove(arg);
            return "Команда выполнена.";
        } else {
            return "Нет элемента с таким ключом.";
        }
    }

    @Override
    public String getHelp() {
        return "remove_key null : удалить элемент из коллекции по его ключу.";
    }
}
