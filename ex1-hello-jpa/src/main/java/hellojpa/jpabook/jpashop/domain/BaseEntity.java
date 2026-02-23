package hellojpa.jpabook.jpashop.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public class BaseEntity {

    private String createdBy;
    private LocalDateTime createDate;
    private String lastModifedBy;
    private LocalDateTime lastModifiedDate;

}
