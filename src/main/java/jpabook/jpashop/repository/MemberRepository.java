package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 이걸 쓰면 스프링부트가 엔티티매니저를 만들어서 주입해준다. 우리가 직접 아래처럼 생성자로 주입 해줘도 된다.
    // 그리고 스프링 데이터 jpa를 쓰면 PersistenceContext 대신 Autowired를 쓸수 있고 그러면 다른 빈처럼 일반 생성자 주입이 가능 하다.
    // 원래는 PersistenceContext로만 주입이 가능 했음.
    // @PersistenceContext
    private final EntityManager em;

    public void save(Member member) {
        // 이렇게 하면 영속성 컨텍스트(db에 적용하기 전)에 이 멤버 객체를 딱 올린다. 영속성 컨텍스트의 이 객체의 key는 memeber의 id다.
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // jpql은 sql이랑의 차이점은 sql은 테이블을 대상으로 쿼리를 하고 jpql은 엔티티 객체를 대상으로 쿼리를 날린다.
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
