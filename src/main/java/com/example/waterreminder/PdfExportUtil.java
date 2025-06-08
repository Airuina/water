package com.example.waterreminder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfExportUtil {

    public static void exportTextToPdf(Context context, String title, List<String> lines, String fileName) {
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 400 + lines.size() * 20, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        int y = 20;
        canvas.drawText(title, 10, y, paint);
        y += 25;

        for (String line : lines) {
            canvas.drawText(line, 10, y, paint);
            y += 20;
        }

        document.finishPage(page);

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            fos.close();
            Toast.makeText(context, "导出成功: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }
}

