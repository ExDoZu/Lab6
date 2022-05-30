package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Inserts Organization entered manually by the user by the given key into the collection.
 */
public class Insert extends Command {
    public Insert(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (collection.containsKey(arg)) {
            return "Уже есть элемент с таким ключом.";
        } else {
            Set<Integer> ids = new HashSet<>();
            for (String key : collection.keySet())
                ids.add(collection.get(key).getId());
            int id = 0;
            for (int i = 1; i <= ids.size() + 1; i++)
                if (!ids.contains(i)) id = i;
            organization.setId(id);
            organization.setCreationDate(new Date());
            collection.put(arg, organization);
            return "Данные успешно введены.";
        }
    }

    @Override
    public String getHelp() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}
