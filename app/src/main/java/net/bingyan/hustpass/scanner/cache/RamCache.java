package net.bingyan.hustpass.scanner.cache;

import android.util.LruCache;

/**
 * Created by lwenkun on 2016/12/22.
 */

public class RamCache<K, V> implements Cache<K, V> {

    private LruCache<K, V> mLruCache;
    private static final long MAX_SIZE = Runtime.getRuntime().maxMemory() / 4;

    public RamCache() {
        mLruCache = new LruCache<>((int)MAX_SIZE);
    }

    @Override
    public V get(K key) {
        if (hasKey(key)) {
            return mLruCache.get(key);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        mLruCache.put(key, value);
    }

    private boolean hasKey(K key) {
        return key != null && mLruCache.get(key) != null;
    }
}
