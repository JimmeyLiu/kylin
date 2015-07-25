package org.kylin.test.address;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kylin.protocol.address.Address;
import org.kylin.protocol.address.AddressService;
import org.kylin.common.AsyncCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jimmey on 15-6-23.
 */
public class LocalAddressService implements AddressService {

    File file;
    ScheduledExecutorService executorService;

    public LocalAddressService() {
        this.file = new File("/tmp/address.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            executorService = Executors.newSingleThreadScheduledExecutor();
        } catch (Exception e) {
            //
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void lookup(final String service, final String version, final AsyncCallback<Set<Address>> callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Set<Address> uris = new HashSet<Address>();
                    List<String> list = IOUtils.readLines(new FileInputStream(file));
                    String key = service + "@" + version + ":";
                    for (String l : list) {
                        if (l.startsWith(key)) {
                            Address address = new Address(l.substring(key.length()));
                            uris.add(address);
                        }
                    }
                    callback.on(uris);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executorService.schedule(this, 10, TimeUnit.SECONDS);
                }
            }
        });
    }

    @Override
    public void register(String service, String version, String address) {
        modify(service, version, address, true);
    }

    @Override
    public void unregister(String service, String version, String address) {
        modify(service, version, address, false);
    }

    private void modify(String service, String version, String address, boolean append) {
        try {
            String value = service + "@" + version + ":" + address;
            List<String> list = IOUtils.readLines(new FileInputStream(file));
            while (list.remove(value)) ;
            if (append) {
                list.add(value);
            }
            OutputStream out = new FileOutputStream(file, false);
            IOUtils.write(StringUtils.join(list, "\n").getBytes(), out);
            IOUtils.closeQuietly(out);
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        AddressService addressService = new LocalAddressService();
        addressService.register("org.jim.rpc.test.service.TestService", "1.0.0", "127.0.0.1:8999");

        addressService.lookup("org.jim.rpc.test.service.TestService", "1.0.0", new AsyncCallback<Set<Address>>() {
            @Override
            public void on(Set<Address> uris) {
                System.out.println(uris);
            }

            @Override
            public void onException(Exception e) {

            }
        });
    }
}
