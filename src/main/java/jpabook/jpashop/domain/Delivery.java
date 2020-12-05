package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // enum을 쓰면 꼭 @Enumerated 써줘야한다. 그리고 EnumType는 ordinary 를 쓰지말고
    // STRING으로 써줘야 한다. 왜냐하면 ordinary는 중간이 다른 enum 속성이 들어오면 밀리기 때문에. comp가 2였다가 3으로 될수 있음.
    private DeliveryStatus status; //READY, COMP
}
