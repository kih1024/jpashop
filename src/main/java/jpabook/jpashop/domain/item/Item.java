package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype") // 하나의 테이블로 때려 박았기 때문에 이 컬럼으로 구분한다.
@Getter @Setter
// 상속 관계 매핑이기 때문에 중요한걸해야한다. 그것은 상속관계 전략을 지정해야 한다.
// 나는 싱글 테이블 전략(한 테이블에 다 때려 놓는 성능상 이점이 있다.)을 쓸거다. 이 전략을 부모 클래스에 잡아줘야 한다.
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    // 엔티티 자체가 해결할수 있는 문제는 엔티티안에 비즈니스 로직을 넣는것이 좋다
    // 우리는 외부에서 stock을 계산하고 setStock을 하는식으로 해서 값을 넣었을 것이다.
    // 하지만 객체 지향 프로그래밍 관점에서 이렇게 엔티티안에 비즈니스 로직을 만드는것이 응집력이 좋다. 그래서 이렇게 하는 것이 좋다.
    // 엔티티만 이용하는 비즈니스 로직은 이 안에서 하자! 그게 가장 객체 지향적

    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stodk 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}
