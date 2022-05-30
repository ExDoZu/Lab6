package zuev.nikita.server.command;

import zuev.nikita.structure.Address;
import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;
import java.util.stream.Collectors;

/**
 * Returns all elements as a string whose postalAddress field value is greater than the given value.
 */
public class FilterGreaterThanPostalAddress extends Command {
    public FilterGreaterThanPostalAddress(Hashtable<String, Organization> collection) {
        super(collection);
    }

    /**
     * @param arg Given address.
     */
    @Override
    public String execute(String arg, String savePath, Organization organization){
        if (arg == null) return "Команда нуждается в аргументе.";
        Address userInputPostalAddress = new Address(arg);
        String str = collection.values().stream().filter(v -> v.getPostalAddress().compareTo(userInputPostalAddress) > 0).map(Organization::toString).collect(Collectors.joining("\n\n"));
        if (!str.equals("")) {
            return str;
        } else {
            return "Нет элементов с адресом больше заданного.";
        }
    }

    @Override
    public String getHelp() {
        return "filter_greater_than_postal_address postalAddress : вывести элементы, значение поля postalAddress которых больше заданного";
    }
}
