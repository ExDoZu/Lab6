package zuev.nikita.server.command;

import zuev.nikita.structure.Organization;

import java.util.Hashtable;

/**
 * Displays all elements of the collection in string representation.
 */
public class Show extends Command {
    public Show(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (arg != null) return "Команда не нуждается в аргументе.";
        StringBuilder response = new StringBuilder();
        if (collection.isEmpty()) {
            return "Коллекция пуста.";
        } else {
            response.append("Элементы, хранящиеся в коллекции:\n");
            for (String key : collection.keySet()) {
                response.append("Ключ ").append(key).append(".\n");
                response.append(collection.get(key)).append("\n");
            }
            return response.toString();
        }
    }

    @Override
    public String getHelp() {
        return "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
