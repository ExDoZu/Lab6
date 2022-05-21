package zuev.nikita.server;

/**
 * Class for errors in data stored in a file or entered by the user.
 */
public class WrongDataException extends Exception {

    public WrongDataException(String message) {
        super(message);
    }

}
