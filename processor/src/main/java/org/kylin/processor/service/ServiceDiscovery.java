package org.kylin.processor.service;

import org.kylin.address.Address;
import org.kylin.address.AddressFactory;
import org.kylin.address.AddressService;
import org.kylin.transport.Client;
import org.kylin.transport.ClientFactory;
import org.kylin.transport.ClientFactoryProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmey on 15-7-28.
 * 服务发现相关处理，包括
 * 1. 权重调整
 * 2. 分组调整
 * 3.
 */
public class ServiceDiscovery {

    RoundRobin<Client> roundRobin;
    AddressService addressService;
    Client.Listener listener;
    ClientFactory clientFactory;

    public ServiceDiscovery(String serviceKey) {
        this.roundRobin = new RoundRobin<Client>();
        this.addressService = AddressFactory.getAddressService();
        this.clientFactory = ClientFactoryProvider.get();
        this.listener = new Client.Listener() {
            @Override
            public void onConnected(Client client) {
                roundRobin.add(client);
            }

            @Override
            public void onClosed(Client client) {
                roundRobin.remove(client);
            }

            @Override
            public void onServerOffline(Client client) {
                roundRobin.remove(client);
            }
        };
        addressService.lookup(serviceKey, new AddressService.Callback() {
            @Override
            public void on(List<Address> strings) {
                List<Address> list = new ArrayList<Address>(strings);
                for (Client t : roundRobin.getElements()) {
                    if (list.contains(t.address())) {
                        list.remove(t.address());
                    } else {
                        t.close();
                    }
                }
                for (Address address : list) {
                    if (clientFactory != null) {
                        clientFactory.create(address, listener);
                    }
                }
            }
        });
    }

    public Client get() {
        return roundRobin.next();
    }

}
