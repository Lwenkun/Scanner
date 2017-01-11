package net.bingyan.hustpass.scanner;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import net.bingyan.hustpass.scanner.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I create this class to record state of every item.
 * Created by lwenkun on 2016/12/25.
 */

public abstract class StateAdapter<T extends RecyclerView.ViewHolder, I> extends BaseAdapter<T, I> {

    private SparseArray<Map<String, Object>> states;

    private Map<String, Object> def;

    public StateAdapter(List<I> dataList, Map<String, Object> def) {
        super(dataList);
        states = new SparseArray<>();
        this.def = def;
        if (dataList == null) return;
        for (int i = 0; i < dataList.size(); i++) {
            states.put(i, new HashMap<>(def));
        }
    }

    @Override
    public void updateData(List<I> dataList) {
        super.updateData(dataList);
        states.clear();
        for (int i = 0; i < dataList.size(); i++) {
            states.put(i, new HashMap<>(def));
        }
    }

    public int getIntState(int position, String key) {
        return (int)states.get(position).get(key);
    }

    public float getFloatState(int position, String key) {
        return (float)states.get(position).get(key);
    }

    public double getDoubleState(int position, String key) {
        return (double)states.get(position).get(key);
    }

    public boolean getBooleanState(int position, String key) {
        return (boolean)states.get(position).get(key);
    }

    public String getStringState(int position, String key) {
        return (String)states.get(position).get(key);
    }

    public Object getState(int position, String key) {
        return states.get(position).get(key);
    }

    public void setState(int position, String key, Object value) {
        ensureStateMapNotNull(position);
        states.get(position).put(key, value);
    }

    private void ensureStateMapNotNull(int position) {
        if (states.get(position) == null) {
            states.put(position, new HashMap<String, Object>());
        }
    }
}
