package zuev.nikita.message;

import java.io.Serializable;

/**
 * Client receives an object of this class from the server
 */
public class ServerResponse implements Serializable {
    private final String response;
    private final int statusCode;

    public static final int OK = 0;
    public static final int WRONG_DATA = 1;
    public static final int WRONG_FILE_STRUCTURE = 2;
    public static final int WRONG_COMMAND = 3;
    public static final int WRONG_FILE_PATH = 4;
    public static final int NO_ACCESS_TO_FILE = 5;


    public ServerResponse(String response, int statusCode) {
        this.response = response;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}
