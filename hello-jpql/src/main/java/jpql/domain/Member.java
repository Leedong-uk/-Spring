package jpql.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    private Integer age;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;


}
