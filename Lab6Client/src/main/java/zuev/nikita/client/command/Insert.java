package zuev.nikita.client.command;

import zuev.nikita.message.ServerResponse;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Inserts Organization entered manually by the user by the given key into the collection.
 */
public class Insert extends Command {

    public Insert(SocketIO socketIO) {
        super(socketIO);
    }

    @Override
    public String execute(String arg, Set<File> scripts) throws IOException, ClassNotFoundException {
        if (arg == null) return "Не был указан Ключ.";

        socketIO.write(new String[]{"insert", arg}, Organization.organizationInput(), null);

        ServerResponse serverResponse = socketIO.read();
        return serverResponse.getResponse();
    }


    @Override
    public String getHelp() {
        return "insert null {element} : добавить новый элемент с заданным ключом";
    }
}
