package net.bingyan.hustpass.scanner;

import android.support.v7.util.DiffUtil;

import net.bingyan.hustpass.scanner.model.UserInfo;

import java.util.List;

/**
 * Created by lwenkun on 2016/12/19.
 */

public class DiffCallback extends DiffUtil.Callback {

    private List<UserInfo> newData;
    private List<UserInfo> oldData;

    public DiffCallback(List<UserInfo> newData, List<UserInfo> oldData) {
        this.newData = newData;
        this.oldData = oldData;
    }

    @Override
    public int getOldListSize() {
        return oldData == null ? 0 : oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData == null ? 0 : newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).id == newData.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
    }
}
