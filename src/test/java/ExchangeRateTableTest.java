import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTableTest {

    static final Currency PLN = Currency.getInstance("PLN");
    static final Currency EUR = Currency.getInstance("EUR");
    static final Currency USD = Currency.getInstance("USD");
    ExchangeRateTable table;

    @BeforeEach
    void setUp() {
        table = new ExchangeRateTable();
    }

    @Test
    void PLNShouldBeReferenceCurrencyInNewTable() {
        assertEquals(PLN, table.getReferenceCurrency());
    }

    @Test
    void USDShouldBePresentWithReferenceRateOfNull() {
        assertNull(table.getReferenceRate(USD));
    }

    @Test
    void EURShouldBePresentWithReferenceRateOfNull() {
        assertNull(table.getReferenceRate(EUR));
    }

    @Test
    void spreadForUSDShouldBeZero() {
        assertEquals(BigDecimal.ZERO, table.getSpread(USD));
    }

    @Test
    void spreadForEURShouldBeZero() {
        assertEquals(BigDecimal.ZERO, table.getSpread(EUR));
    }

    @Test
    void shouldReturnOneAsReferenceRateForReferenceCurrency() {
        assertEquals(BigDecimal.ONE, table.getReferenceRate(table.getReferenceCurrency()));
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
    void shouldReturnZeroAsSpreadForReferenceCurrency() {
        assertEquals(BigDecimal.ZERO, table.getSpread(table.getReferenceCurrency()));
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
        table.setReferenceRate(USD, 4.2);
        BigDecimal expectedRate = new BigDecimal("4.2").
                setScale(ExchangeRateTable.REFERENCE_RATE_SCALE, ExchangeRateTable.ROUNDING_MODE);
        assertEquals(expectedRate, table.getReferenceRate(USD));
    }

    @Test
    void shouldSetSpreadForExistingCurrency() {
        table.setSpread(EUR, 0.49);
        BigDecimal expectedSpread = new BigDecimal("0.49").
                setScale(ExchangeRateTable.DEFAULT_SCALE, ExchangeRateTable.ROUNDING_MODE);
        assertEquals(expectedSpread, table.getSpread(EUR));
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

    @Test
    void shouldThrowExceptionOnNonPositiveReferenceRateArgument() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.setReferenceRate(EUR, -3.2389)
        );
        assertEquals("The reference rate must be greater than zero!", e.getMessage());
    }

    @Test
    void shouldThrowExceptionOnNegativeSpreadArgument() {
        Exception e = assertThrows(
                IllegalArgumentException.class,
                () -> table.setSpread(USD, -0.52)
        );
        assertEquals("The spread must not be less than zero!", e.getMessage());
    }
}