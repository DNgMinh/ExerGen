package com.example.exergen.persistence;

import static org.junit.Assert.assertEquals;

import com.example.exergen.persistence.helper.CSVParser;

import org.junit.Test;
import java.lang.reflect.Method;

public class CSVParserTest {

    @Test
    public void testParseLine() throws Exception {
        String line = "1,Pushups,Chest,None,\"Keep back straight, feet apart\",2";

        Method method = CSVParser.class.getDeclaredMethod("parseLine", String.class);
        method.setAccessible(true);
        String[] result = (String[]) method.invoke(null, line);

        assertEquals(6, result.length);
        assertEquals("Pushups", result[1]);
        assertEquals("Keep back straight, feet apart", result[4]);
        assertEquals("2", result[5]);
    }
}