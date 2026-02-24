package jpql.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "ORDERS_ID")
    private Long id;

    private Integer orderAmount;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;


}
