package splitter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class FriendGroupBalance {
    private Map<String, BigDecimal> groupBalanceMap = new TreeMap<>();

    public FriendGroupBalance() {
    }

    public Map<String, BigDecimal> getGroupBalanceMap() {
        return groupBalanceMap;
    }

    public void setGroupBalanceMap(Map<String, BigDecimal> groupBalanceMap) {
        this.groupBalanceMap = groupBalanceMap;
    }

    @Override
    public String toString() {
        return "FriendGroupBalance{" +
                "groupBalanceMap=" + groupBalanceMap +
                '}';
    }
}
