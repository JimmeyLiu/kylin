package org.kylin.common.command;

/**
 * Created by jimmey on 15-7-28.
 */
public interface Command {

    public String pattern();

    public String handle(String msg);

}
