package kth.socialqr.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * Utility class for generating QR-code.
 * Using androidmads library based on zxing
 */

public class QRGenerator {

    public static Bitmap generate(Context context, String content) {
        QRGEncoder qrgEncoder;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = Math.min(width, height);
        smallerDimension = smallerDimension * 2/4;

        qrgEncoder = new QRGEncoder(content, null, QRGContents.Type.TEXT, smallerDimension);

        return qrgEncoder.getBitmap();
    }
}
