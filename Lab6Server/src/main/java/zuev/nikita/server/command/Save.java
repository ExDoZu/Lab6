package zuev.nikita.server.command;

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
    public Save(Hashtable<String, Organization> collection) {
        super(collection);
    }

    @Override
    public String execute(String arg, String savePath, Organization organization) throws FileNotFoundException {
        if (arg != null) return "Команда не нуждается в аргументе.";
        File file = new File(savePath);
        try {
            if (!file.exists()) throw new FileNotFoundException();
            if (!file.canWrite()) throw new FileNotFoundException("Нет доступа к файлу из-за нехватки прав доступа.");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(JsonDataHandler.hashtableToString(collection));
            fileWriter.close();
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        return "Данные сохранены.";
    }

    @Override
    public String getHelp() {
        return "save : сохранить коллекцию в файл";
    }
}
