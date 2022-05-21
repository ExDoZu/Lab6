package zuev.nikita.server;

import zuev.nikita.server.command.*;
import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;
import zuev.nikita.server.socket.SocketChannelIO;
import zuev.nikita.structure.Organization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;


/**
 * Launch the program and starts command processing.
 *
 * @author Nikita Zuev
 */
public class ProgramLauncher {
    private final SocketChannelIO socketChannelIO;

    public ProgramLauncher(SocketChannelIO socketChannelIO) {
        this.socketChannelIO = socketChannelIO;
    }

    /**
     * Launches program
     */
    public void launch(Hashtable<String, Organization> collection, String path) throws IOException, ClassNotFoundException {

        if (collection == null) return;
        Invoker invoker = new Invoker();

        invoker.register("info", new Info(collection));
        invoker.register("clear", new Clear(collection));

        invoker.register("filter_greater_than_postal_address", new FilterGreaterThanPostalAddress(collection));
        invoker.register("help", new Help(invoker.getRegisteredCommands()));

        invoker.register("insert", new Insert(collection));
        invoker.register("print_field_ascending_postal_address", new PrintFieldAscendingPostalAddress(collection));
        invoker.register("print_field_descending_postal_address", new PrintFieldDescendingPostalAddress(collection));
        invoker.register("remove_greater_key", new RemoveGreaterKey(collection));
        invoker.register("remove_key", new RemoveKey(collection));
        invoker.register("remove_lower", new RemoveLower(collection));
        invoker.register("save", new Save(collection));
        invoker.register("show", new Show(collection));
        invoker.register("update", new Update(collection));
        String[] fullCommand;

        Organization organization;
        //'while' statement completes after inputting  the 'exit' command
        String invokerResponse;
        while (true) {
            ClientRequest clientRequest = socketChannelIO.read();
            fullCommand = clientRequest.getFullCommand();
            if(clientRequest.getPath()!=null)
                path = clientRequest.getPath();
            organization = clientRequest.getOrganization();
            if (fullCommand[0].equals("exit")){
                invoker.invoke(new String[]{"save"}, path, null);
                break;
            }

            try {
                invokerResponse = invoker.invoke(fullCommand, path, organization);
                if (invokerResponse.equals(String.valueOf(ServerResponse.WRONG_COMMAND)))
                    socketChannelIO.write(null, ServerResponse.WRONG_COMMAND);
                else
                    socketChannelIO.write(invokerResponse, ServerResponse.OK);
            } catch (FileNotFoundException e) {
                if (e.getMessage().equals("Нет доступа к файлу из-за нехватки прав доступа.")) {
                    socketChannelIO.write(null, ServerResponse.NO_ACCESS_TO_FILE);
                } else {
                    socketChannelIO.write(null, ServerResponse.WRONG_FILE_PATH);
                }
            } catch (IOException e) {
                socketChannelIO.write(null, ServerResponse.NO_ACCESS_TO_FILE);
            }


        }
    }
}
