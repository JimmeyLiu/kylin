package org.kylin.console;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jimmey on 15-6-30.
 */
public class Portal {

    @RequestMapping
    public ModelAndView index() {

        return new ModelAndView("");
    }

}
