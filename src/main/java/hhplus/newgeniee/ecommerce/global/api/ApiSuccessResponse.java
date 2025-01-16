package hhplus.newgeniee.ecommerce.global.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiSuccessResponse {

	Class<?> value();

	boolean isList() default false;
}
