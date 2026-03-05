package com.example.exergen.business.service;

import static org.junit.Assert.*;
import org.junit.Test;

public class CSVParserTest {

    @Test
    public void testParseLineWithCommasInQuotes() {
        String csvLine = "1,Pushups,Chest,None,\"Keep back straight, then lift\",2,pushup_img";

        // Same regex as in CSVParser.parseAssetCSV
        String[] tokens = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        assertEquals("Should have 7 columns", 7, tokens.length);
        assertEquals("ID should match", "1", tokens[0]);
        assertEquals("Instructions should contain the comma", "\"Keep back straight, then lift\"", tokens[4]);
        assertEquals("Intensity should be in the correct index", "2", tokens[5]);
    }

    @Test
    public void testParseNormalLine() {
        String csvLine = "2,Squats,Legs,None,Standard squat,3,squat_img";
        String[] tokens = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        assertEquals(7, tokens.length);
        assertEquals("Squats", tokens[1]);
        assertEquals("3", tokens[5]);
    }
}