package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 기본적으로 데이터를 변경할때는 트랜잭션이 있어야하고 그래야 LAZY 로딩 등이 된다.
@Transactional(readOnly = true)
@RequiredArgsConstructor // 이녀석은 알다시피 final 붙어있는 필드의 생성자를 생성해준다. 실무에 많이 씀.
public class MemberService {

//    @Autowired
//    private MemberRepository memberRepository; // 이렇게 많이 쓰지만 단점이 있다면 이것을 못바꾼다. 바꾸고 싶을때는 setter 인젝션을 쓴다.

    private final MemberRepository memberRepository; // 이 녀석은 변경할 일이 없기 때문에 파이널로 정해주면 좋다. 파이널을 넣으면 컴파일 시점에 체크를 해주기 때문에 좋다.

    // @Autowired // 궁극적으로 생성자 주입이 좋다. 이렇게 하면 생성자에서 주입을 해준다. 테스트케이스를 작성할때, MemberService를 생성할때 꼭 빈을 주입해야 하기 때문에 빼먹지 않을수 있다.
    // 그리고 요즘 생성자가 하나면 @Autowired를 생략 할 수 있다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //    @Autowired // 세터 인젝션의 장점은 임의의 빈을 주입해줄수 있다. 하지만 어플리케이션이 들어가는 시점에 누군가가 memberRepository를 바꿀수 있어서 치명적이다.
                // 하지만 그럴일은 거의 없다. 왜냐하면 보통 실행적은 어플리케이션 조립을 다 해놓고 돌리기 때문.
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    // @Transactional(readOnly = true)
    // 조회 하는것에다가 이렇게 쓰면 jpa가 조회하는데에 있어서 성능을 좀 더 최적화 한다. 그래서 읽기에는 가급적 readOnly = true를 써준다.
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 단건 조회
    // @Transactional(readOnly = true)
    public Member findOne(Long MemberId) {
        return memberRepository.findOne(MemberId);
    }


}
