package splitter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Person {

    private String name;
    private int balance;

    // Key is the date of transaction Object[] has 2 elements - otherPerson, amount (+ for borrowed, - for repayment)
    private Map<LocalDate, Object[]> transactions = new HashMap<>();


    // final solution for this stage didn't end up using the person's balance field
    public Person(String name) {
        this.name = name;
        this.balance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Map<LocalDate, Object[]> getTransactions() {
        return transactions;
    }

    public void setTransactions(Map<LocalDate, Object[]> transactions) {
        this.transactions = transactions;
    }

//    @Override
//    public String toString() {
//        return "Person{" +
//                "name='" + name + '\'' +
//                '}';
//    }
}
