package com.flossi;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DummyJUnit5Test {
    @Tag("slow")
    @Test
    void testAddMaxInteger() {
        assertEquals(2147483646, Integer.sum(2147183646, 300010));
    }
    
    @Tag("fast")
    @Test
    public void testDivide() {
        assertThrows(ArithmeticException.class, () -> {
            Integer.divideUnsigned(42, 0);
        });
    }
}
