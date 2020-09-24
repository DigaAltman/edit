package back.annotation;

/**
 * created by TMT
 */
public @interface Results {
    String id() default "";

    Result[] value() default {};
}
