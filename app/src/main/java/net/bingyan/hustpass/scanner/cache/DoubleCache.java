package net.bingyan.hustpass.scanner.cache;

/**
 * Created by lwenkun on 2016/12/22.
 */

public class DoubleCache<K, V> implements Cache<K, V> {

    private Cache<K, V> mDiskCache;
    private Cache<K, V> mRamCache;

    public DoubleCache(Cache<K, V> diskCache, Cache<K, V> ramCache) {
        this.mDiskCache = diskCache;
        this.mRamCache = ramCache;
    }

    @Override
    public V get(K key) {
        V value = mRamCache.get(key);
        if (value != null) return value;

        value = mDiskCache.get(key);
        if (value != null) {
            mRamCache.put(key, value);
            return value;
        }

        return null;
    }

    @Override
    public void put(K key, V value) {
        mRamCache.put(key, value);
        mDiskCache.put(key, value);
    }
}
