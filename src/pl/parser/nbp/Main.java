package pl.parser.nbp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by Maciej Tomczyk.
 */
class Main {

    public static void main(String[] args) throws Exception {

        //Required collections
        List<String> totalDates;
        List<String> listOfTables;
        List<String> results;
        Parser parser = new Parser();
        String currency;
        List<String> Currencies = Arrays.asList("USD", "EUR", "CHF", "GBP");
        //Checking if needed parameters are provided
        if (args.length != 3) {
            System.out.println("Wrong parameters!");

        } else {
            //Checking if given currency is acceptable
            if (!Currencies.contains(args[0])) {
                System.out.println("Wrong currency given");
            } else {
                currency = args[0];
                DateTimeFormatter InitialDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                LocalDate DateFrom;
                LocalDate DateTo;
                try {
                    DateFrom = LocalDate.parse(args[1], InitialDateFormat);
                    DateTo = LocalDate.parse(args[2], InitialDateFormat);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Wrong format of the given dates");
                }
                //Check if the second date is before the first given
                //If yes we swap the dates
                if (DateTo.isBefore(DateFrom)) {
                    LocalDate tmp = DateFrom;
                    DateFrom = DateTo;
                    DateTo = tmp;
                }
                //Fetching dates between two given
                totalDates = parser.getDates(DateFrom, DateTo);

                listOfTables = parser.getFileNames(totalDates);

                if (listOfTables.size() > 0) {
                    //Printing results
                    results = parser.parseFiles(listOfTables, currency);
                    System.out.println(results.get(0));
                    System.out.println(results.get(1));
                } else {
                    System.out.println("Sorry! No results found! Please pick a different date range");
                }

            }

        }
    }
}
