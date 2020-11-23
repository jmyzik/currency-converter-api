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
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.getReferenceRate(table.getReferenceCurrency())
        );
        assertEquals("Currency PLN is the reference currency, no reference rate available.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfCurrencyIsNotPresentInReferenceRateTable() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.getReferenceRate(Currency.getInstance("CHF"))
        );
        assertEquals("Currency CHF not found in the exchange rate table!", e.getMessage());
    }

    @Test
    void shouldThrowExceptionOnAttemptToGetSpreadForReferenceCurrency() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.getSpread(table.getReferenceCurrency())
        );
        assertEquals("Currency PLN is the reference currency, no spread available.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfCurrencyIsNotPresentInSpreadTable() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.getSpread(Currency.getInstance("CHF"))
        );
        assertEquals("Currency CHF not found in the exchange rate table!", e.getMessage());
    }

    @Test
    void shouldSetReferenceRateForExistingCurrency() {
        table.setReferenceRate(Currency.getInstance("USD"), 4.2);
        BigDecimal expectedRate = new BigDecimal(4.2).
                setScale(ExchangeRateTable.REFERENCE_RATE_SCALE, ExchangeRateTable.ROUNDING_MODE);
        assertEquals(expectedRate, table.getReferenceRate(Currency.getInstance("USD")));
    }

    @Test
    void shouldSetSpreadForExistingCurrency() {
        table.setSpread(Currency.getInstance("EUR"), 0.49);
        BigDecimal expectedSpread = new BigDecimal(0.49).
                setScale(ExchangeRateTable.DEFAULT_SCALE, ExchangeRateTable.ROUNDING_MODE);
        assertEquals(expectedSpread, table.getSpread(Currency.getInstance("EUR")));
    }

    @Test
    void shouldThrowExceptionOnAttemptToSetReferenceRateForReferenceCurrency() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.setReferenceRate(table.getReferenceCurrency(), 3.7285)
        );
        assertEquals("Setting reference rate for the reference currency is not allowed.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionOnAttemptToSetSpreadForReferenceCurrency() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.setSpread(table.getReferenceCurrency(), 0.32)
        );
        assertEquals("Setting spread for the reference currency is not allowed.", e.getMessage());
    }

}