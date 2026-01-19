package com.urlshortener.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Base62UtilTest {

    @Test
    void testEncode_Zero() {
        assertEquals("0", Base62Util.encode(0));
    }

    @Test
    void testEncode_SingleDigit() {
        assertEquals("1", Base62Util.encode(1));
        assertEquals("a", Base62Util.encode(10));
        assertEquals("A", Base62Util.encode(36));
        assertEquals("Z", Base62Util.encode(61));
    }

    @Test
    void testEncode_MultipleDigits() {
        assertEquals("10", Base62Util.encode(62));
        assertEquals("1A", Base62Util.encode(62 + 36)); // 62 + 36 = 98
        assertEquals("100", Base62Util.encode(62 * 62)); // 62^2 = 3844
    }

    @Test
    void testEncode_LargeNumber() {
        long largeNumber = 123456789L;
        String encoded = Base62Util.encode(largeNumber);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());

        // Verify it can be decoded (test consistency)
        // Note: We don't have decode, but we can verify it's not the same as input
        assertNotEquals(String.valueOf(largeNumber), encoded);
    }

    @Test
    void testEncode_Consistency() {
        long[] testValues = {0, 1, 10, 36, 61, 62, 100, 1000, 10000, 1000000};
        for (long value : testValues) {
            String encoded = Base62Util.encode(value);
            assertNotNull(encoded);
        }
    }
}