package hello.upload.testannotation.advice;

import hello.upload.testannotation.annotation.ApiSuccess;
import hello.upload.testannotation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethodAnnotation(ApiSuccess.class) != null;
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //  ApiResponse 인 경우 중복 wrapping 방지
        if (body instanceof ApiResponse) {
            return body;
        }


        ApiSuccess methodAnnotation = returnType.getMethodAnnotation(ApiSuccess.class);
        int statusCode = methodAnnotation.statusCode();
        String messageCode = methodAnnotation.message();
        String message = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());


        return ApiResponse.success(statusCode, message, body);



    }
}
