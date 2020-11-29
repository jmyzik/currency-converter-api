import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConverterTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private CurrencyConverter converter;

    @BeforeEach
    void setUp() {
        ExchangeRateTable table = mock(ExchangeRateTable.class);
        when(table.getReferenceRate(USD)).thenReturn( new BigDecimal("4.2134"));
        when(table.getSpread(USD)).thenReturn(new BigDecimal("0.42"));
        when(table.getReferenceRate(EUR)).thenReturn( new BigDecimal("4.5313"));
        when(table.getSpread(EUR)).thenReturn(new BigDecimal("0.46"));

        converter = new CurrencyConverter(table);
    }

    @Test
    void shouldConvertFromReferenceCurrencyToAnotherCurrency() {
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal expectedValue = new BigDecimal("22.61");

        assertEquals(expectedValue, converter.convertTo(USD, amount));
    }

    @Test
    void shouldConvertToReferenceCurrencyFromAnotherCurrency() {
        BigDecimal amount = new BigDecimal("80.00");

        BigDecimal expectedValue = new BigDecimal("344.10");

        assertEquals(expectedValue, converter.convertFrom(EUR, amount));
    }
}