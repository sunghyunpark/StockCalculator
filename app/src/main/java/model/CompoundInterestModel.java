package model;

/*
* 복리 계산 결과에 사용되는 Model
*
 */
public class CompoundInterestModel {
    private String no;    // 회차
    private String sum;    // 원금
    private String rate;    // 수익률

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

}
