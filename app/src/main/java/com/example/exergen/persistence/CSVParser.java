package com.example.exergen.persistence;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    // Reads a CSV file from the assets folder and returns a list of row tokens.
    public static List<String[]> parseAssetCSV(Context context, String fileName) {
        List<String[]> rows = new ArrayList<>();

        try (InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            reader.readLine(); // Skip the header row

            while ((line = reader.readLine()) != null) {
                /*
                Black magic regex to split the row into pieces and add it to our list.
                This allows us to handle commas inside the instructions field of an exercise
                 */
                rows.add(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
            }

        }
        catch (IOException e) {
            Log.e("CSVParser", "Failed to parse CSV: " + fileName, e);
        }
        return rows;
    }
}