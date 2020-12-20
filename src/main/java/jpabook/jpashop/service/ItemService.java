package jpabook.jpashop.service;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    // 보면 단순하게 상품서비스를 상품리파지토리에 위임만 하는 코드다

    private final ItemRepository itemRepository;

    // merge 방법
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // 더티체킹 방법 : itemRepository.save(item)를 할 필요 없이 이 트랜잭션안에서 엔티티를 불러와서 값만 변경해주면 더티체킹이 일어나서
    // 트랜잭션이 끝나는시점에 db에 반영해줌.
    // 즉, 리파지토리에서 아이템 엔티티를 불러오면 영속성 컨택스트안에 findItem 영속성 앤티티가 있고 트랜잭션이 끝날때, flush(영속성 엔티티중에서 변경된 애들을 찾는다)를 하게 된다.
    // 이때, 바뀐게 있다면 바뀐것을 업데이트 쿼리를 날려서 디비를 자동으로 업데이트 해줌.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        // 사실 이렇게 setter 말고 Item 엔티티에서 관리하는 update 메소드를 둬서 값을 설정하는게 낫다. setter를 여러군데서 쓰게 되는 코드는 어디에서 값을 변경하는지 탐지 하기 힘들기때문에
        // 예를들어,
        // findItem.change(name,price,stockQuantity);
        // 이렇게라도 만들어서 이안에서 값을 변경하는것이 낫다. 그래서 change만 엮으로 뒤지기만 어디서 변경하는지 찾을 수 있기 때문에.
        // 한곳으로 정해주는것이 좋음. 하지만 여기서는 그냥 간단히 setter를 사용함.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
