package org.kylin.bootstrap.service;

import org.kylin.protocol.address.AddressFactory;
import org.kylin.protocol.address.AddressNotFoundException;
import org.kylin.protocol.address.AddressService;
import org.kylin.common.AsyncCallback;
import org.kylin.common.util.ReflectUtils;
import org.kylin.protocol.message.Mid;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.serialize.SerializeFactory;
import org.kylin.transport.Client;
import org.kylin.transport.ClientFactory;
import org.kylin.transport.ClientFactoryProvider;
import org.kylin.transport.TransportFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

/**
 * Created by jimmey on 15-6-22.
 */
public class ServiceProxy implements InvocationHandler {

    RoundRobin<Client> roundRobin;
    AddressService addressService;
    Client.Listener listener;
    ClientFactory clientFactory;
    String service;
    String version;

    public ServiceProxy(String service, String version) {
        this.service = service;
        this.version = version;
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
        };
        addressService.lookup(service, version, new AsyncCallback<Set<URI>>() {
            @Override
            public void on(Set<URI> strings) {
                for (Client t : roundRobin.getElements()) {
                    if (strings.contains(t.uri())) {
                        strings.remove(t.uri());
                    } else {
                        t.close();
                    }
                }
                for (URI address : strings) {
                    if (clientFactory != null) {
                        clientFactory.create(address, listener);
                    }
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });
    }

    @Override
    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
        Client client = roundRobin.next();
        if (client == null) {
            throw new AddressNotFoundException(service + "." + method.getName() + "@" + version);
        }
        final int serializeType = 1;
        Request request = new Request(serializeType);
        request.setMid(Mid.next());
        request.setService(service);
        request.setMethod(method.getName());
        request.setArgTypes(ReflectUtils.getArgTypes(method.getParameterTypes()));
        request.setArgs(args);

        TransportFuture future = TransportFuture.create(request);
        client.doAsk(future);
        Response response = future.get();
        if (response.getException() != null) {
            throw new Exception("Invoke error " + response.getException());
        }
        byte[] result = response.getResultBytes();
        if (result != null && result.length > 0) {
            return SerializeFactory.deserialize(response.getSerializeType(), result, method.getReturnType());
        }
        return null;
    }

}
