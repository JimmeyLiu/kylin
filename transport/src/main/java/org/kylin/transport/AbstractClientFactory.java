package org.kylin.transport;


import org.kylin.protocol.address.Address;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jimmey on 15-6-25.
 */
public abstract class AbstractClientFactory implements ClientFactory {

    ConcurrentMap<Address, Client> pool = new ConcurrentHashMap<Address, Client>();

    @Override
    public void create(final Address uri, Client.Listener listener) {
        Client client = pool.get(uri);
        if (client == null) {
            createClient(uri, new Client.Listener() {
                @Override
                public void onConnected(Client client) {
                    pool.putIfAbsent(uri, client);
                }

                @Override
                public void onClosed(Client client) {
                    remove(client);
                }
            }, listener);
        }
    }

    protected abstract void createClient(Address uri, Client.Listener... listener);

    @Override
    public List<Client> listAll() {
        return new ArrayList<Client>(pool.values());
    }

    @Override
    public void remove(Client client) {
        pool.remove(client.address());
    }
}
