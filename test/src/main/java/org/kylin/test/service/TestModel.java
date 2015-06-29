package org.kylin.test.service;

import org.msgpack.annotation.Message;

/**
 * Created by jimmey on 15-6-24.
 */
@Message
public class TestModel {

    String name;

    int age;

    Integer time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
