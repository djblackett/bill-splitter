package splitter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

public class Group {
    private String name;
    private List<Person> groupMembers = new ArrayList<>();

    public void show() {
        groupMembers.sort(new PersonComparator());
        groupMembers.forEach(person -> System.out.println(person.getName()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Person> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void createGroup(List<Person> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public FriendGroupBalance splitPriceAmongGroup(BigDecimal price, Group group1, Person buyer) {
        BigDecimal priceTimes100 = price.multiply(BigDecimal.valueOf(100));

        group1.getGroupMembers().sort(new PersonComparator());
        FriendGroupBalance friendGroupBalance = null;


        if (group1.groupMembers.size() != 0) {

            int groupSize = groupMembers.size();

            if (group1.groupMembers.contains(buyer)) {
                groupSize--;
            }


            BigDecimal dividedPrice = priceTimes100.divideToIntegralValue(new BigDecimal(group1.groupMembers.size()));
            BigDecimal correctedPrice = dividedPrice.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            correctedPrice = correctedPrice.setScale(2, RoundingMode.DOWN);
            correctedPrice = correctedPrice.setScale(2, RoundingMode.DOWN);

            BigDecimal equalPricing = correctedPrice.multiply(new BigDecimal(group1.groupMembers.size()));
            BigDecimal remainder = price.subtract(equalPricing);

//            System.out.println("EqualPricing: " + equalPricing);
//            System.out.println("Remainder: " + remainder);
//            System.out.println("Corrected Price " + correctedPrice);

            friendGroupBalance = new FriendGroupBalance();
            BigDecimal finalCorrectedPrice = correctedPrice;
            FriendGroupBalance finalFriendGroupBalance = friendGroupBalance;
            group1.groupMembers.forEach(friend -> finalFriendGroupBalance.getGroupBalanceMap().put(friend.getName(), new BigDecimal(finalCorrectedPrice.toString())));

            if (remainder.equals(new BigDecimal("0.00"))) {
                return friendGroupBalance;
            } else {


                for (int i = 0; i < group1.groupMembers.size(); i++) {
                    Person person = group1.groupMembers.get(i);
                    String personName = person.getName();
                    Map<String, BigDecimal> map = friendGroupBalance.getGroupBalanceMap();

                    friendGroupBalance.getGroupBalanceMap().replace(personName, map.get(personName).add(new BigDecimal("0.01")));
                    remainder = remainder.subtract(new BigDecimal("0.01"));
                    if (remainder.equals(new BigDecimal("0.00"))) {
                        break;
                    }
                }
            }
        } else {
            System.out.println("Group size is zero");
        }
        return friendGroupBalance;
    }


//    public static void main(String[] args) {
//        BigDecimal price = new BigDecimal("10");
//        price = price.setScale(2, RoundingMode.DOWN);
//
//        Group group1 = new Group();
//        group1.createGroup(Arrays.asList(new Person("Jake"), new Person("Bob"), new Person("Ralph")));
//        group1.show();
//        group1.groupMembers.sort(Comparator.comparing(Person::getName));
//
//        BigDecimal priceTimes100 = price.multiply(BigDecimal.valueOf(100));
//
//        BigDecimal dividedPrice = priceTimes100.divideToIntegralValue(new BigDecimal(group1.groupMembers.size()));
//        BigDecimal correctedPrice = dividedPrice.divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
//        correctedPrice = correctedPrice.setScale(2, RoundingMode.DOWN);
//
//        BigDecimal equalPricing = correctedPrice.multiply(new BigDecimal(group1.groupMembers.size()));
//        BigDecimal remainder = price.subtract(equalPricing);
//
//        FriendGroupBalance friendGroupBalance = new FriendGroupBalance();
//        BigDecimal finalCorrectedPrice = correctedPrice;
//        group1.groupMembers.forEach(friend -> friendGroupBalance.getGroupBalanceMap().put(friend.getName(), new BigDecimal(finalCorrectedPrice.toString())));
//
//        if (remainder.equals(new BigDecimal("0.00"))) {
//            //return equalPricing
//        } else {
//
//
//            for (int i = 0; i < group1.groupMembers.size(); i++) {
//                Person person = group1.groupMembers.get(i);
//                String personName = person.getName();
//                Map<String, BigDecimal> map = friendGroupBalance.getGroupBalanceMap();
//
//                friendGroupBalance.getGroupBalanceMap().replace(personName,map.get(personName).add(new BigDecimal("0.01")));
//                remainder = remainder.subtract(new BigDecimal("0.01"));
//                if (remainder.equals(new BigDecimal("0.00"))) {
//                    break;
//                }
//            }
//        }
//
//        System.out.println(friendGroupBalance);
//        System.out.println(friendGroupBalance.getGroupBalanceMap());
//
//        System.out.println(dividedPrice);
//        System.out.println("Corrected Price: " + correctedPrice);
//        System.out.println("EqualPricing: " + equalPricing);
//        System.out.println("Remainder: " + remainder);
//        System.out.println(friendGroupBalance);
//
//
//
//
//
//
//
//
//    }

}
