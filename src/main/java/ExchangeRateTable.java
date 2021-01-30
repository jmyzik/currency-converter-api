import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateTable {
    public static final int DEFAULT_SCALE = 2;
    public static final int REFERENCE_RATE_SCALE = 4;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final Currency referenceCurrency;
    private final Map<Currency, BigDecimal> referenceRates;
    private final Map<Currency, BigDecimal> spreads;

    public ExchangeRateTable() {
        this(Currency.getInstance("PLN"));
        referenceRates.put(Currency.getInstance("USD"), null);
        spreads.put(Currency.getInstance("USD"), BigDecimal.ZERO);
        referenceRates.put(Currency.getInstance("EUR"), null);
        spreads.put(Currency.getInstance("EUR"), BigDecimal.ZERO);
    }

    public ExchangeRateTable(Currency referenceCurrency) {
        this.referenceCurrency = referenceCurrency;
        referenceRates = new HashMap<>();
        spreads = new HashMap<>();
    }

    public Currency getReferenceCurrency() {
        return referenceCurrency;
    }

    public BigDecimal getReferenceRate(Currency currency) {
        if (currency.equals(referenceCurrency)) {
            return BigDecimal.ONE;
        }
        if (!referenceRates.containsKey(currency)) {
            throw new IllegalArgumentException(
                    String.format("Currency %s not found in the exchange rate table!", currency.toString())
            );
        }
        return referenceRates.get(currency);
    }

    public BigDecimal getSpread(Currency currency) {
        if (currency.equals(referenceCurrency)) {
            return BigDecimal.ZERO;
        }
        if (!spreads.containsKey(currency)) {
            throw new IllegalArgumentException(
                    String.format("Currency %s not found in the exchange rate table!", currency.toString())
            );
        }
        return spreads.get(currency);
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

    public void setSpread(Currency currency, double spread) {
        if (currency.equals(referenceCurrency)) {
            throw new IllegalArgumentException("Setting spread for the reference currency is not allowed.");
        }
        if (spread < 0) {
            throw new IllegalArgumentException("The spread must not be less than zero!");
        }
        spreads.put(currency, new BigDecimal(spread).setScale(DEFAULT_SCALE, ROUNDING_MODE));
    }

    public void addCurrency(Currency currency) {
        referenceRates.put(currency, null);
        spreads.put(currency, BigDecimal.ZERO);
    }

    public void addCurrency(Currency currency, double referenceRate) {
        addCurrency(currency);
        setReferenceRate(currency, referenceRate);
    }

    public void addCurrency(Currency currency, double referenceRate, double spread) {
        addCurrency(currency, referenceRate);
        setSpread(currency, spread);
    }

    public void downloadRates() throws IOException {
        Map rates;

        try {
            rates = getRatesMapFromExternalAPI();
        } catch (IOException | ParseException e) {
            String message = String.format("Could not obtain rates from the external API (%s).", e.getMessage());
            throw new IOException(message);
        }

        BigDecimal referenceCurrencyRate = extractRateAsBigDecimal(rates, referenceCurrency);

        for (Currency currency : referenceRates.keySet()) {
            BigDecimal extractedRate = extractRateAsBigDecimal(rates, currency);
            BigDecimal convertedRate = BigDecimal.ONE
                    .divide(extractedRate, extractedRate.scale(), ROUNDING_MODE)
                    .multiply(referenceCurrencyRate)
                    .setScale(REFERENCE_RATE_SCALE, ROUNDING_MODE);
            referenceRates.put(currency, convertedRate);
        }
    }

    private Map getRatesMapFromExternalAPI() throws IOException, ParseException {
        String APIKey = "a785e31e967aa583fe71093d0527b6ff";
        String myURLString = "http://data.fixer.io/api/latest?access_key=" + APIKey;
        URL myURL = new URL(myURLString);
        Reader reader = new InputStreamReader(myURL.openStream());
        Object object = new JSONParser().parse(reader);
        JSONObject jsonObject = (JSONObject) object;
        Map rates = (Map) jsonObject.get("rates");
        if (rates == null) {
            throw new IOException("External rates table unavailable");
        }
        return rates;
    }

    private BigDecimal extractRateAsBigDecimal(Map rates, Currency currency) {
        Number rateAsNumber = (Number) rates.get(currency.toString());
        String rateAsString = rateAsNumber.toString();
        return new BigDecimal(rateAsString);
    }
}