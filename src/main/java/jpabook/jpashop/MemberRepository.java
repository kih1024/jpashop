package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    // 스프링 부트가 이 어노테이션이 있으면 엔티티 매니저를 주입을 해준다. 그냥 쓰면 된다. 이 엔티티메니저는 스프링부트가 알아서 만들어 줬다.
    // starter-data-jpa를 주입하면 application.yml을 읽어들여 알아서 EntityManager를 만들어줌.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }


}
