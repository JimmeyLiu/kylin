package org.kylin.processor.command;

import org.kylin.common.command.Command;
import org.kylin.processor.service.ServiceFactory;

/**
 * Created by jimmey on 15-7-28.
 */
public class ListCommand implements Command {
    @Override
    public String pattern() {
        return "ls|ll";
    }

    @Override
    public String handle(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("As Consumer: \n");
        for (String str : ServiceFactory.getConsumers()) {
            sb.append(str).append("\n");
        }
        sb.append("\n");
        sb.append("As Provider: \n");
        for (String str : ServiceFactory.getProviders()) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }
}
