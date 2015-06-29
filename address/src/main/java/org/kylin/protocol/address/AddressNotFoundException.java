package org.kylin.protocol.address;

/**
 * Created by jimmey on 15-6-23.
 */
public class AddressNotFoundException extends Exception {
    public AddressNotFoundException() {
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressNotFoundException(Throwable cause) {
        super(cause);
    }
}
