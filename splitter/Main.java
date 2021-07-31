package splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/*
    Random notes for myself:
    create person objects as new people are referenced
    use filter() to get the correct groups of transactions when  need them.
    Make a transaction class and keep a list outside of the person objects. WIll make filter() easier to use.
    May need to keep track of who was person1 already so we don't do redundant transactions
 */


public class Main {
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    static List<String> commandList = new ArrayList<>(Arrays.asList("add", "balance", "borrow", "exit", "help", "remove", "repay", "group", "purchase"));


    public static void main(String[] args) throws IOException {
        Map<String, Person> personMap = new HashMap<>();
        List<Transaction> transactionList = new ArrayList<>();
        Set<FriendPairBalance> friendPairBalances = new HashSet<>();
        Map<String, Group> groupMap = new HashMap<>();

        while (true) {

            String input = getInput().trim();


            if (input.equals("help")) {
                print("balance\n" +
                        "borrow\n" +
                        "exit\n" +
                        "group\n" +
                        "help\n" +
                        "purchase\n" +
                        "repay");
                continue;
            }

            // exit function to break out of loop and quit
            if (input.equals("exit")) {
                exit();
            }

            //split input into array for parsing
            String[] splitLine = input.split(" ");
            if (!isFirstArgumentDate(splitLine) && !commandList.contains(splitLine[0])) {
                unknownCommand();
                continue;
            }


            String command = getCommand(splitLine);
            int commandIndex = Arrays.asList(splitLine).indexOf(command);

            // Calculate balance for each pair of friends
            if (command.equals("balance")) {

                //show balance for each person
                boolean isOpen = splitLine.length > commandIndex + 1 && splitLine[commandIndex + 1].equals("open");

                // Check if date arg was given
                LocalDate date = getDateFromArgOrDefault(splitLine);

                LocalDate targetDate;
                List<Transaction> balancePeriodTransactionList;


                // Filters list by date up to the end of the last month. (This month's opening balance)
                if (isOpen) {
                    targetDate = LocalDate.of(date.getYear(), date.getMonth(), 1);

                    balancePeriodTransactionList = transactionList.stream().filter(transaction -> transaction.getDate().isBefore(targetDate)).collect(Collectors.toList());
                }

                // Filter list by date before closing (inclusive)
                else {
                    targetDate = date;
                    balancePeriodTransactionList = transactionList.stream().filter(transaction -> transaction.getDate().compareTo(targetDate) <= 0).collect(Collectors.toList());
                }


                // Iterate through filtered transaction list to calculate all the balances.
                for (Transaction t : balancePeriodTransactionList) {
                    Person person1 = t.getPerson1();
                    Person person2 = t.getPerson2();

                    // Get friend pairing for given transactions
                    FriendPairBalance pair = getPairByPeople(person1, person2, friendPairBalances);


                    // The pair balance is always from the perspective of person 1
                    // The nested conditional ensures it works regardless of the ordering of the 2 people in the pair
                    if (t.getTransactionType().equals("borrow")) {

                        if (person1.equals(person2)) {
                            continue;
                        }

                        if (person1.equals(pair.getPerson1())) {
                            pair.setBalance(pair.getBalance().add(t.getAmount().abs()));
                        } else {
                            pair.setBalance(pair.getBalance().subtract(t.getAmount().abs()));
                        }

                    }

                    if (t.getTransactionType().equals("repay")) {

                        if (person1 == pair.getPerson1()) {
                            pair.setBalance(pair.getBalance().subtract(t.getAmount()));
                        } else {
                            pair.setBalance(pair.getBalance().add(t.getAmount()));
                        }
                    }
                }

                // Filter list so only pairs of friends with outstanding balances remain
                List<FriendPairBalance> results = friendPairBalances.stream().filter(pair -> pair.getBalance().intValue() != 0).collect(Collectors.toList());
                FriendPairBalance result;


                // Check if everyone is up to date with payments
                if (results.isEmpty()) {
                    print("No repayments need");
                    continue;
                }


                // Sorting results first by person owing, then by person owed.
                results.sort(new FriendPairBalanceComparator());

                // Gather results into a new list for printing all at once
                List<String> printList = new ArrayList<>();

                for (FriendPairBalance friendPairBalance : results) {

                    // Determine who owes whom the balance
                    if (friendPairBalance.getBalance().intValue() > 0) {
                        result = friendPairBalance;
                        printList.add(result.getPerson1().getName() + " owes " + result.getPerson2().getName() + " " + result.getBalance().abs());
                    } else {
                        result = friendPairBalance;
                        printList.add(result.getPerson2().getName() + " owes " + result.getPerson1().getName() + " " + result.getBalance().abs());
                    }
                }

                // Print final balances
                // Reset friend pair balances (transactions are unaffected)
                printList.forEach(System.out::println);
                results.forEach(pair -> pair.setBalance(new BigDecimal(0)));

                continue;
            }



            //  Borrowing and repaying money - this only records transactions. Processing occurs in "balance" section above
            if (command.equals("borrow") || command.equals("repay")) {

                // Check for valid args list
                if (splitLine.length < commandIndex + 4) {
                    print("Illegal command arguments");
                    continue;
                }

                // Parse input args
                String person1 = splitLine[commandIndex + 1];
                String person2 = splitLine[commandIndex + 2];
                String amountString = splitLine[commandIndex + 3];
                if (!amountString.contains(".")) {
                    amountString = amountString.concat(".00");
                }
                BigDecimal amount = new BigDecimal(amountString);

                // Create person objects if not yet instantiated
                if (!personMap.containsKey(person1)) {
                    personMap.put(person1, new Person(person1));
                }

                if (!personMap.containsKey(person2)) {
                    personMap.put(person2, new Person(person2));
                }

                // Retrieve person objects
                Person personObj1 = personMap.get(person1);
                Person personObj2 = personMap.get(person2);

                LocalDate date;

                // Check if date arg is provided. If not, date is set to current date.
                if (isFirstArgumentDate(splitLine)) {
                    date = LocalDate.parse(splitLine[0], dateTimeFormatter);
                } else {
                    date = LocalDate.now();
                }

                // Record transaction and add to list
                if (person1 != null && person2 != null) {
                    Transaction transaction = new Transaction(date, personObj1, personObj2, command, amount);
                    transactionList.add(transaction);
                } else {
                    print("Illegal command arguments");
                }
            }

            if (splitLine[0].equals("group")) {

                // 4 group functions defined below: create, show, add, remove


                if (splitLine[1].equals("create")) {
                    commandIndex = Arrays.asList(splitLine).indexOf("create");
                    String groupName = splitLine[commandIndex + 1];
                    //System.out.println(groupName);

                    // Group name must be all caps
                    if (groupName.matches("[A-Z]+")) {
                        String[] splitInputForGroup = input.split(" \\(");
                        String friends = splitInputForGroup[1];
                        friends = friends.substring(0, friends.length() - 1);
                        String[] friendArray = friends.split(", ");

                        // Get list of people from String[]
                        // View the function implementation for details
                        List<Person> groupList = parsePersonList(friendArray,groupMap, personMap);

                        // Create group with memberList and add to map for future access
                        Group group = new Group();
                        group.createGroup(groupList);
                        group.setName(groupName);
                        groupMap.put(groupName, group);

                        // Ensure each group member is in personMap for future access
                        for (Person p :group.getGroupMembers()) {
                            if (!personMap.containsValue(p)) {
                                personMap.put(p.getName(), p);
                            }
                        }

                    } else {
                        print("Illegal command arguments");
                    }
                    continue;
                }


                if (splitLine[1].equals("show")) {

                    // Print group members
                    String groupName = splitLine[2];
                    if (groupMap.containsKey(groupName)) {
                        Group group = groupMap.get(groupName);
                        group.show();
                    } else {
                        print("Unknown group");
                    }
                    continue;
                }

                if (splitLine[1].equals("add")) {

                    // Parse string into its components
                    String groupName = splitLine[2];
                    String newMembers = input.split(" \\(")[1];
                    Group group = groupMap.get(groupName);
                    String s = newMembers.substring(0, newMembers.length() - 1);
                    String[] newMemberArray = s.split(", ");

                    // Reuse utility function to parse the new member array
                    List<Person> peopleToAdd = parsePersonList(newMemberArray,groupMap, personMap);
                    group.getGroupMembers().addAll(peopleToAdd);
                    group.getGroupMembers().sort(new PersonComparator());
                    //System.out.println(group.getGroupMembers());

                }

                if (splitLine[1].equals("remove")) {

                    // Parse String into its components
                    String groupName = splitLine[2];
                    String newMembers = input.split(" \\(")[1];
                    Group group = groupMap.get(groupName);
                    String s = newMembers.substring(0, newMembers.length() - 1);
                    String[] newMemberArray = s.split(", ");


                    // invert the prepended - and + signs in order to reuse the utility function
                    for (int i = 0; i < newMemberArray.length; i++) {
                        String name = newMemberArray[i];

                        if (name.startsWith("-")) {
                            name = name.replace("-", "+");
                        }

                        else if (name.matches("[a-zA-Z]+")) {
                            name = name.replace("+", "");
                            name = "-" + name;
                        }
                        newMemberArray[i] = name;
                    }

                    // Now the parser returns the list of who we want removed
                    List<Person> removalList = parsePersonList(newMemberArray, groupMap, personMap);

                    // Remove unwanted people
                    group.getGroupMembers().removeAll(removalList);
                }
            }

            if (command.equals("purchase")) {
                // Parse input into components
                // itemBought is not actually used yet
                String buyerName = splitLine[commandIndex + 1];
                String itemBought = splitLine[commandIndex + 2];
                BigDecimal amount = new BigDecimal(splitLine[commandIndex + 3]);

                String groupName = input.split(" \\(")[1];
                groupName = groupName.substring(0, groupName.length() - 1);
                String[] friendGroup = groupName.split(", ");


                // Parse people to split the money amongst
                List<Person> groupOfFriendsList = parsePersonList(friendGroup, groupMap, personMap);

                Group group = new Group();
                group.setGroupMembers(groupOfFriendsList);

                // Ensure buyer is in personMap for future access
                if (!personMap.containsKey(buyerName)) {
                    personMap.put(buyerName, new Person(buyerName));
                }
                Person buyer = personMap.get(buyerName);

                // Check input for a given date
                LocalDate date = getDateFromArgOrDefault(splitLine);

                // Get the balances for each group member and create transaction
                FriendGroupBalance groupBalance = group.splitPriceAmongGroup(amount, group, buyer);
                for (Person p : group.getGroupMembers()) {
                    if (p.equals(buyer)) {
                        continue;
                    }

                    // Set proper rounding for money amount
                    BigDecimal bd = groupBalance.getGroupBalanceMap().get(p.getName());
                    bd = bd.setScale(2, RoundingMode.HALF_UP);

                    // Create transaction and add to list
                    Transaction t = new Transaction(date, p, buyer, "borrow", bd);
                    transactionList.add(t);
                    //System.out.println(groupBalance.getGroupBalanceMap().get(p.getName()));
                }
            }
            continue;
        }
    }

    public static String getInput() throws IOException {
        return bufferedReader.readLine();
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static void unknownCommand() {
        print("Unknown command. Print help to show commands list");
    }

    public static void exit() {
        System.exit(0);
    }

    public static boolean isFirstArgumentDate(String[] line) {
        String firstArgument = line[0];
        return firstArgument.matches("\\d{4}\\.\\d{2}\\.\\d{2}");
    }

    public static String getCommand(String[] line) {
        if (isFirstArgumentDate(line)) {
            return line[1];
        } else {
            return line[0];
        }
    }

    public static FriendPairBalance getPairByPeople(Person person1, Person person2, Set<FriendPairBalance> friendPairBalances) {

        // Makes sure that only one friendPairBalance object exists for each pairing of people, regardless of who is person1 or person2
        List<FriendPairBalance> filteredList = friendPairBalances.stream().filter(pair -> pair.getPerson1().equals(person1) && pair.getPerson2().equals(person2) ||
                pair.getPerson1().equals(person2) && pair.getPerson2().equals(person1)).collect(Collectors.toList());

        // if no pairing exists, create one
        FriendPairBalance pair;
        if (filteredList.size() == 0) {
            pair = new FriendPairBalance(person1, person2);
            friendPairBalances.add(pair);
        } else {
            pair = filteredList.get(0);
        }

        return pair;
    }

    public static LocalDate getDateFromArgOrDefault(String[] args) {
        LocalDate date;

        // Check if date arg was given. If not, default to today's date.
        if (isFirstArgumentDate(args)) {
            date = LocalDate.parse(args[0], dateTimeFormatter);
        } else {
            date = LocalDate.now();
        }
        return date;
    }

    public static List<Person> parsePersonList(String[] friendArray, Map<String, Group> groupMap, Map<String, Person> personMap) {

        // Essentially, everyone from the friend array is added to the list initially. This includes group names.
        // Through a series of filters, we whittle the list down to exactly what is required.
        List<Person> groupPersonList = Arrays.stream(friendArray).map(Person::new).collect(Collectors.toList());


        // Collect indices and modified names to avoid concurrent modification exception
        Map<Integer, String> nameModificationMap = new HashMap<>();



        // remove + signs from person names and add people from groups
        List<Person> collectedPeopleFromGroupsList = new ArrayList<>();

        groupPersonList.forEach(person -> {
            if (person.getName().startsWith("+") && !person.getName().matches("-[A-Z]+")) {
                nameModificationMap.put(groupPersonList.indexOf(person), person.getName().substring(1));
            }

            else if (person.getName().matches("\\+?[A-Z]+")) {
                String possibleGroupName = person.getName().replace("+", "");
                if  (groupMap.containsKey(possibleGroupName)) {
                    collectedPeopleFromGroupsList.addAll(groupMap.get(possibleGroupName).getGroupMembers());
                }
            }
        });

        // This likely changes the memory reference for the updated person objects to something other than what may be in groupMap
        // I overrode the equals() and hashCode() methods in the Person class to be able to compare by value
        for (Map.Entry<Integer, String> entry :nameModificationMap.entrySet()) {
            groupPersonList.set(entry.getKey(), new Person(entry.getValue()));
        }

        groupPersonList.addAll(collectedPeopleFromGroupsList);



        // Gather names for removal - attempting to avoid concurrent modification exceptions while not forcing the app to use a single thread
        List<Person> groupRemovalNames = new ArrayList<>();

        groupPersonList.forEach(person -> {
            if (person.getName().matches("-[A-Z][a-z]*") && personMap.containsKey(person.getName().substring(1))) {
                groupRemovalNames.add(personMap.get(person.getName().substring(1)));
            }
        });


        // Remove individual names prepended with "-"
        groupPersonList.removeIf(person -> person.getName().matches("-[A-Z][a-z]*"));


        // Remove entire group of people - We are treating the group as a person for simplicity.
        // Finds each member of the group and adds them to removal list
        groupPersonList.forEach(person -> {

            if (person.getName().matches("-[A-Z]+")) {
                String s = person.getName().substring(1);
                if (groupMap.containsKey(s)) {
                    // groupPersonList.removeAll(groupMap.get(person.getName()).getGroupMembers());
                    groupRemovalNames.addAll(groupMap.get(s).getGroupMembers());
                }
            }
        });

        groupPersonList.removeAll(groupRemovalNames);


        // remove any leftover group names from list
        groupPersonList.removeIf(person -> person.getName().matches("[+-]?[A-Z]+"));

        // Remove any possible duplicates from list - may be unnecessary
        Set<Person> groupSet = new HashSet<>(groupPersonList);
        return new ArrayList<>(groupSet);
    }

}