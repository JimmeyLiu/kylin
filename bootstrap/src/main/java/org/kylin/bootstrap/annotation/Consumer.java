package org.kylin.bootstrap.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by jimmey on 15-6-23.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Consumer {

    String version() default "1.0.0";

}
