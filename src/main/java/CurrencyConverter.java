import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConverter {
    private final ExchangeRateTable exchangeRateTable;

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
