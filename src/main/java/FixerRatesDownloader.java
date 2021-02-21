import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FixerRatesDownloader implements RatesDownloader {

    @Override
    public Map<Currency, BigDecimal> downloadRates(Currency referenceCurrency, Set<Currency> currencies) throws IOException {
        Map<Currency, BigDecimal> result = new HashMap<>();

        Map downloadedRates;
        try {
            downloadedRates = getRatesMapFromExternalAPI();
        } catch (IOException | ParseException e) {
            String message = String.format("Could not obtain rates from the external API (%s).", e.getMessage());
            throw new IOException(message);
        }

        BigDecimal referenceCurrencyRate = extractRateAsBigDecimal(downloadedRates, referenceCurrency);

        for (Currency currency : currencies) {
            BigDecimal extractedRate = extractRateAsBigDecimal(downloadedRates, currency);
            BigDecimal convertedRate = BigDecimal.ONE
                    .divide(extractedRate, extractedRate.scale(), ExchangeRateTable.ROUNDING_MODE)
                    .multiply(referenceCurrencyRate)
                    .setScale(ExchangeRateTable.REFERENCE_RATE_SCALE, ExchangeRateTable.ROUNDING_MODE);
            result.put(currency, convertedRate);
        }

        return result;
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
