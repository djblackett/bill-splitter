package splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    static List<String> commandList = new ArrayList<>(Arrays.asList("balance", "borrow", "exit", "help", "repay"));


    public static void main(String[] args) throws IOException {
        Map<String, Person> personMap = new HashMap<>();
        List<Transaction> transactionList = new ArrayList<>();
        Set<FriendPairBalance> friendPairBalances = new HashSet<>();


        while (true) {

            String input = getInput().trim();


            if (input.equals("help")) {
                print("balance\n" +
                        "borrow\n" +
                        "exit\n" +
                        "help\n" +
                        "repay");
                continue;
            }

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
                LocalDate date;

                // Check if date arg was given
                if (isFirstArgumentDate(splitLine)) {
                    date = LocalDate.parse(splitLine[0], dateTimeFormatter);
                } else {
                    date = LocalDate.now();
                }

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

                        if (person1 == pair.getPerson1()) {
                            pair.setBalance(pair.getBalance() + Math.abs(t.getAmount()));
                        } else {
                            pair.setBalance(pair.getBalance() - Math.abs(t.getAmount()));
                        }

                    }

                    if (t.getTransactionType().equals("repay")) {

                        if (person1 == pair.getPerson1()) {
                            pair.setBalance(pair.getBalance() - t.getAmount());
                        } else {
                            pair.setBalance(pair.getBalance() + t.getAmount());
                        }
                    }
                }

                // Filter list so only pairs of friends with outstanding balances remain
                List<FriendPairBalance> results = friendPairBalances.stream().filter(pair -> pair.getBalance() != 0).collect(Collectors.toList());
                FriendPairBalance result;

                // Check if everyone is up to date with payments
                if (results.isEmpty()) {
                    print("No repayments need");
                    continue;
                }

                // Sorting results first by person owing, then by person owed.
                results.sort(new FriendPairBalanceComparator());
                // Must sort results here


                // Gather results into a new list for printing all at once
                List<String> printList = new ArrayList<>();

                for (int i = 0; i < results.size(); i++) {

                    // Determine who owes whom the balance
                    if (results.get(i).getBalance() > 0) {
                        result = results.get(i);
                        printList.add(result.getPerson1().getName() + " owes " + result.getPerson2().getName() + " " + Math.abs(result.getBalance()));
                    } else {
                        result = results.get(i);
                        printList.add(result.getPerson2().getName() + " owes " + result.getPerson1().getName() + " " + Math.abs(result.getBalance()));
                    }
                }

                // Print final balances
                // Reset friend pair balances (transactions are unaffected)
                printList.forEach(System.out::println);
                results.forEach(pair -> pair.setBalance(0));

                continue;
            }


            //  Borrowing Money
            if (command.equals("borrow") || command.equals("repay")) {

                // Check for valid args list
                if (splitLine.length < commandIndex + 4) {
                    print("Illegal command arguments");
                    continue;
                }

                // repay money
                String person1 = splitLine[commandIndex + 1];
                String person2 = splitLine[commandIndex + 2];
                int amount = Integer.parseInt(splitLine[commandIndex + 3]);

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

        List<FriendPairBalance> filteredList = friendPairBalances.stream().filter(pair -> pair.getPerson1() == person1 && pair.getPerson2() == person2 ||
                pair.getPerson1() == person2 && pair.getPerson2() == person1).collect(Collectors.toList());

        FriendPairBalance pair;
        if (filteredList.size() == 0) {
            pair = new FriendPairBalance(person1, person2);
            friendPairBalances.add(pair);
        } else {
            pair = filteredList.get(0);
        }

        return pair;
    }
}