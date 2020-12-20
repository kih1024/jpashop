package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        // 이렇게 하면 새로 만든 delivery와 orderItem의 정보를 order에 세팅하여 디비에 저장한다.
        // 원래대로 라면 DeliveryRepository.save()해서 디비에 저장하고 OrderItemRepository.save() 해서 디비에 저장 한다음
        // 마지막에 만들어진 2개 가지고 OrderRepository.save()해서 디비에 저장해야 한다.
        // 하지만 여기선 OrderRepository.save()만 했다.
        // 왜냐? Order에 넣어준 cascade 옵션 때문에
        // cascade = CascadeType.ALL를 하면 orderItems에다가 아이템을 넣어두고 order를 persist(디비에 반영)하면 orderItem도 같이 persist(디비 반영) 된다.
        // Order가 persist 되면 이어서 Delivery도 persist 된다.
        // cascade는 막 사용하면 안된다.
        // 여기서는 OderItem을 Order에서만 쓰서 상관없지만, 만약 OrderItem을 여기저기에서 쓴다면 cascade를 쓸 시 나도 모르게 값이 변경 될 수도 있음.
        // 그때는 따로 OrderItemRepository를 만들어서 각각 persist하는게 좋음.
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
        // 여기서 더티체킹이 일어남
        // 만약 jpa를 사용하지 않고 mybatis 같이 sql을 직접 써주는 방법을 쓸경우,
        // 이렇게 order.cancel(); 이나 상태를 변경하는 setStatus를 할 경우, 서비스 계층(이 부분에서)에서 객체 모델에서 변경한대로 디비에도 업데이트 쿼리를 날려서 일일히 변경해줘야함.
        // 하지만 jpa 같은경우 한 트랜잭션안에서 더티체킹이라는 것이 자동으로 이루어져서, 객체(엔티티) 모델만 변경해주면 즉, 엔티티안에 데이터들만 바꿔주면
        // 알아서 변경된 부분을 감지(더티체킹)를 해서 변경된 부분을 찾아 디비 업데이트 쿼리를 날려서 db 에 반영해줌. jpa의 엄청 큰 장점. 너무 편한 기능.
    }

    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
