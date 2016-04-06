package pl.parser.nbp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Maciej Tomczyk.
 */
class Parser {
    //Loads xml file as a dom object
    private Document loadDocument(String url) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

    public List<String> getFileNames(List<String> dates) throws IOException {
        URL url;
        //Checking the given year to search for the correct dir file
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int givenYear = Integer.parseInt("20"+dates.get(0).substring(0,2));

        if(year == givenYear){
            url = new URL("http://www.nbp.pl/kursy/xml/dir.txt");
        }else{
            url = new URL("http://www.nbp.pl/kursy/xml/dir"+givenYear+".txt");
        }
        List<String> listOfTables = new ArrayList<>();


        Scanner FileScanner;
        try {
            FileScanner = new Scanner(url.openStream());
        } catch (IOException e) {
            throw new IOException("DIR FILE NOT FOUND!");
        }
        while (FileScanner.hasNext()) {
            //Saving only the names of the files with required data
            String line = FileScanner.next();
            if ('c' == line.charAt(0) && dates.contains(line.substring(5))) {
                listOfTables.add(line);
            }
        }
        FileScanner.close();

        return listOfTables;
    }

    public List<String> parseFiles(List<String> list, String currency) throws Exception {
        //Iterating through the xml files for given days to obtain buyout and sell price for requested currency at each day
        List<Double> buyoutPrice = new ArrayList<>();
        List<Double> sellPrice = new ArrayList<>();
        Calculations calculator = new Calculations();
        for (String tableItem : list) {
            Document doc = loadDocument("http://www.nbp.pl/kursy/xml/" + tableItem + ".xml");
            doc.getDocumentElement().normalize();
            NodeList CurrencyTable = doc.getElementsByTagName("pozycja");
            for (int iterator = 0; iterator < CurrencyTable.getLength(); iterator++) {
                Node node = CurrencyTable.item(iterator);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element item = (Element) node;
                    if (item.getElementsByTagName("kod_waluty").item(0).getTextContent().equals(currency) && !currency.equals("")) {

                        String buyPrice = item.getElementsByTagName("kurs_kupna").item(0).getTextContent().replace(",", ".");
                        String itemSellPrice = item.getElementsByTagName("kurs_sprzedazy").item(0).getTextContent().replace(",", ".");

                        buyoutPrice.add(Double.parseDouble(buyPrice));
                        sellPrice.add(Double.parseDouble(itemSellPrice));
                    }
                }
            }
        }

        String sumResult = new DecimalFormat("#0.0000").format(calculator.calculateAvarage(buyoutPrice));
        String devResult = new DecimalFormat("#0.0000").format(calculator.getStdDev(sellPrice));

        List<String> results = new ArrayList<>();
        results.add(sumResult);
        results.add(devResult);


        return results;
    }

    public List<String> getDates(LocalDate DateFrom, LocalDate DateTo) {
        List<String> totalDates = new ArrayList<>();
        List<LocalDate> localDates = new ArrayList<>();
        while (!DateFrom.isAfter(DateTo)) {
            localDates.add(DateFrom);
            DateFrom = DateFrom.plusDays(1);
        }
        //Converting dates format
        //yyyy-MM-dd to yyMMdd
        for (LocalDate date : localDates) {
            String FormattedDate = date.toString().replaceAll("(\\d+)-(\\d+)-(\\d+)", "$1$2$3").substring(2);
            totalDates.add(FormattedDate);
        }
        return totalDates;
    }
}
