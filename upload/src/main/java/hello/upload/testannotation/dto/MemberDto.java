package hello.upload.testannotation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private String memberEmail;
    private int age;
}
