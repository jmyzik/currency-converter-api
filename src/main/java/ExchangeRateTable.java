import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateTable {
    public static final int DEFAULT_SCALE = 2;
    public static final int REFERENCE_RATE_SCALE = 4;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private Currency referenceCurrency;
    private Map<Currency, BigDecimal> referenceRates;
    private Map<Currency, BigDecimal> spreads;

    public ExchangeRateTable() {
        referenceCurrency = Currency.getInstance("PLN");
        referenceRates = new HashMap<>();
        spreads = new HashMap<>();
        referenceRates.put(Currency.getInstance("USD"), null);
        spreads.put(Currency.getInstance("USD"), BigDecimal.ZERO);
        referenceRates.put(Currency.getInstance("EUR"), null);
        spreads.put(Currency.getInstance("EUR"), BigDecimal.ZERO);
    }

    public Currency getReferenceCurrency() {
        return referenceCurrency;
    }

    public BigDecimal getReferenceRate(Currency currency) {
        if (currency.equals(referenceCurrency)) {
            throw new IllegalArgumentException(String.format(
                    "Currency %s is the reference currency, no reference rate available.", referenceCurrency)
            );
        }
        if (!referenceRates.containsKey(currency)) {
            throw new IllegalArgumentException(
                    String.format("Currency %s not found in the exchange rate table!", currency.toString())
            );
        }
        return referenceRates.get(currency);
    }

    public BigDecimal getReferenceRate(String currency) {
        try {
            return getReferenceRate(Currency.getInstance(currency));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public BigDecimal getSpread(Currency currency) {
        if (currency.equals(referenceCurrency)) {
            throw new IllegalArgumentException(String.format(
                    "Currency %s is the reference currency, no spread available.", referenceCurrency)
            );
        }
        if (!spreads.containsKey(currency)) {
            throw new IllegalArgumentException(
                    String.format("Currency %s not found in the exchange rate table!", currency.toString())
            );
        }
        return spreads.get(currency);
    }

    public BigDecimal getSpread(String currency) {
        try {
            return getSpread(Currency.getInstance(currency));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public void setReferenceRate(Currency currency, double rate) {
        if (currency.equals(referenceCurrency)) {
            throw new IllegalArgumentException("Setting reference rate for the reference currency is not allowed.");
        }
        if (rate <= 0) {
            throw new IllegalArgumentException("The reference rate must be greater than zero!");
        }
        referenceRates.put(currency, new BigDecimal(rate).setScale(REFERENCE_RATE_SCALE, ROUNDING_MODE));
    }

    public void setReferenceRate(String currency, double rate) {
        try {
            setReferenceRate(Currency.getInstance(currency), rate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public void setSpread(Currency currency, double spread) {
        if (currency.equals(referenceCurrency)) {
            throw new IllegalArgumentException("Setting spread for the reference currency is not allowed.");
        }
        if (spread <= 0) {
            throw new IllegalArgumentException("The spread must be greater than zero!");
        }
        spreads.put(currency, new BigDecimal(spread).setScale(DEFAULT_SCALE, ROUNDING_MODE));
    }

    public void setSpread(String currency, double rate) {
        try {
            setSpread(Currency.getInstance(currency), rate);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public void downloadRates() {
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

            BigDecimal referenceCurrencyRate = extractRateAsBigDecimal(rates, referenceCurrency);

            for (Currency currency : referenceRates.keySet()) {
                BigDecimal extractedRate = extractRateAsBigDecimal(rates, currency);
                BigDecimal convertedRate = BigDecimal.ONE
                        .divide(extractedRate, extractedRate.scale(), ROUNDING_MODE)
                        .multiply(referenceCurrencyRate)
                        .setScale(REFERENCE_RATE_SCALE, ROUNDING_MODE);
                referenceRates.put(currency, convertedRate);
            }
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private BigDecimal extractRateAsBigDecimal(Map rates, Currency currency) {
        Number rateAsNumber = (Number) rates.get(currency.toString());
        String rateAsString = rateAsNumber.toString();
        return new BigDecimal(rateAsString);
    }
}