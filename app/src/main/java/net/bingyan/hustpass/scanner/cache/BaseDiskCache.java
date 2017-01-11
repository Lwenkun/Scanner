package net.bingyan.hustpass.scanner.cache;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import net.bingyan.hustpass.scanner.BuildConfig;
import net.bingyan.hustpass.scanner.OnTaskListener;
import net.bingyan.hustpass.scanner.ThreadPoolManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * Created by lwenkun on 2016/12/20.
 */

public abstract class BaseDiskCache<T> implements Cache<String, T> {

    public interface CacheGetListener<T> {
        void onFinish(T value);
    }

    private long mSize;
    private static final long DEFAULT_SIZE = 20 * 1024 * 1024;
    private File mCacheDir;

    private DiskLruCache mDiskCache;
    private final byte[] lock = new byte[0];

    /**
     * construct a BaseDiskCache use necessary arguments.
     * @param context A Context object.
     * @param subDir sub directory where you want save your cache.
     * @param size how big should this cache be. if size == -1, then will use default size.
     */
    public BaseDiskCache(Context context, String subDir, long size) {
        this.mCacheDir = getCacheDir(context, subDir);
        this.mSize = size == -1 ? DEFAULT_SIZE : size;
        initCache();
    }

    /**
     * init this cache in background for better performance.
     */
    private void initCache() {
        ThreadPoolManager.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        mDiskCache = DiskLruCache.open(mCacheDir, BuildConfig.VERSION_CODE, 1, mSize);
                        lock.notifyAll();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get a cache item according a key. You'd better use {@link #get(String, CacheGetListener)}
     * instead if you don't call this method on worker method because this method can block main
     * thread which can cause bad user experience.
     * @param key A unique key that points to a specific cache item.
     * @return Cache item that corresponds to this key.
     */
    @Override
    public T get(String key) {
        if (TextUtils.isEmpty(key)) return null;
        try {
            DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
            BufferedInputStream bis = new BufferedInputStream(snapShot.getInputStream(0));
            Log.w("BaseDiskCache", "This method is executed on main thread. " +
                    "Is this what you want? If not, use get(String, CacheGetListener) instead");
            return readFromInputStream(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a cache item according key Asynchronously.
     * @param key A unique key that points to a specific cache item.
     * @param l A listener which you can use to handle the cache item.
     */
    public void get(final String key, final CacheGetListener<T> l) {
        ThreadPoolManager.getInstance()
                .submit(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        return get(key);
                    }
                }, new OnTaskListener<T>() {
                    @Override
                    public void onFinish(T result) {
                        l.onFinish(result);
                    }

                    @Override
                    public void onPreTask() {
                    }
                });
    }

    @Override
    public void put(final String key, final T value) {
        ThreadPoolManager.getInstance()
                .submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DiskLruCache.Editor editor = edit(key);
                            OutputStream out = editor.newOutputStream(0);
                            if (out != null) {
                                write2OutputStream(value, out);
                                editor.commit();
                                mDiskCache.flush();
                            } else {
                                throw new NullPointerException("out is null");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Sub class should implement this to write item using a given OutputStream.
     * @param value Item that you want to put into cache.
     * @param out OutputStream.
     */
    protected abstract void write2OutputStream(T value, OutputStream out);

    /**
     * sub class should implement this to read item using a given InputStream
     * @param in  InputStream
     * @return Item that you want to get from cache
     */
    protected abstract T readFromInputStream(InputStream in);


    private DiskLruCache.Editor edit(String key) throws IOException {
        if (mDiskCache != null) {
            return mDiskCache.edit(key);
        }
        throw new NullPointerException("mDiskCache is null");
    }

    private static File getCacheDir(Context context, String subDir) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + subDir);
    }

}
