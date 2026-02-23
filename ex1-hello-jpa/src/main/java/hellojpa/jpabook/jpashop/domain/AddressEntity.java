package hellojpa.jpabook.jpashop.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ADDRESS")
public class AddressEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Address address;

    public AddressEntity(Address address) {
        this.address = address;
    }

    public AddressEntity() {
    }

    public AddressEntity(String city, String street, String zipCode) {
        this.address = new Address(city, street, zipCode);
    }
}
