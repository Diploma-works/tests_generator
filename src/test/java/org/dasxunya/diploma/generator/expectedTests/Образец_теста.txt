package org.dasxunya.diploma.generator.expectedTests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.Assert.assertTrue;

class ParameterizedVoidMethod {

    boolean someMethod(String str, int i, boolean flag, byte b, char c, short s, long l, float f, double d){
        return true;
    }

    @ParameterizedTest
    @CsvSource({
            "exampleString, 0, true, 0, 'a', 0, 0, 0.0f, 0.0",
            "exampleString, 0, true, 0, 'a', 0, 0, 0.0f, 0.0"
    })
    public void testVoidMethod(String str, int i, boolean flag, byte b, char c, short s, long l, float f, double d) {
        // TODO: Тестирование логики
        assertTrue(someMethod(str, i, flag, b, c, s, l, f, d));
        // TODO: Добавить другие проверки
    }
}
