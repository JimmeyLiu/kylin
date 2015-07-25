package org.kylin.protocol.codec;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.kylin.protocol.message.Response;
import org.kylin.serialize.SerializeFactory;

/**
 * ResponseCodec Tester.
 *
 * @author Jim
 * @version 1.0
 * @since 七月 17, 2015
 */
public class ResponseCodecTest {

    ResponseCodec codec = new ResponseCodec();

    Response response;

    @Before
    public void before() throws Exception {
        response = new Response(0);
        response.setStatus(200);
//        response.setException("hello");
        response.setResult("fdsa");
        response.setResultType(String.class);
    }

    /**
     * Method: encodePayload(Response message)
     */
    @Test
    public void testEncodePayload() throws Exception {
        Block block = codec.encode(response);
        Response r = codec.decode(block);

        assertEquals(response.getException(), r.getException());
        assertEquals(response.getResult(), SerializeFactory.deserialize(response.getSerializeType(), r.getResultBytes(), String.class));
    }


    /**
     * Method: instance(int serializeType)
     */
    @Test
    public void testInstance() throws Exception {
        //TODO: Test goes here... 
    }


    /**
     * Method: decodePayload(Response response, byte[] payload)
     */
    @Test
    public void testDecodePayload() throws Exception {
        //TODO: Test goes here... 
    }


} 
