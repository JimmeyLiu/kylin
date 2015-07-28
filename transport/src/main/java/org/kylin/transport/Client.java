package org.kylin.transport;

import org.kylin.address.Address;

/**
 * Created by jimmey on 15-6-23.
 */
public interface Client {

    void doAsk(TransportFuture future);

    boolean isConnected();

    void close();

    Address address();

    public interface Listener {
        void onConnected(Client client);

        void onClosed(Client client);

        void onServerOffline(Client client);
    }

}
