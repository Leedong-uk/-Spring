package hellojpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Member {

    @Id
    private Long id;
    @Column(name="name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;

    @Lob
    private String description;

    public Member() {
    }


}
