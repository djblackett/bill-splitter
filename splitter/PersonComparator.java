package splitter;

import java.util.Comparator;

public class PersonComparator implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        if (o1 != null && o2 != null) {
            return o1.getName().compareTo(o2.getName());
        }
        return 0;
    }
}
