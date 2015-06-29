package org.kylin.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by jimmey on 15-6-23.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Provider {
}
