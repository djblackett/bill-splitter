package splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/*
    Make a map of date (for easy sorting) and string value. The string will contain the
    transaction and will be parsed with string methods to do the calculations.

    UNles...

    create person objects as new people are referenced
    use filter() to get the correct groups of transactions when  need them.
    Make a transaction class and keep a list outside of the person objects. WIll make filter() easier to use.
    May need to keep track of who was person1 already so we don't do redundant transactions

    /7/28

    New though. Store a list of objects for each person. Date, other person, and change in value from the original person's perspective.


 */




public class Main {
    static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    static List<String> commandList = new ArrayList<>(Arrays.asList("balance", "borrow", "exit", "help", "repay"));


    public static void main(String[] args) throws IOException {
        Map<String, Person> personMap = new HashMap<>();
        List<Transaction> transactionList = new ArrayList<>();
        Set<FriendPairBalance> friendPairBalances = new HashSet<>();

//        Person bob = new Person("Bob");
//        Person ann = new Person("Ann");
//
//        personMap.put("Ann", bob);
//        personMap.put("Bob", ann);


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

            //split array
            String[] splitLine = input.split(" ");
            if (!isFirstArgumentDate(splitLine) && !commandList.contains(splitLine[0])) {
                unknownCommand();
                continue;
            }
            //String command

            String command = getCommand(splitLine);
            int commandIndex = Arrays.asList(splitLine).indexOf(command);

            // Calculate balance for each pair of friends
            if (command.equals("balance")) {

                //show balance
                boolean isOpen = splitLine.length > commandIndex + 1 && splitLine[commandIndex + 1].equals("open");
                LocalDate date;

                // How to seperate friend pairs ???
                // Create a list of duple sets so I can check if they contain... something?
                // or create filtered lists sort of like sql queries

                // Iterate through transaction list, not through personMap



//                Person person1 = personMap.get("Bob");
//                Person person2 = personMap.get("Ann");
                List<LocalDate> dates = new ArrayList<>();
                //int balance = 0;

                if (isFirstArgumentDate(splitLine)) {
                    date = LocalDate.parse(splitLine[0], dateTimeFormatter);
                } else {
                    date = LocalDate.now();
                }

                LocalDate targetDate;
                List<Transaction> balancePeriodTransactionList;
                // Calculates balance up to the end of the last month. (This month's opening balance)
                if (isOpen) {
                    targetDate = LocalDate.of(date.getYear(), date.getMonth(), 1);

                    balancePeriodTransactionList = transactionList.stream().filter(transaction -> transaction.getDate().isBefore(targetDate)).collect(Collectors.toList());
                } else {
                            targetDate = date;
                            balancePeriodTransactionList = transactionList.stream().filter(transaction -> transaction.getDate().compareTo(targetDate) <= 0).collect(Collectors.toList());
                }




                for (Transaction t: balancePeriodTransactionList) {
                        Person person1 = t.getPerson1();
                        Person person2 = t.getPerson2();

                        // Get friend pairing for given transactions

                        FriendPairBalance pair = getPairByPeople(person1, person2, friendPairBalances);


                        // The pair balance is always from the perspective of person 1
                        if (t.getTransactionType().equals("borrow")) {

                            if (person1.equals(pair.getPerson1())) {
                                pair.setBalance(pair.getBalance() + Math.abs(t.getAmount()));
                            } else {
                                pair.setBalance(pair.getBalance() - Math.abs(t.getAmount()));
                            }

                        }

                        if (t.getTransactionType().equals("repay")) {

                            if (person1.equals(pair.getPerson1())) {
                                pair.setBalance(pair.getBalance() - Math.abs(t.getAmount()));
                            } else {
                                pair.setBalance(pair.getBalance() + Math.abs(t.getAmount()));
                            }



                        }
                    }

                        List<FriendPairBalance> results = friendPairBalances.stream().filter(pair -> pair.getBalance() != 0).collect(Collectors.toList());
                        FriendPairBalance result = null;

                        if (results.isEmpty()) {
                            print("No repayments need");
                        } else if (results.get(0).getBalance() > 0) {
                            result = results.get(0);
                            print(result.getPerson1().getName() + " owes " + result.getPerson2().getName() + " " + Math.abs(result.getBalance()));
                        } else {
                            result = results.get(0);
                            print(result.getPerson2().getName() + " owes " + result.getPerson1().getName() + " " + Math.abs(result.getBalance()));
                        }

                        // Debugging print statments
                        if (result != null) {
                            print(result.toString());
                        }
                    // replace with filter method
//                    for (LocalDate d: person1.getTransactions().keySet()) {
//                        if (d.isBefore(firstOfMonth)) {
//                            dates.add(d);
//                           // balance += Integer.parseInt(person1.getTransactions().get(d)[1]);
//                        }
//                    }

//                    if (balance == 0 || dates.isEmpty()) {
//                        print("No repayments need");
//                    } else if (balance < 0) {
//                        print(person1.getName() + " owes " + person2.getName() + " " + Math.abs(balance));
//                    } else {
//                        print(person2.getName() + " owes " + person1.getName() + " " + Math.abs(balance));
//                    }
                continue;
                }

                // Calculates balance up to the end of the exact date given
//                else {
//
//                    List<Transaction> closingDateTransactionList = transactionList.stream().filter(transaction -> transaction.getDate().compareTo(date) <= 0).collect(Collectors.toList());
//
//                    // Use above filter method
//                    for (LocalDate d: person1.getTransactions().keySet()) {
//                        if (d.compareTo(date) <= 0) {
//                            dates.add(d);
//                          //  balance += Integer.parseInt(person1.getTransactions().get(d)[1]);
//                        }
//                    }
//                    if (balance == 0 || dates.isEmpty()) {
//                        print("No repayments need");
//                    } else if (balance < 0) {
//                        print(person1.getName() + " owes " + person2.getName() + " " + Math.abs(balance));
//                    } else {
//                        print(person2.getName() + " owes " + person1.getName() + " " + Math.abs(balance));
//                    }
//                }



            //  Borrowing Money
            if (command.equals("borrow") || command.equals("repay")) {
                //borrow money
                //int commandIndex = Arrays.asList(splitLine).indexOf("borrow");
                if (splitLine.length < commandIndex + 4) {
                    print("Illegal command arguments");
                    continue;
                }
                // repay money
                String person1 = splitLine[commandIndex + 1];
                String person2 = splitLine[commandIndex + 2];
                int amount = Integer.parseInt(splitLine[commandIndex + 3]);

                if (!personMap.containsKey(person1)) {
                    personMap.put(person1, new Person(person1));
                }

                if (!personMap.containsKey(person2)) {
                    personMap.put(person2, new Person(person2));
                }

                Person personObj1 = personMap.get(person1);
                Person personObj2 = personMap.get(person2);

                LocalDate date;

                if (isFirstArgumentDate(splitLine)) {
                    date = LocalDate.parse(splitLine[0], dateTimeFormatter);
                } else {
                    date = LocalDate.now();
                }

                if (person1 != null && person2 != null) {
                    transactionList.add(new Transaction(date, personObj1, personObj2, command, amount));

                } else {
                    print("Illegal command arguments");
                }
                continue;
            }
//            // Repaying money
//            if (command.equals("repay")) {
//                int repayIndex = Arrays.asList(splitLine).indexOf("repay");
//                if (splitLine.length < repayIndex + 4) {
//                    print("Illegal command arguments");
//                    continue;
//                }
//
//
//                // repay money
//                Person person1 = personMap.get(splitLine[repayIndex + 1]);
//                Person person2 = personMap.get(splitLine[repayIndex + 2]);
//                int amount = Integer.parseInt(splitLine[repayIndex + 3]);
//                LocalDate date;
//
//                if (isFirstArgumentDate(splitLine)) {
//                    date = LocalDate.parse(splitLine[0], dateTimeFormatter);
//                } else {
//                    date = LocalDate.now();
//                }
//
//                if (person1 != null && person2 != null) {
//                    person1.setBalance(person1.getBalance() - amount);
//                    person1.getTransactions().put(date, new String[]{person2.getName(), "-" + amount});
//                    person2.setBalance(person2.getBalance() + amount);
//                    person2.getTransactions().put(date, new String[]{person1.getName(), String.valueOf(amount)});
//                } else {
//                    print("Illegal command arguments");
//                }
//                continue;
//            }
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

    public static void repay(String person1, String person2, Integer amount) {
       // Person personOne =
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
