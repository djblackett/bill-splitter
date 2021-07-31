package splitter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Person {

    private String name;


    // Key is the date of transaction Object[] has 2 elements - otherPerson, amount (+ for borrowed, - for repayment)
    private Map<LocalDate, Object[]> transactions = new HashMap<>();


    // final solution for this stage didn't end up using the person's balance field
    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<LocalDate, Object[]> getTransactions() {
        return transactions;
    }

    public void setTransactions(Map<LocalDate, Object[]> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
