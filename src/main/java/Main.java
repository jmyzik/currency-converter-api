import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

public class Main {
    private static final Currency PLN = Currency.getInstance("PLN");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    public static void main(String[] args) {
        ExchangeRateTable table = new ExchangeRateTable();
        CurrencyConverter converter = new CurrencyConverter(table);

        try {
            table.downloadRates();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
//        table.setSpread("USD", 0.32);
//        table.setSpread("EUR", 0.44);
        System.out.printf("1 USD equals %s PLN%n", table.getReferenceRate(USD));
        System.out.printf("1 EUR equals %s PLN%n", table.getReferenceRate(EUR));
        System.out.printf("15.70 EUR equals %s USD%n", converter.convertBetween(EUR, USD, new BigDecimal("15.7")));
    }
}
