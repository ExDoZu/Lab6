package zuev.nikita.client.command;

import zuev.nikita.message.ServerResponse;
import zuev.nikita.client.net.SocketIO;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Updates the collection element by its ID.
 */
public class Update extends Command {

    public Update(SocketIO socketIO) {
        super(socketIO);
    }

    @Override
    public String execute(String arg, Set<File> scripts) throws IOException, ClassNotFoundException {
        if (arg == null) return "Не был указан ID.";
        Integer.parseInt(arg);//throw exception if it's not number
        socketIO.write(new String[]{"update", arg}, Organization.organizationInput(), null);
        ServerResponse serverResponse = socketIO.read();
        return serverResponse.getResponse();
    }

    @Override
    public String getHelp() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }
}
