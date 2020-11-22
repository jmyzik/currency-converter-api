import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

class ExchangeRateTable {
    private Currency referenceCurrency;
    private Map<Currency, BigDecimal> referenceRates;
    private Map<Currency, BigDecimal> spreads;

    ExchangeRateTable() {
        referenceCurrency = Currency.getInstance("PLN");
        referenceRates = new HashMap<>();
        spreads = new HashMap<>();
        referenceRates.put(Currency.getInstance("USD"), null);
        spreads.put(Currency.getInstance("USD"), BigDecimal.ZERO);
        referenceRates.put(Currency.getInstance("EUR"), null);
        spreads.put(Currency.getInstance("EUR"), BigDecimal.ZERO);
    }

    Currency getReferenceCurrency() {
        return referenceCurrency;
    }

    BigDecimal getReferenceRate(Currency currency) {
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

    BigDecimal getSpread(Currency currency) {
        return spreads.get(currency);
    }
}