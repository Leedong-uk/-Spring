package hello.springmvc.basic.response;

import hello.springmvc.basic.HelloData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
//@Controller
@RestController
public class ResponseBodyController {

    @GetMapping("/response-body-string-v1")
    public void responseBodyV1(HttpServletResponse response) throws IOException {
         response.getWriter().write("ok");
    }


    @GetMapping("/response-body-string-v2")
    public ResponseEntity<String> responseBodyV2(HttpServletResponse response) throws IOException {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping("/response-body-string-v2")
    public String responseBodyV3(HttpServletResponse response)  {
        return "ok";
    }

    @GetMapping("/response-body-json-v1")
    public ResponseEntity<HelloData> responseBodyJsonV1 () {
        HelloData data = new HelloData();
        data.setUsername("userA");
        data.setAge(20);
        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/response-body-json-v2")
    public HelloData responseBodyJsonV2 () {
        HelloData helloData = new HelloData();
        helloData.setUsername("userA");
        helloData.setAge(20);
        return helloData;
    }

    @GetMapping("/response-body-json-v2-v1")
    public HelloData responseBodyJsonV2V1 (@RequestBody HelloData helloData) {

        helloData.setUsername("userA");
        helloData.setAge(20);
        return helloData;
    }

    @GetMapping("/response-body-json-v2-v1")
    public HelloData responseBodyJsonV2V1 (HttpEntity<HelloData> httpEntity) {
        HelloData helloData = httpEntity.getBody();
        return helloData;
    }


}
