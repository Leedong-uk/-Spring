package hellojpa.japshop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Item {
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
