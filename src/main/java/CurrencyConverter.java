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

    public BigDecimal convertTo(String currency, BigDecimal amount) {
        try {
            return convertTo(Currency.getInstance(currency), amount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public BigDecimal convertFrom(Currency currency, BigDecimal amount) {
        BigDecimal halfSpread = exchangeRateTable.getSpread(currency)
                .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        BigDecimal exchangeRate = exchangeRateTable.getReferenceRate(currency)
                .subtract(halfSpread);
        BigDecimal result = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        return result;
    }

    public BigDecimal convertFrom(String currency, BigDecimal amount) {
        try {
            return convertFrom(Currency.getInstance(currency), amount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s has not been recognized!", currency)
            );
        }
    }

    public BigDecimal convertBetween(Currency originalCurrency, Currency targetCurrency, BigDecimal amount) {
        BigDecimal referenceCurrencyAmount = convertFrom(originalCurrency, amount);
        BigDecimal result = convertTo(targetCurrency, referenceCurrencyAmount);

        return result;
    }

    public BigDecimal convertBetween(String originalCurrency, String targetCurrency, BigDecimal amount) {
        try {
            return convertBetween(Currency.getInstance(originalCurrency), Currency.getInstance(targetCurrency), amount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Currency %s or %s has not been recognized!", originalCurrency, targetCurrency)
            );
        }
    }
}
