package org.kylin.processor.command;

import org.kylin.common.command.Command;

/**
 * Created by jimmey on 15-7-28.
 */
public class GracefulCommand implements Command {
    @Override
    public String pattern() {
        return "online|offline";
    }

    @Override
    public String handle(String msg) {
        if ("online".equalsIgnoreCase(msg)) {

        } else if ("offline".equalsIgnoreCase(msg)) {

        }
        return "done";
    }
}
