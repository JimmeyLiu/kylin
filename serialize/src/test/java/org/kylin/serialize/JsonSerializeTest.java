package org.kylin.serialize;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.kylin.serialize.model.Simple;

import java.util.Date;

/**
 * JsonSerialize Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>七月 17, 2015</pre>
 */
public class JsonSerializeTest {


    JsonSerialize serialize;
    Simple simple;
    String json = "{\"@type\":\"org.kylin.serialize.model.Simple\",\"aLong\":1,\"age\":1,\"bLong\":1,\"name\":\"json\",\"price\":4.3D,\"time\":1437107322421}";

    @Before
    public void before() throws Exception {
        serialize = new JsonSerialize();
        simple = new Simple();
        simple.setAge(1);
        simple.setName("json");
        simple.setaLong(1L);
        simple.setbLong(1);
        simple.setPrice(4.3);
        simple.setTime(new Date(1437107322421L));
    }

    /**
     * Method: serialize(Object in)
     */
    @Test
    public void testSerialize() throws Exception {
        byte[] bytes = serialize.serialize(simple);
        assertEquals(json, new String(bytes));
        Simple r = serialize.deserialize(bytes, Simple.class);
        assertEquals(r.getaLong(), simple.getaLong());
        assertEquals(r.getbLong(), simple.getbLong());
        assertEquals(r.getPrice(), simple.getPrice());
        assertEquals(r.getTime().getTime(), 1437107322421L);
    }

    /**
     * Method: deserialize(byte[] in, Class<T> classType)
     */
    @Test
    public void testDeserialize() throws Exception {
//TODO: Test goes here... 
    }


} 
