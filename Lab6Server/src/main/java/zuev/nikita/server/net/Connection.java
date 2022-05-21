package zuev.nikita.server.net;

import zuev.nikita.server.Invoker;
import zuev.nikita.server.JsonDataHandler;
import zuev.nikita.server.WrongDataException;
import zuev.nikita.server.command.*;
import zuev.nikita.message.ClientRequest;
import zuev.nikita.message.ServerResponse;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

/**
 * Client connection class
 */
public class Connection {
    private final SocketChannelIO socketChannelIO;
    private String filePath=null;
    private Hashtable<String, Organization> collection=null;
    private final Invoker invoker;

    public Connection(SocketChannel socketChannel) {
        try {
            socketChannelIO = new SocketChannelIO(socketChannel);
            socketChannelIO.block();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initPathAndCollection();
        try {
            socketChannelIO.unblock();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        invoker = new Invoker();
        registerInvokerCommands();
    }

    private void initPathAndCollection() {
        boolean flag = true;
        String response;
        try {
            filePath = socketChannelIO.read().getPath();
            while (flag) {
                File file = new File(filePath);
                if (filePath.equals("exit")) break;
                else if (file.isDirectory()) {
                    socketChannelIO.write("Укажите имя файла, а не директории.", ServerResponse.WRONG_FILE_PATH);
                    filePath = socketChannelIO.read().getPath();
                } else if (file.exists()) {
                    if (!(file.canRead() && file.canWrite())) {
                        socketChannelIO.write(null, ServerResponse.NO_ACCESS_TO_FILE);
                        filePath = socketChannelIO.read().getPath();
                    } else {
                        try {
                            collection = JsonDataHandler.parseFile(filePath);
                            socketChannelIO.write(null, ServerResponse.OK);
                            flag = false;
                        } catch (WrongDataException e) {
                            socketChannelIO.write(e.getMessage(), ServerResponse.WRONG_DATA);
                        } catch (Exception e) {
                            socketChannelIO.write(null, ServerResponse.WRONG_FILE_STRUCTURE);
                        }
                        if (flag) {
                            response = socketChannelIO.read().getPath();
                            if (response.equalsIgnoreCase("exit") || response.equalsIgnoreCase("no") || response.equalsIgnoreCase("0")) {
                                flag = false;
                            } else if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("1")) {
                                collection = new Hashtable<>();
                                socketChannelIO.write(null, ServerResponse.OK);
                                flag = false;
                            } else {
                                filePath = response;
                            }
                        }
                    }
                } else {
                    socketChannelIO.write("Файл не обнаружен.", ServerResponse.WRONG_FILE_PATH);
                    filePath = socketChannelIO.read().getPath();
                }

            }
        } catch (ClassNotFoundException | IOException rwException) {
            System.out.println("Read/Write error. Trying to get message.");
        }
    }

    private void registerInvokerCommands() {
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
        invoker.register("show", new Show(collection));
        invoker.register("update", new Update(collection));
    }

    /**
     * Handles commands by a client using the invoker
     * @return true - continue handling this connection
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public boolean clientHandle() throws IOException, ClassNotFoundException {
        if (collection == null) return false;
        String invokerResponse;

        ClientRequest clientRequest = socketChannelIO.read();
        if (clientRequest == null) return true;
        String[] fullCommand = clientRequest.getFullCommand();
        if (clientRequest.getPath() != null)
            filePath = clientRequest.getPath();
        Organization organization = clientRequest.getOrganization();

        if (fullCommand[0].equals("exit")) {
            new Save(collection).execute(null, filePath, null);
            return false;
        }
        try {
            invokerResponse = invoker.invoke(fullCommand, filePath, organization);
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
        return true;
    }

    public String getFilePath() {
        return filePath;
    }

    public Hashtable<String, Organization> getCollection() {
        return collection;
    }
}
