import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        ExchangeRateTable table = new ExchangeRateTable();
        CurrencyConverter converter = new CurrencyConverter(table);

        table.downloadRates();
        System.out.printf("1 USD equals %s PLN%n", table.getReferenceRate("USD"));
        System.out.printf("1 EUR equals %s PLN%n", table.getReferenceRate("EUR"));
        System.out.printf("15.7 EUR equals %s USD%n", converter.convertBetween("EUR", "USD", new BigDecimal("15.7")));
    }
}
