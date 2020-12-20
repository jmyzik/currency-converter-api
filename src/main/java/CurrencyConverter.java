import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Map;

public class CurrencyConverter {
    private ExchangeRateTable exchangeRateTable;

    public CurrencyConverter(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    public static void main(String[] args) {
        String APIKey = "a785e31e967aa583fe71093d0527b6ff";
        String myURLString = "http://data.fixer.io/api/latest?access_key=" + APIKey;
        URL myURL;

        try {
            myURL = new URL(myURLString);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return;
        }

        try (Reader reader = new InputStreamReader(myURL.openStream())) {
            Object object = new JSONParser().parse(reader);
            JSONObject jsonObject = (JSONObject) object;
            Map rates = (Map) jsonObject.get("rates");
            System.out.println(rates.get("PLN"));
            System.out.println(rates.get("USD"));
            System.out.println(rates.get("UXT"));

        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }

    }

    public BigDecimal convertTo(Currency currency, BigDecimal amount) {
        BigDecimal halfSpread = exchangeRateTable.getSpread(currency)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        BigDecimal exchangeRate = exchangeRateTable.getReferenceRate(currency)
                .add(halfSpread);
        BigDecimal result = amount.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        return result;
    }

    public BigDecimal convertFrom(Currency currency, BigDecimal amount) {
        BigDecimal halfSpread = exchangeRateTable.getSpread(currency)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        BigDecimal exchangeRate = exchangeRateTable.getReferenceRate(currency)
                .subtract(halfSpread);
        BigDecimal result = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        return result;
    }

    public BigDecimal convertBetween(Currency originalCurrency, Currency targetCurrency, BigDecimal amount) {
        BigDecimal referenceCurrencyAmount = convertFrom(originalCurrency, amount);
        BigDecimal result = convertTo(targetCurrency, referenceCurrencyAmount);

        return result;
    }
}
