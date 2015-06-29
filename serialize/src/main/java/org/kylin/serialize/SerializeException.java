package org.kylin.serialize;

import java.io.IOException;

/**
 * Created by jimmey on 15-6-22.
 */
public class SerializeException extends IOException {
    public SerializeException() {
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
