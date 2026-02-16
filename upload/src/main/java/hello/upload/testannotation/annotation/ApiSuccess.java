package hello.upload.testannotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccess {
    int statusCode() default 200;
    String message() default "요청에 성공 하였습니다.";
}
