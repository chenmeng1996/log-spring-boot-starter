package p.cm.system;


import org.springframework.beans.factory.annotation.Autowired;
import p.cm.system.handler.DefaultLogHandler;
import p.cm.system.handler.LogHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAudit {
    String operation() default "";
    String information() default "";
    Class<? extends LogHandler> handler() default DefaultLogHandler.class;
}
