package zuev.nikita.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zuev.nikita.server.JsonDataHandler;
import zuev.nikita.server.net.Connection;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;


/**
 * Saves the collection to the file.
 */
public class Save extends Command {
    private final static Logger log = LoggerFactory.getLogger(Save.class);

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (arg != null) return "Команда не нуждается в аргументе.";
        File file = new File(savePath);
        try {
            if (!file.exists()) {
                log.error("Collection is not saved. File '" + savePath + "'is not found");
                return "One of files is not found to save a collection.";
            }
            if (!file.canWrite()) {
                log.error("Collection is not saved. No access to file '" + savePath + "'");
                return "One of files is not accessible to save a collection.";
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(JsonDataHandler.hashtableToString(collection));
            fileWriter.close();
            log.info("Collection is successfully saved");
        } catch (IOException e) {
            log.error("Collection is not saved. File '" + savePath + "'is not found or no access to it");
            return "One of files is not saved.";
        }
        return "ok";
    }

    @Override
    public String serverExecute(String arg, HashMap<SocketChannel, Connection> connections) {
        for (SocketChannel key : connections.keySet()) {
            collection = connections.get(key).getCollection();
            String response = execute(null, connections.get(key).getFilePath(), null);
            if (!response.equals("ok")) {
                return response;
            }
        }
        return "All collections are successfully saved.";
    }

    @Override
    public String getHelp() {
        return "save : save collection to the file";
    }
}
