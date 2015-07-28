package org.kylin.test.address;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kylin.address.Address;
import org.kylin.address.AddressService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jimmey on 15-6-23.
 */
public class LocalAddressService implements AddressService {

    File root;
    ScheduledExecutorService executorService;

    public LocalAddressService() {
        this.root = new File("/tmp/address");
        try {
            if (!root.exists()) {
                root.mkdirs();
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

    Map<String, Long> modified = new ConcurrentHashMap<String, Long>();

    @Override
    public void lookup(final String serviceKey, final Callback callback) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Long last = modified.get(serviceKey);
                    File file = new File(root, serviceKey);
                    if (!file.exists()) {
                        return;
                    }
                    if (last != null && last == file.lastModified()) {
                        return;
                    }
                    modified.put(serviceKey, file.lastModified());
                    List<Address> uris = new ArrayList<Address>();
                    List<String> list = IOUtils.readLines(new FileInputStream(file));
                    for (String l : list) {
                        Address address = Address.parse(l);
                        uris.add(address);
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
    public void register(String serviceKey, String address) {
        modify(serviceKey, address, true);
    }

    @Override
    public void unregister(String serviceKey, String address) {
        modify(serviceKey, address, false);
    }

    private void modify(String serviceKey, String address, boolean append) {
        try {
            File file = new File(root, serviceKey);
            if (!file.exists()) {
                file.createNewFile();
            }
            List<String> list = IOUtils.readLines(new FileInputStream(file));
            while (list.remove(address)) ;
            if (append) {
                list.add(address);
            }
            OutputStream out = new FileOutputStream(file, false);
            IOUtils.write(StringUtils.join(list, "\n").getBytes(), out);
            IOUtils.closeQuietly(out);
        } catch (Exception e) {

        }
    }
}
