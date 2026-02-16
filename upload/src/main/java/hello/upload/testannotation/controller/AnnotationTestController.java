package hello.upload.testannotation.controller;

import hello.upload.testannotation.annotation.ApiSuccess;
import hello.upload.testannotation.dto.MemberDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test-annotation")
@RestController
public class AnnotationTestController {

    @ApiSuccess( message = "success.test")
    @GetMapping("")
    public MemberDto testAnnotation() {
        return new MemberDto("test@naver.com", 20);
    }
}
