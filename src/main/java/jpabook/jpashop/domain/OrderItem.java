package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격

    private int count; // 주문 수량량

    // 이 생성자를 만들어준 이유는 외부에서
    // OrderItem orderItem = new OrderItem(); 를 하여
    // orderItem.set...();
    // 을 하지 말라고 왜냐하면 누구는 도메인 클래스에서 createOrderItem를 사용하고 누구는 바깥에서 만든다음 set 해주면 나중에 유지보수 하기 어려움
    // 그래서 이렇게 써줌으로서 외부에서 OrderItem 객체를 만드는것을 사전에 막을수 있음.
    protected OrderItem() {
    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /**
     * 주문 가격 * 주문 수량
      */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
