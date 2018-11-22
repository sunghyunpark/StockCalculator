package model;

/*
* 복리 계산 결과에 사용되는 Model
*
 */
public class CompoundInterestModel {
    private int no;    // 회차
    private long sum;    // 원금
    private double rate;    // 수익률

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
