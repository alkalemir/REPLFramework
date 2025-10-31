package dev.alkal.repl.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Commands.class)
public @interface Command {
  String value() default "";
}
