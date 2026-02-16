package hello.upload.testannotation.dto;

import hello.upload.testannotation.annotation.ApiSuccess;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;


    //data 있는 성공 응답
    public static <T> ApiResponse<T> success(int code, String message , T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}
