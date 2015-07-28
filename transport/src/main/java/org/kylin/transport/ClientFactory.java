package org.kylin.transport;

import org.kylin.address.Address;

import java.util.List;

/**
 * Created by jimmey on 15-6-25.
 */
public interface ClientFactory {

    void create(Address address, Client.Listener listener);

    List<Client> listAll();

    void remove(Client client);

}
