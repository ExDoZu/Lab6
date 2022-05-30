package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Removes all elements from the collection that are less than the specified value.
 */
public class RemoveLower extends Command {
    public RemoveLower(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization){
        Hashtable<String, Organization> newCollection = new Hashtable<>(collection.entrySet().stream().filter(x -> x.getValue().compareTo(organization) >= 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
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
        return "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный";
    }
}
