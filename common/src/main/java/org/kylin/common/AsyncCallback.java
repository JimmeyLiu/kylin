package org.kylin.common;

/**
 * Created by jimmey on 15-6-22.
 */
public interface AsyncCallback<T> {

    void on(T t);

    void onException(Exception e);

}
