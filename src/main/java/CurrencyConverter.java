import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConverter {
    private ExchangeRateTable exchangeRateTable;

    public CurrencyConverter(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    public BigDecimal convertTo(Currency currency, BigDecimal amount) {
        BigDecimal halfSpread = exchangeRateTable.getSpread(currency)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        BigDecimal exchangeRate = exchangeRateTable.getReferenceRate(currency)
                .add(halfSpread);
        BigDecimal result = amount.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        return result;
    }
}
