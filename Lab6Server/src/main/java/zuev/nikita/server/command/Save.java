package zuev.nikita.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zuev.nikita.server.JsonDataHandler;
import zuev.nikita.structure.Organization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Saves the collection to the file.
 */
public class Save extends Command {
    private final static Logger log = LoggerFactory.getLogger(Save.class);

    public Save(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) {
        if (arg != null) return "Команда не нуждается в аргументе.";
        File file = new File(savePath);
        try {
            if (!file.exists()) {
                log.error("Collection is not saved. File '" + savePath + "'is not found");
                return "nofile";
            }
            if (!file.canWrite()) {
                log.error("Collection is not saved. No access to file '" + savePath + "'");
                return "noaccess";
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(JsonDataHandler.hashtableToString(collection));
            fileWriter.close();
            log.info("Collection is successfully saved");
        } catch (IOException e) {
            log.error("Collection is not saved. File '" + savePath + "'is not found or no access to it");
            return "fail";
        }
        return "ok";
    }

    @Override
    public String getHelp() {
        return "save : сохранить коллекцию в файл";
    }
}
