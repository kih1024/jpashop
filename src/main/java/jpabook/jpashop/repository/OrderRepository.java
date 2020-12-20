package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

//    public List<Order> findAll(OrderSearch orderSearch) {
    // order o 와 연관 멤버 m을 조인한다. sql의 on 없다 o.member이 연결하는 수단위 되기 때문.
    // 테이블로 표현해야 할것을 객체로 표현 하려다 보니 이렇게 표현한거. 실제로 돌려보면 우리가 생각 하는 조인문이 나타난다.
    // 하지만 이렇게 하면 만약 status와 name이 없다면? 동작 안된다. 여기서 동적 쿼리를 이용한다.
//        return em.createQuery("select o from Order o join o.member m" +
//                "where o.status = :status" +
//                "and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) // 최대 1000건
//                .getResultList();
//      }

        // 방법1. jpql쿼리를 문자로 생성하여 붙여나간다. 하지만 비추천. 이렇게 문자를 더해서 하는거는 보기보다 더 힘들다.
        // 거의 안쓰임. pdf 참고. p60. mybatis를 쓰는 이유가 이러한 동적쿼리를 생성하기 매우 쉽다.

        // 방법2. jpa Criteria(jpql을 자바코드로 작성할수 있게 해준다. 특히 동적 쿼리를 작성할때 )를 쓰는 방법.
        // 이것도 권장 안함. 보면 알겠지만 실무에서 쓰라고 만든것이 아닐것이기 때문.
        // 무슨 sql이 만들어지는지 파악하기 어려움. 너무 복잡함. 읽는사람도 이해하기 힘들고 유지보수 하기 힘듬.

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

        // 그래서 나온것이 queryDSL 이다. 하지만 여기선 다루지는 않음. 그래서 일단 Criteria를 사용한다.

}
