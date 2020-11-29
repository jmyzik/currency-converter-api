import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConverterTest {

    @Test
    void shouldConvertFromReferenceCurrencyToAnotherCurrency() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal referenceRate = new BigDecimal("4.2134");
        BigDecimal spread = new BigDecimal("0.42");
        BigDecimal amount = new BigDecimal("100.00");

        ExchangeRateTable table = mock(ExchangeRateTable.class);
        when(table.getReferenceRate(currency)).thenReturn(referenceRate);
        when(table.getSpread(currency)).thenReturn(spread);

        CurrencyConverter converter = new CurrencyConverter(table);

        BigDecimal expectedValue = new BigDecimal("22.61");

        assertEquals(expectedValue, converter.convertTo(currency, amount));
    }

}