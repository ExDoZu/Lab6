package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Removes from the collection all elements whose key is greater than the specified value.
 */
public class RemoveGreaterKey extends Command {
    public RemoveGreaterKey(Hashtable<String, Organization> collection) {
        super(collection);
    }

    /**
     * @param arg Given key
     */
    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (arg == null) return "Не был указан Ключ.";

        Hashtable<String, Organization> newCollection = new Hashtable<>(collection.entrySet().stream().filter(x -> x.getKey().compareTo(arg) <= 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        if (newCollection.size() == collection.size()) {
            return "Нет элементов с ключом больше заданного.";
        } else {
            collection.clear();
            collection.putAll(newCollection);
            return "Команда выполнена.";
        }
    }

    @Override
    public String getHelp() {
        return "remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный";
    }
}
