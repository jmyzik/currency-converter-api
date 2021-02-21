import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final RatesDownloader downloader;

    public ExchangeRateTable() {
        this(Currency.getInstance("PLN"));
        referenceRates.put(Currency.getInstance("USD"), null);
        spreads.put(Currency.getInstance("USD"), BigDecimal.ZERO);
        referenceRates.put(Currency.getInstance("EUR"), null);
        spreads.put(Currency.getInstance("EUR"), BigDecimal.ZERO);
    }

    public ExchangeRateTable(Currency referenceCurrency) {
        this(referenceCurrency, new FixerRatesDownloader());
    }

    public ExchangeRateTable(Currency referenceCurrency, RatesDownloader downloader) {
        this.referenceCurrency = referenceCurrency;
        this.downloader = downloader;
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
        referenceRates.putAll(downloader.downloadRates(referenceCurrency, referenceRates.keySet()));
    }
}
