package zuev.nikita.client.command;

import zuev.nikita.message.ServerResponse;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Removes all elements from the collection that are less than the specified value.
 */
public class RemoveLower extends Command {
    public RemoveLower(SocketIO socketIO) {
        super(socketIO);
    }

    @Override
    public String execute(String arg, Set<File> scripts) throws IOException, ClassNotFoundException {
        if (arg != null) return "Команда не нуждается в аргументе.";
        Organization organization = Organization.organizationInput();
        socketIO.write(new String[]{"remove_lower"}, organization, null);
        ServerResponse serverResponse = socketIO.read();

        return serverResponse.getResponse();
    }

    @Override
    public String getHelp() {
        return "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный";
    }
}
