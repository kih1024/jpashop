package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // validation을 해주기 때문에 빈 껍데기라도 보낸다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") // @Valid 어노테이션을 쓰면 MemberberForm의 validate를 해줌
    // BindingResult를 쓰면 원래는 @Valid MemberForm form 에서 에러가 있으면 튕겨 버리지만 BindingResult가 있으면 에러를 여기다가 담고 아래 메소드를 실행한다.
    // BindingResult 안에는 에러를 찾을수 있는 메소드가 많다.
    // MemberForm 는 일종의 dto(data transfer object)다.
    // MemberForm 를 사용하냐? 실제 Member 엔티티 객체를 사용하냐?
    // 간단한 경우에는 Member 엔티티를 사용할 수 있으나, 대부분의 경우 Member 엔티티는 MemberForm과 다를수 밖에 없기 때문에 MemberForm를 따로 만들어서 쓰는것이 좋다.
    // 그리고 Member 엔티티를 폼으로 써버리면 엔티티에 화면을 처리하기 위한 기능을 계속 추가해야 한다. 결과적으로 엔티티가 화면 종속적인 기능이 생긴다. 앤티티가 지저분해 진다. 유지보수 하기 어려워진다.
    // jpa를 쓸때 조심해야 하는것이, 엔티티를 최대한 순수하게 유지해야 한다. 뭔가 어디에 dependency가 없이 핵심 비지니스로직에서 dependency가 있도록 해야한다. 화면의 대한 로직은 없어야한다.
    // 그래서 화면에 처리하는 api나 이런것은 form 객체나 dto를 사용한다. 엔티티를 최대한 순수하게 유지해야 한다.
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            // 이렇게 하면 createMemberForm 페이지로 돌아가고 name input 아래 에러메시지가 출력 된다.
            // 타임리프와 스프링부트가 다 잘 integration(통합) 되어있어서
            // BindingResult 의 result를 createMemberForm에 긁어와서 쓸 수 있게 해준다.
            // 에러가 있어도 form 데이터를 가져가기 때문에 다시 로딩되도 에러가 없는 폼은 그대로 가지고 있다.
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; // return "home" 처럼 재로딩하는건 안좋기 때문에 리다이랙트를 했다.
    }

    @GetMapping("/members")
    public String list(Model model) {
        // inline으로 헀다. 변수 합치기. ctrl + shift + alt + t 를 통해서 리팩토링 설정으로 들어가서 인라인 선택.
        // 여기서 보면 List<Member> 타입의 리스트처럼 멤버 엔티티를 그대로 뿌리고 있지만 실무적으로 복잡해 지면 dto를 따로 만들어서
        // 화면에 꼭 필요한 데이터들만 가지고 출력하는것을 권장한다.
        // 그리고 여기서는 어차피 서버내에서 내가 원하는 데이터만 출력하는것이기 때문에 괜찮은데(서버 템플릿 엔진에서는)
        // api를 만들때는 이유를 불문하고 절대 엔티티를 넘기면 안됨.
        // 즉 api를 만들때는 절대 외부로 엔티티를 노출하면 안된다.
        // 왜냐면, 엔티티에 password를 추가한다고 했을때, password가 노출될뿐만아니라,
        // api는 스펙인데 엔티티에 로직을 추가하면 api의 스펙이 변한다. 그럼 엄청 불안전한 api가 된다.
        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }
}
