package org.kylin.transport;

import java.net.URI;

/**
 * Created by jimmey on 15-6-23.
 */
public interface Client {

    void doAsk(TransportFuture future);

    boolean isConnected();

    void close();

    URI uri();

    public interface Listener {
        void onConnected(Client client);

        void onClosed(Client client);
    }

}
