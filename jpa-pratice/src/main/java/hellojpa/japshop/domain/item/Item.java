package hellojpa.japshop.domain.item;

import hellojpa.japshop.domain.BaseEntity;
import hellojpa.japshop.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
@Setter
public abstract class Item extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    @ManyToMany(mappedBy = "items" , fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    private String name;

    private Integer price;

    private Integer stockQuantity;
}
