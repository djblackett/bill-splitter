package splitter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Person {


    private String name;


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
