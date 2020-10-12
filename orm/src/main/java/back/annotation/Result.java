package back.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface Result {
    boolean id() default false;

    String column();

    String property();

    String select() default "";
}
