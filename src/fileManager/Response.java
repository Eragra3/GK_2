package fileManager;

/**
 * Created by bider_000 on 06.11.2015.
 */
public class Response {

    public String message;

    public Object content;

    public boolean success;

    public Response(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    public Response(boolean success, String message, Object content) {
        this.message = message;
        this.content = content;
        this.success = success;
    }
}
