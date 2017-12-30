package com.team.emi_projekt.misc;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SheetsReader {

    public static void storeSheets(Context context, Sheets sheets) {

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput("Sheets.emi", Context.MODE_PRIVATE);

            outputStream.write(sheets.getSheetsData().getBytes());
            /*
            Sheets format:

            SheetLabel{ItemLabel|Itemsomething|ItemData|\nSecondItemBlaBla|\n}SecondSheetLabel{same}

             */
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sheets loadSheets(Context context) {
        Sheets sheets = new Sheets();

        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput("Sheets.emi");
            char current;
            String temp = "";
            while (inputStream.available() > 0) {
                current = (char) inputStream.read();
                if (current == '}') {
                    sheets.addSheetData(temp);
                    temp = "";
                }
                else
                    temp += current;
            }
            /*
            Sheets format:

            SheetLabel{ItemLabel|Itemsomething|ItemData|\nSecondItemBlaBla|\n}SecondSheetLabel{same}

             */
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sheets;
    }
}
