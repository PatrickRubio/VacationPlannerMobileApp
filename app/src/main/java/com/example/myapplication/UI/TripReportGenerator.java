package com.example.myapplication.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TripReportGenerator {

    public static void generateReport(Context context, Vacation vacation, List<Excursion> excursions) {
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();

        // Creation of one page for reporting
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 40;
        int y = 60;

        // Title of report
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText("Trip Details Report", x, y, paint);

        y += 40;
        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        canvas.drawText("Trip: " + vacation.getVacationName(), x, y, paint);

        y += 20;
        canvas.drawText("Start: " + vacation.getStartDate() + "   End: " + vacation.getEndDate(), x, y, paint);

        y += 20;
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        canvas.drawText("Generated on: " + timestamp, x, y, paint);


        // Header for Excursions table
        y += 40;
        paint.setFakeBoldText(true);
        canvas.drawText("Excursion Name", x, y, paint);
        canvas.drawText("Date", x + 200, y, paint);
        canvas.drawText("Cost", x + 350, y, paint);

        paint.setFakeBoldText(false);

        // Rows for report
        for (Excursion excursion : excursions) {
            y += 20;
            canvas.drawText(excursion.getExcursionName(), x, y, paint);
            canvas.drawText(excursion.getExcursionDate(), x + 200, y, paint);
            canvas.drawText("$" + excursion.getPrice(), x + 350, y, paint);
        }

        pdf.finishPage(page);

        // File Save in Downloads
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "TripReport_" + vacation.getVacationName() + ".pdf");

        try {
            pdf.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "Report saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error saving report: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdf.close();
    }
}
