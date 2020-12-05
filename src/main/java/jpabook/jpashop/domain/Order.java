package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블 바꿔주고 위해서 이 어노테이션을 써줌. 원래대로라면 order로 들어가는데 sql의 order by와 충돌이 날수 있음.
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // Order와 Member는 다대일 관계이다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 외래키를 설정하는 어노테이션. 아래 속성을 외래키로 정한다. 보통 다쪽에 외래키를 설정한다.
    // 그리고 매핑은 뭘로 할거냐? member_id. 이렇게 하면 외래키의 이름이 member_id가 된다.
    // 이것과 member.orders와 연관관계를 맺었으면 누굴 보고 외래키를 업데이트를 해야할까? jpa는 고민하게 된다.
    // order.member 만 바뀔수도 있고 member.orders 만 바뀔수도 있다. 하지만 두개는 연관관계에 의해 연결 되어있다. 그럼 누굴 보고 외래키를 업데이트를 해야하나?
    // 둘중에 하나만 선택하게 jpa에서 약속을 했다. 따라서 주인을 정해줘야함. 왜냐하면 객체는 변경포인트가 윗줄처럼 두곳인데 테이블은 외래키 하나만 변경하면 된다. 그래서 이걸 맞춘거다.
    // 외래키를 정했으면 둘중의 하나를 연관관계의 주인으로 정해야한다. 대부분 외래키있는쪽을 주인으로 잡는게 좋다. 왜냐하면 외래키가 있는 테이블이 자신을 직접 바꾸기 때문에 직관적이고 복잡하지 않다.
    // 만약 member를 주인으로 잡게되면 member.orders을 바꿨는데 다른 테이블인 order 테이블의 외래키가 바뀌면서 헷갈리게 된다.
    // 그래서 이녀석을 연관관계의 주인으로 잡으면 된다.
    // 이 녀석이 주인이기 때문에 여기에 값을 세팅하면 member_id인 외래키 값이 변경 되면서 다른 멤버로 변경이 된다. 즉 외래키 값을 변경하려면 여길 변경해야함.
    private Member member;

    // JPQL select o from order o; -> SQL select * from order 100 + 1(order) : 이것은 n+1 문제이다.
    // fetch 타입이 EAGER이면 이것은 조인해서 한번에 member를 가져오는게 아니라 order의 데이터가 100개 있다면 100번의 단건 쿼리를 member에 날려서 member를 조회 한다.
    // 즉 처음에 조인해서 한번에 조회 하는것이 아닌 order를 조회할때 연관관계인 member에 값을 가져오는 쿼리를 날려서 하나의 order를 구할때마다 member를 구하는 쿼리를 날린다.
    // 그래서 여러 문제가 발생할수 있고 그러니까 LAZY를 무조건 사용한다! 실무에서는 EAGER 쓰면 안된다!
    // 지연로딩(LAZY)로 바꿔놓으면 순수히 order만 조회한다. 이때 member도 필요해서 한방 쿼리로 같이 끌고 오고 싶으면 fetch join, 엔티티 그래프라는 것을 쓴다.
    // 여기서 xToOne 의 관계는 기본이 EAGER라서 LAZY로 바꾸고 oneToX는 기본이 LAZY 바꿀 필요 x

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // OrderItem.order에 의해서 매핑이 된다는 뜻.
    // cascade = CascadeType.ALL를 하면 orderItem에다가 아이템을 넣어두고 order를 저장하면 orderItems도 같이 저장이 된다.
    // 즉, 3개의 아이템을 넣었다면
    // persist(oderItemA)
    // persist(oderItemB)
    // persist(oderItemC)
    // persist(order)
    // 이렇게 각각 논리적으로 persist호출하고 order를 저장 하는것과 같다(하지만 읽기전용이라 이렇게 3번 저장하는것은 데이터 변경에 영향이 없다).
    // 하지만 cacade를 하면 persist(order)이것만 써주면 된다.
    // 그리고 위와 차이점은 mappedBy를 써서 읽기만 가능하다는 것에서 cascade를 쓰면 자식 엔티티에서 데이터 변경이 가능하다는 부가적인 기능이 생긴다.

    // 하지만! cascade는 mappedBy,양방향 등과 전혀 관계가 없으니
    // 복잡하게 엮어서 생각하지 말자. 단순히 A엔티티(order)를 persist할때 B 엔티티(orderItem)도 연쇄적으로 함께 persist 한다고 생각하자.
    // mappedBy는 양방향 연관관계에서 FK(외래키값)을 누가 관리할지에 대한 내용입니다.
    // 반면에 cascade는 A를 영속화 할 때 B도 함께 영속화 할지에 대한 내용입니다. 따라서 둘은 완전히 관계가 없다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL) // 일대일 관계에서는 외래키를 order에 넣어도 되고 delivery에 넣어도 되지만, 그래도 접근이 많은 테이블에 넣는게 낫다.
    // 따라서 연관관계의 주인을 외래키와 가까운 order에 둔다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime localDate; // 자바8 이상에서는 하이버네이트가 알아서 LocalDateTime를 지원해줌.

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    // == 양방향 연관관계 편의 매서드 ==//
    // db에서는 연관관계 주인인 member만 바꾸면 반영되지만 자바 코드에서는
    // 엔티티 객체 양쪽 다 바꾸는 것이 논리적으로 맞기 때문에 메서드를 만들었다.
    // 원자적으로 한번에 양방향 연관관계에서의 양쪽 엔티티들을 세팅할때 쓴다.
    // 이렇게 setter를 만들게 되면 바깥에서 order.setMember(member)를 호출만 해도 연관관계인 member의 orders list에도 반영된다.
    // 이 연관관계 편의 매서드의 위치는 핵심적으로 어플리케이션을 컨트롤 하는쪽에서 들고 있는것이 좋다.
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);

    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
