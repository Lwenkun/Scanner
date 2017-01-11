package net.bingyan.hustpass.scanner.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lwenkun on 2016/12/17.
 */

public class QRCodeUtils {

    /**
     * Transform a String to a Bitmap using zxing.
     * @param content String content.
     * @return What you want.
     */
    public static Bitmap string2QRCode(String content) {

        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE); //white background

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            // 注意要使用 utf-8，因为刚才生成二维码时，使用了utf-8
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints);
            //fill this bitmap according to BitMatrix
            for (int i = 0; i < bitMatrix.getHeight(); i++) {
                for (int j = 0; j < bitMatrix.getWidth(); j++) {
                    if (bitMatrix.get(i, j)) {
                        bitmap.setPixel(i, j, Color.parseColor("#ff000000"));//I use black color. And you?
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
