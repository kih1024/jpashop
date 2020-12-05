package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

// 스프링을 같이 테스트 할려면 위에 두가지 필요
@RunWith(SpringRunner.class) // 이것은 junit 실행할때 스프링이랑 같이 엮어서 실행할때 사용
@SpringBootTest // 스프링부트를 띄운 상태로 테스트 할려면 꼭 필요함. 밑에 @Autowired등이 작동 안됨.
@Transactional // 테스트 롤백 할려면 이게 꼭 필요
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

//    @Autowired
//    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
//        System.out.println(member.getId()); id의 값은 널인데 왜냐하면 데이터가 영속성 컨텍스트에 반영될때 그제서야 아이디가 생성되기 때문에.
        // 따라서 save해서 영속성 컨텍스트에 들어가면 member에 id가 할당 된다.
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);
        // 영속성 컨텍스트에 이 member 객체가 들어간다.
        // 일반적으로 이렇게 한다고 해서 디비에 데이터가 반영 안된다. 데이터 베이스 트랜잭션이 커밋 될때 반영된다.
        // 여기선 커밋이 안되는데 왜냐하면 테스트케이스에서 트랜잭션이 있으면 기본적으로 커밋 false기 때문.
        // 커밋을 하는 순간 영속성 컨텍스트에 있는 멤버 객체의 인서트 문이 만들어지면서 디비에 인서트 된다.
        // 그래도 인서트문에 나가는것을 확인 하고 싶으면 @commit 또는 @rollback false를 쓰던가
        // 아니면 엔티티매니저를 주입 받아서 em.flush를 해준다 -> 이것은 디비에 강제로 커밋하지만 트랜잭션이 다시 롤백 해준다.
        // em.flush();
        // 가장 좋은것은 was와 함께 메모리 디비를 띄우는게 가장 좋다. -> 스프링 부트를 쓰면 이런걸 쉽게 할 수 있다.

        //then
        // 같은데 가능한 이유는 jpa에서 같은 트랜잭션 안에서 같은 엔티티, 즉 아이디 값이 똑같으면 같은 영속성 컨텍스트에서 똑같은 애로 관리가 된다.
        assertEquals(member, memberRepository.findOne(saveId));
//        System.out.println(member.getId()); 여기선 id가 1이 나옴.
//        Assertions.assertThat(memberRepository.findOne(saveId)).isEqualTo(member);
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        memberService.join(member2); // 이렇게 하면 정상적으로 IllegalStateException가 발생해서 테스트를 통과하게 된다.

        //then
        fail("예외가 발생해야 한다.");
    }

}