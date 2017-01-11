package net.bingyan.hustpass.scanner.cache;

/**
 * Created by lwenkun on 2016/12/20.
 */

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
}
