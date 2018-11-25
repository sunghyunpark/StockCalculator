package model;

/**
 * 평균 매수 단가 Model
 */
public class PurchaseModel {
    private int type;    // 매수 1, 매도 -1, 미선택 0
    private int amount;    // 수량
    private long price;    // 가격

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
