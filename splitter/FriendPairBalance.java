package splitter;

public class FriendPairBalance {
    private Person person1;
    private Person person2;
    private int balance;

    public FriendPairBalance(Person person1, Person person2) {
        this.person1 = person1;
        this.person2 = person2;
        this.balance = 0;
    }

    public Person getPerson1() {
        return person1;
    }

    public Person getPerson2() {
        return person2;
    }

    public int getBalance() {
        return balance;
    }

    public void setPerson1(Person person1) {
        this.person1 = person1;
    }

    public void setPerson2(Person person2) {
        this.person2 = person2;
    }

    public void setBalance(int balance) {
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




