package splitter;

import java.math.BigDecimal;

public class FriendPairBalance {
    private final Person person1;
    private final Person person2;
    private BigDecimal balance;

    public FriendPairBalance(Person person1, Person person2) {
        this.person1 = person1;
        this.person2 = person2;
        this.balance = new BigDecimal(0);
    }

    public Person getPerson1() {
        return person1;
    }

    public Person getPerson2() {
        return person2;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "FriendPairBalance{" +
                "person1=" + person1 +
                ", person2=" + person2 +
                ", balance=" + balance +
                '}';
    }
}




