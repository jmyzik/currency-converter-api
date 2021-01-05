import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class Main {
    public static void main(String[] args) {
        ExchangeRateTable table = new ExchangeRateTable();
        CurrencyConverter converter = new CurrencyConverter(table);

        table.downloadRates();
        System.out.printf("1 USD equals %s PLN%n", table.getReferenceRate(Currency.getInstance("USD")).toString());
        System.out.printf("1 EUR equals %s PLN%n", table.getReferenceRate(Currency.getInstance("EUR")).toString());

//                converter.convertFrom(Currency.getInstance("USD"), BigDecimal.ONE).toString());
//        System.out.printf("1 EUR equals %s PLN%n",
//                converter.convertFrom(Currency.getInstance("EUR"), BigDecimal.ONE).toString());
    }
}
