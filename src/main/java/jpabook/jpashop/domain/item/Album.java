package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") // 싱글 테이블 입장에서 어디서 왔는지 구분하기 위해서 쓴다.
@Getter @Setter
public class Album extends Item {
    private String artist;
    private String etc;
}
