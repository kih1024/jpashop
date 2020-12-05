package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // item의 id가 있을려면 기존에 엔티티 객체를 persist를 해서 영속성 컨텍스트에 반영 됬어야 한다.
    // 따라서 처음 생성된 엔티티 객체는 당연히 id가 null일 수 밖에 없고
    // id가 널이 아니면 그전에 영속성 컨텍스트에 해당 엔티티 객체가 반영됬다는것이고 이것은 결국 db에 커밋 됬을 것이고
    // 그래서 merge라는 것을 쓰면 업데이트를 한다고 일단 생각하면 된다.
    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
