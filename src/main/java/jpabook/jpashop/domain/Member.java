package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // Member와 Order는 일대다 관계이다.
    // 연관관계에서 주인이 아니라는 뜻 즉 order.member의 거울. 실제로 db 테이블에 반영 안됨. order.member에 의해서 매핑이 됬다. 단순히 order.member에 의해서 매핑된 거울이라는 뜻.
    // 그래서 단순히 읽기 전용이다. 그래서 여기에 값을 넣는다고 해서 저 외래키 값이 변경 되지않음.
    // 즉, mappedBy는 연관관계에서 주인을 받고 외래키를 반영한 거울을 만들어줌. 이걸 써줌으로서 연관관계에서 자동으로 order.member가 주인이 된다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
