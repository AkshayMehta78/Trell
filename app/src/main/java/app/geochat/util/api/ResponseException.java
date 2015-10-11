package app.geochat.util.api;

/**
 * Created by akshaymehta on 30/07/15.
 */
public class ResponseException extends Exception {
    private static final long serialVersionUID = 1L;
    public static final String tag = ResponseException.class.getSimpleName();
    private String mMessage;

    public ResponseException(String message) {
        super(message);
        mMessage = message;
    }

    @Override
    public String toString() {
        return mMessage;
    }

}