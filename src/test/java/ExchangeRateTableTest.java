import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTableTest {

    private ExchangeRateTable table;

    @BeforeEach
    void setUp() {
        table = new ExchangeRateTable();
    }

    @Test
    void PLNShouldBeReferenceCurrencyInNewTable() {
        assertEquals(Currency.getInstance("PLN"), table.getReferenceCurrency());
    }

    @Test
    void USDShouldBePresentWithReferenceRateOfNull() {
        assertNull(table.getReferenceRate(Currency.getInstance("USD")));
    }

    @Test
    void EURShouldBePresentWithReferenceRateOfNull() {
        assertNull(table.getReferenceRate(Currency.getInstance("EUR")));
    }

    @Test
    void spreadForUSDShouldBeZero() {
        assertEquals(BigDecimal.ZERO, table.getSpread(Currency.getInstance("USD")));
    }

    @Test
    void spreadForEURShouldBeZero() {
        assertEquals(BigDecimal.ZERO, table.getSpread(Currency.getInstance("EUR")));
    }

    @Test
    void shouldThrowExceptionOnAttemptToGetReferenceRateForReferenceCurrency() {
        Currency referenceCurrency = table.getReferenceCurrency();
        Exception e = assertThrows(IllegalArgumentException.class, () -> table.getReferenceRate(referenceCurrency));
        assertEquals("Currency PLN is the reference currency, no reference rate available.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfCurrencyIsNotPresentInReferenceRateTable() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> table.getReferenceRate(Currency.getInstance("CHF")));
        assertEquals("Currency CHF not found in the exchange rate table!", e.getMessage());
    }
}