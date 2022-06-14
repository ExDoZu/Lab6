package zuev.nikita.server.command;

import zuev.nikita.server.net.Connection;
import zuev.nikita.structure.Organization;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Hashtable;

public class Exit extends Command{
    @Override
    public String execute(String arg, String savePath, Organization organization) {
       return null;
    }

    @Override
    public String serverExecute(String arg,  HashMap<SocketChannel, Connection> connections) {
        System.out.println(new Save().serverExecute(arg, connections));
        return "exit";
    }

    @Override
    public String getHelp() {
        return "exit: exit from the program saving collections.";
    }
}
