package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> item = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", item);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        // 이것처럼 컨트롤러 같은 바깥에서는 식별자만 넘기고 service 계층(핵심비즈니스 로직) 단에서 엔티티를 찾는게 좋다.
        // 컨트롤러가 더 깔끔하고 service의 영속성 컨텍스트를 이용할수 있기 때문에.
        // 여기서 엔티티를 불러온다고 하면 service에 넘겨지는 그 엔티티는 영속성 텍스트에 없는놈이다.
        // 그래서 값을 수정하거나 추가하는데 있어서 애매해진다.
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    // 여기서 생각해야 할것은 이 orderList가 그냥 form일때는 submit 할때 다시 자기 자신을 불러온다는것이다.
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
