package splitter;

import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private Person person1;
    private Person person2;
    private final String transactionType;
    private int amount;

    public Transaction(LocalDate date, Person person1, Person person2, String transactionType ,int amount) {
        this.date = date;
        this.person1 = person1;
        this.person2 = person2;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Person getPerson1() {
        return person1;
    }

    public void setPerson1(Person person1) {
        this.person1 = person1;
    }

    public Person getPerson2() {
        return person2;
    }

    public void setPerson2(Person person2) {
        this.person2 = person2;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }



}
