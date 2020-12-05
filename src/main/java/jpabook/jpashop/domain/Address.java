package jpabook.jpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

// 내장 타입이라는것을 정의 해주기 위해서 쓴다. 내장 타입을 쓽때는 @Embeddable나 @Embedded 중 둘줄 하나만 있으면 된다. 근데 두개 다 써줌
@Embeddable
@Getter
//@NoArgsConstructor // 기본생성자 생성. 기본생성자를 protected로 바꾸야해서 안쓴다. public 써도 된다.
// 하지만 더 안전하게 쓰기 위해서
@AllArgsConstructor // 모든 필드값을 포함한 생성자 생성.
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }
}
