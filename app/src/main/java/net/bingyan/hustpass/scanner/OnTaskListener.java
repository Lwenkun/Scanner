package net.bingyan.hustpass.scanner;

/**
 * Created by lwenkun on 2016/12/17.
 */

public interface OnTaskListener<T> {
    void onFinish(T result);
    void onPreTask();
}
