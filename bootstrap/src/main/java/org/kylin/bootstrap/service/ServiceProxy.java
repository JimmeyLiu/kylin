package org.kylin.bootstrap.service;

import org.kylin.protocol.address.Address;
import org.kylin.protocol.address.AddressFactory;
import org.kylin.protocol.address.AddressNotFoundException;
import org.kylin.protocol.address.AddressService;
import org.kylin.common.AsyncCallback;
import org.kylin.common.util.ReflectUtils;
import org.kylin.protocol.message.Mid;
import org.kylin.protocol.message.Request;
import org.kylin.protocol.message.Response;
import org.kylin.serialize.SerializeFactory;
import org.kylin.trace.ResultCode;
import org.kylin.trace.Trace;
import org.kylin.transport.Client;
import org.kylin.transport.ClientFactory;
import org.kylin.transport.ClientFactoryProvider;
import org.kylin.transport.TransportFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        addressService.lookup(service, version, new AsyncCallback<Set<Address>>() {
            @Override
            public void on(Set<Address> strings) {
                List<Address> list = new ArrayList<Address>(strings);
                for (Client t : roundRobin.getElements()) {
                    if (list.contains(t.address())) {
                        list.remove(t.address());
                    } else {
                        t.close();
                    }
                }
                for (Address address : strings) {
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
        Trace.startRpc();
        ResultCode code = ResultCode.OK;
        try {
            Client client = roundRobin.next();
            if (client == null) {
                throw new AddressNotFoundException(service + "." + method.getName() + "@" + version);
            }

            Request request = new Request(client.address().getSerializeType());
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
        } catch (Throwable e) {
            code = ResultCode.ERROR;
            throw e;
        } finally {
            Trace.end(code);
        }
    }

}
