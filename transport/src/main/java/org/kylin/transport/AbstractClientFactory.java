package org.kylin.transport;


import org.kylin.address.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jimmey on 15-6-25.
 */
public abstract class AbstractClientFactory implements ClientFactory {

    ConcurrentMap<Address, Client> pool = new ConcurrentHashMap<Address, Client>();

    ReentrantLock lock = new ReentrantLock();

    @Override
    public Client create(final Address address) {
        Client client = pool.get(address);
        if (client == null) {
            lock.lock();
            try {
                client = pool.get(address);
                if (client == null) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    createClient(address, new Client.Listener() {
                        @Override
                        public void onConnected(Client client) {
                            pool.putIfAbsent(address, client);
                            latch.countDown();
                        }

                        @Override
                        public void onClosed(Client client) {
                            remove(client);
                        }

                        @Override
                        public void onServerOffline(Client client) {
                            //ignore
                        }
                    });
                    try {
                        latch.await(address.getConnectTimeout() + 100, TimeUnit.MILLISECONDS);
                        client = pool.get(address);
                    } catch (Exception e) {
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return client;
    }

    @Override
    public void create(final Address address, Client.Listener listener) {
        Client client = pool.get(address);
        if (client == null) {
            createClient(address, new Client.Listener() {
                @Override
                public void onConnected(Client client) {
                    pool.putIfAbsent(address, client);
                }

                @Override
                public void onClosed(Client client) {
                    remove(client);
                }

                @Override
                public void onServerOffline(Client client) {
                    //ignore
                }
            }, listener);
        } else {

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
