package org.kylin.transport;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by jimmey on 15-6-25.
 */
public abstract class AbstractClientFactory implements ClientFactory {

    ConcurrentMap<URI, Client> pool = new ConcurrentHashMap<URI, Client>();

    @Override
    public void create(final URI uri, Client.Listener listener) {
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

    protected abstract void createClient(URI uri, Client.Listener... listener);

    @Override
    public List<Client> listAll() {
        return new ArrayList<Client>(pool.values());
    }

    @Override
    public void remove(Client client) {
        pool.remove(client.uri());
    }
}
