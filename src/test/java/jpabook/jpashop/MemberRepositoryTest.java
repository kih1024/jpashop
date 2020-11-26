package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

// 아래 어노테이션 스프링과 관련된 테스트를 할거란걸 알려준다.
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    // 엔티티 메니저를 통한 모든 데이터 변경은 항상 트랜잭션안에서 이루어져야 함.
    // 알다시피 이 트랜잭션 어노테이션이 테스트케이스에 있으면 테스트가 끝난다음에 디비를 원래대로 롤백해줌. 왜냐하면 다른 테스트도 돌려야 해서
    // 반영하고 싶으면 @Commit 또는 Rollback(false)를 쓴다.
    @Transactional
    @Test
    @Rollback(false)
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");
        
        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        // 같은 트랜젹션 즉, 같은 영속성 컨텍스트(testMember)에서 식별자(id)가 같으면 같은 엔티티로 인식한다.
        // 영속성 컨텍스트를 쭉 확인한다음 있으면 같은 식별자의 엔티티가 있으면 캐시에서 불러오는식.
        Assertions.assertThat(findMember).isEqualTo(member);
        System.out.println("findMember == member: " + (findMember == member));
    }

}