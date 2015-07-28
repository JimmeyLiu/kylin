package org.kylin.protocol.codec;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.kylin.common.util.ReflectUtils;
import org.kylin.protocol.message.Request;

import java.util.Date;

/**
 * RequestCodec Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>七月 17, 2015</pre>
 */
public class RequestCodecTest {

    Request request;

    RequestCodec codec = new RequestCodec();
    Object[] args = new Object[]{new Date(), 1L, 1, "Hello"};

    @Before
    public void before() throws Exception {
        request = new Request(1);
        request.setServiceKey("hello");
        request.setMethod("say");
        request.setArgTypes(ReflectUtils.getTypes(args));
        request.setArgs(args);
    }

    @Test
    public void codec() throws Exception {
        Block block = codec.encode(request);
        Request r = codec.decode(block);
        assertEquals(r.getServiceKey(), request.getServiceKey());
        assertEquals(r.getMethod(), request.getMethod());
        assertArrayEquals(r.getArgTypes(), request.getArgTypes());
        assertArrayEquals(r.getArgs(), request.getArgs());
    }

}
