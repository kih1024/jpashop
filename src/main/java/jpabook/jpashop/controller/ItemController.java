package jpabook.jpashop.controller;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    // 사실 여기에도 @ModelAttribute("form") 이걸 붙여주는게 맞다. 여기서는 html 랜더링을 안해서 상관없다.
    public String create(BookForm form) {
        Book book = new Book();
        // 이렇게 setter로 일일히 넣어주는것보다 BookForm에서 crate 메소드로 set 해주는것이 더 좋다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);
        // 팁 : 멀티라인 샐렉트는 기능은 ctrl 두번 누르고 방향키
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    // 사실 @ModelAttribute("form") 이 없어도 스프링 이 자동으로 바인딩 해줘서 잘 작동함.
    // HTTP 요청을 통해서 controller에 들어올떄는 사실 BookForm 의 변수 이름은 의미가 없다,.
    //    @ModelAttribute("form") BookForm form
    //    ModelAttribute는 자동으로 model.addAttribute가 호출됩니다.
    //    그래서 model.addAttribute("form", form)이런 코드가 추가 되는 것이지요.
    //    @ModelAttribute("form") BookForm form
    //    model.addAttribute("form", form) //자동
    //    그런데 생략을 해도 되고, 생략을 하더라도 model.addAttribute가 자동으로 호출됩니다.
    //    BookForm form
    //    model.addAttribute("bookForm", form) //자동
    //    여기서 잘 봐야 하는 것이 있습니다. @ModelAttribute 자체를 생략하거나
    //    @ModelAttribute(이름) 이름을 생략하는 경우에 스프링 MVC는 BookForm이라는 클래스 이름에서 앞글자를 소문자로 만들어서 이름으로 사용합니다! 따라서 이름이 form이 아니라 bookForm이 됩니다.
    //    그래서 템플릿 뷰에서 화면을 그려야 하는데, 아마 form이라고 해서 찾을꺼에요. 그런데 이렇게 되면 form 되신에 bookForm이라는 이름이 등록되어 버리니 오류가 발생합니다^
    //    그래서 updateItem에서 결과로 view를 렌더링 해야 하면 그 경우에 오류가 발생할 수 있습니다.
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

        // 가령 아래의 book은 준영속 엔티티라고 할수 있다. jpa에 한번 들렀다 나온놈.
        // 왜냐하면 얘는 이미 db에 저장되어서 jpa가 식별할수 있는 id를 가지고 있다.
        // 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티로 볼 수 있다.
        // 이것은 jpa가 관리(감지)하는(영속성 컨텍스트에서 관리하는) 영속상태의 엔티티가 아니다.
        // 영속상태의 엔티티는 변경감지(더티체킹) 이라는것이 일어나서 데이터를 업데이트 한다. 그래서 트랜잭션 커밋시점에 db에 변경시킨다.
        // 하지만 여기의 book은 내가 직접 new해서 만들어낸 엔티티다. jpa가 기본적으로 관리 하지 않는다.
        // 그러기 때문에 값을 아무리 바꿔치기를 해도 db에 업데이트를 안한다.
        // 그래서 준영속 상태의 엔티티는 다음과 같이 itemService.saveItem를 써서 merge를 쓴다. 또는 더티체킹을 쓴다.
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
        // 이렇게 엔티티를 컨트롤러에서 어설프게 생성하는것보다 아래 방법이 더 좋다.

        // merge 방법
        // merge란, 영속성 컨텍스트,디비 에서 해당 book의 id과 같은 id를 찾는다. 파라미터로 넘긴 데이터(book)를 찾은 찾은 데이터와 완전히 다 바꿔치기 해버린다.
        // 즉 모든 데이터를 바꿔치기 해버린다. 그리고 바꿔치기 된 데이터를 반환(이것은 영속성 컨텍스트에서 관리하게 된다.).
        // 변경감지는 원하는 속성만 선택해서 변경할수 있지만, merge는
        // 모든 데이터를 변경하기 때문에 병합시 파라미터의 값이 없다면 null로 업데이트 할 위험이 있다.
        // 예를 들어, book은 한번 price를 책정하면 바뀌지 않는다고 했을때, setPrice를 안한다 치자.
        // 그러면 위에서 book 엔티티의 Price 속성은 null이 되고 null이 그대로 merge로 인해서 디비에 업데이트 되버린다.
        // 이런것을 생각하면 복잡해 질수 있다. 그래서 merge를 쓰지 말자! 더티체킹을 쓰자.
//         itemService.saveItem(book);

        // 이것은 더티체킹 방법 한가지만 쓴다. 이것이 더 나은 방법이다. 컨트롤로에서 어슬프게 Book 엔티티를 생성하지 않았다. 유지보수적으로 낫다.
        // 만약 업데이트 할것이 많다면(써야할 파라미터가 많으면) dto를 따로 만든다.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());


        return "redirect:/items";
    }
}
