import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({"0, 0.00",
                "0.01, 0.00",
                "1, 0.23",
                "13.03, 2.95",
                "25.17, 5.69",
                "100.00, 22.61",
                "123000, 27806.66",
                "35000000000, 7912465524.26"})
    void shouldConvertFromReferenceCurrencyToAnotherCurrency(String input, String expected) {
        BigDecimal amount = new BigDecimal(input);
        BigDecimal expectedValue = new BigDecimal(expected);

        assertEquals(expectedValue, converter.convertTo(USD, amount));
    }

    @ParameterizedTest
    @CsvSource({"0, 0.00",
                "0.01, 0.04",
                "1, 4.30",
                "15.2, 65.38",
                "77.77, 334.51",
                "150, 645.20",
                "123000, 529059.90",
                "35000000000, 150545500000.00"})
    void shouldConvertToReferenceCurrencyFromAnotherCurrency(String input, String expected) {
        BigDecimal amount = new BigDecimal(input);
        BigDecimal expectedValue = new BigDecimal(expected);

        assertEquals(expectedValue, converter.convertFrom(EUR, amount));
    }

    @ParameterizedTest
    @CsvSource({"0, 0.00",
                "0.01, 0.01",
                "1, 0.97",
                "15.37, 14.95",
                "99.99, 97.23",
                "250, 243.10",
                "123000, 119604.81",
                "35000000000, 34033887959.49"})
    void shouldConvertBetweenEURAndUSD(String amountInEUR, String expectedAmountInUSD) {
        BigDecimal input = new BigDecimal(amountInEUR);
        BigDecimal expected = new BigDecimal(expectedAmountInUSD);

        assertEquals(expected, converter.convertBetween(EUR, USD, input));
    }
}