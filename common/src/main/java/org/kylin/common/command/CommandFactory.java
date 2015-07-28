package org.kylin.common.command;

import org.kylin.common.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by jimmey on 15-7-28.
 */
public class CommandFactory {
    static Map<Pattern, Command> commandMap = new HashMap<Pattern, Command>();
    public static String SUPPORT = "";

    static {
        ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
        Iterator<Command> it = loader.iterator();
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            Command command = it.next();
            commandMap.put(Pattern.compile(command.pattern()), command);
            list.add(command.pattern());
        }
        SUPPORT = StringUtils.join(list, ",");
    }

    public static String handle(String msg) {
        for (Map.Entry<Pattern, Command> entry : commandMap.entrySet()) {
            if (entry.getKey().matcher(msg).matches()) {
                return entry.getValue().handle(msg);
            }
        }
        return null;
    }


}
