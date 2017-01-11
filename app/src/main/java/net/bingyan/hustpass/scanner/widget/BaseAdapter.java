package net.bingyan.hustpass.scanner.widget;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * this class is a base class for all RecyclerView$Adapter.
 * Created by lwenkun on 2016/12/19.
 */

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, I> extends RecyclerView.Adapter<T> {

    private List<I> mDataList;

    public BaseAdapter(List<I> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void updateData(List<I> dataList) {
        mDataList = dataList;
    }

    public List<I> getData() {
        return mDataList;
    }

    public I getItem(int position) {
        if (mDataList != null)
            return mDataList.get(position);
        return null;
    }

}
