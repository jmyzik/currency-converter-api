import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

public interface RatesDownloader {
    public Map<Currency, BigDecimal> downloadRates(Currency referenceCurrency, Set<Currency> currencies) throws IOException;
}
