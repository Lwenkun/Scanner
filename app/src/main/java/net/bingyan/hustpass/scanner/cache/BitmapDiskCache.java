package net.bingyan.hustpass.scanner.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lwenkun on 2016/12/21.
 */

public class BitmapDiskCache extends BaseDiskCache<Bitmap> {


    public BitmapDiskCache(Context context, String subDir, long size) {
        super(context, subDir, size);
    }

    @Override
    protected void write2OutputStream(Bitmap value, OutputStream out) {
        value.compress(Bitmap.CompressFormat.PNG, 100, out);
    }

    @Override
    protected Bitmap readFromInputStream(InputStream in) {
        return BitmapFactory.decodeStream(in);
    }

}
