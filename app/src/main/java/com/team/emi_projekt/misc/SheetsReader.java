package com.team.emi_projekt.misc;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class SheetsReader {

    public static void storeSheets(Context context, Sheets sheets) {

        FileOutputStream outputStream;
        OutputStreamWriter writer;

        try {
            outputStream = context.openFileOutput("Sheets.emi", Context.MODE_PRIVATE);

            writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            writer.write(sheets.getSheetsData());

            /*
            Sheets format:

            SheetLabel|userName{ItemLabel|Itemsomething|ItemData|\nSecondItemBlaBla|\n}SecondSheetLabel|userName|secondUserName{same}

             */
            writer.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sheets loadSheets(Context context) {
        Sheets sheets = new Sheets();

        FileInputStream inputStream;
        InputStreamReader reader;

        try {
            inputStream = context.openFileInput("Sheets.emi");
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Character current;
            String temp = "";
            while (reader.ready()) {
                current = (char) reader.read();
                if (current == '}') {
                    sheets.addSheetData(temp);
                    temp = "";
                }
                else //TODO: change from += to StringFormat
                    temp += current;
            }
            /*
            Sheets format:

            SheetLabel{ItemLabel|Itemsomething|ItemData|\nSecondItemBlaBla|\n}SecondSheetLabel{same}

             */
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sheets;
    }
}
