package back.annotation;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();
}
