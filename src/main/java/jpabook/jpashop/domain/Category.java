package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    // 다대다 관계에서는 조인 테이블이 필요하다. 관계형 db에서 다대다관계를 표현하려면
    // 중간테이블이 필요 하기 때문에. 일대다 다대일로 풀어내는 중간테이블이 필요하다.
    // 이것은 실전에서는 쓰지말자. 왜냐하면 더 필드를 추가하거는 그런게 불가능하기 때문에. 실무에서 거의 못씀.
    // 이것은 중간테이블에서 외래키를 넣고 이름을 설정하기 위해서
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
    // 카테고리 구조는 어떻게 하지?
    // 셀프로 양방향 연관관계를 건거다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();


    // == 양방향 연관관계 편의 매서드 ==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }


}
