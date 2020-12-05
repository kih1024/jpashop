package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException{
    // 이것들을 오버라이드 해줘야한다.

    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
