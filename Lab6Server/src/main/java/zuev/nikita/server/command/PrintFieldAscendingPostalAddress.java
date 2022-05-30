package zuev.nikita.server.command;

import zuev.nikita.structure.Address;
import zuev.nikita.structure.Organization;

import java.io.IOException;
import java.util.Hashtable;
import java.util.stream.Collectors;

/**
 * Returns the values of the postalAddreess field of all elements in ascending order as a string.
 */
public class PrintFieldAscendingPostalAddress extends Command {
    public PrintFieldAscendingPostalAddress(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg,String savePath, Organization organization) {
        if(arg!=null)return "Команда не нуждается в аргументе.";
        return collection.values().stream().map(Organization::getPostalAddress).sorted().map(Address::getZipCode).collect(Collectors.joining("\n"));
    }

    @Override
    public String getHelp() {
        return "print_field_ascending_postal_address : вывести значения поля postalAddress всех элементов в порядке возрастания.";
    }
}
