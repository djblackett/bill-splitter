package splitter;

import java.util.Comparator;

public class FriendPairBalanceComparator implements Comparator<FriendPairBalance> {
    @Override
    public int compare(FriendPairBalance o1, FriendPairBalance o2) {
        int value1 = o1.getPerson1().getName().compareTo(o2.getPerson1().getName());
        if (value1 == 0) {
            return o1.getPerson2().getName().compareTo(o2.getPerson2().getName());
        }
        return value1;
    }
}
