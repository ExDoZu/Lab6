package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

/**
 * Updates the collection element by its ID.
 */
public class Update extends Command {
    public Update(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        int id = Integer.parseInt(arg);
        boolean foundID = false;
        for (String key : collection.keySet())
            if (collection.get(key).getId() == id) {
                foundID = true;
                organization.setId(id);
                organization.setCreationDate(new Date());
                collection.replace(key, organization);
                break;
            }
        if (foundID) return "Данные успешно обновлены.";
        else return "Нет элемента коллекции с таким ID.";
    }

    @Override
    public String getHelp() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }
}
