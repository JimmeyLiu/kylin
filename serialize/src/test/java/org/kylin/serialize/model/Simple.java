package org.kylin.serialize.model;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jimmey on 15-7-17.
 */
@Message
public class Simple implements Serializable {

    String name;
    int age;
    Integer size;
    Double price;
    Date time;
    Long aLong;
    long bLong;

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public long getbLong() {
        return bLong;
    }

    public void setbLong(long bLong) {
        this.bLong = bLong;
    }
}
