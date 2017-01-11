package net.bingyan.hustpass.scanner.widget;

import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.bingyan.hustpass.scanner.App;
import net.bingyan.hustpass.scanner.BaseViewHolder;
import net.bingyan.hustpass.scanner.R;
import net.bingyan.hustpass.scanner.StateAdapter;
import net.bingyan.hustpass.scanner.UserInfoManager;
import net.bingyan.hustpass.scanner.cache.BitmapDiskCache;
import net.bingyan.hustpass.scanner.cache.DoubleCache;
import net.bingyan.hustpass.scanner.cache.RamCache;
import net.bingyan.hustpass.scanner.model.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lwenkun on 2016/12/19.
 */

public class SearchResultAdapter extends StateAdapter<SearchResultAdapter.ViewHolder, UserInfo> {

    private DoubleCache<String, Bitmap> doubleCache;
    private UserInfoManager userInfoManager = App.getInstance().getUserInfoManager();

    public interface Callback {
        /**
         * handle click action on view of this item
         * @param v the view that you click on.
         * @param info the user info that associated with this item
         */
        void onViewClick(View v, UserInfo info);

        /**
         * handle click action on the popup menu item.
         * @param item MenuItem that you click.
         * @param info the user info that associated with this item
         * @return true if you had handled this click or false you didn't.
         */
        boolean onPopupMenuItemClick(MenuItem item, UserInfo info);
    }

    private Callback mCallback;

    public SearchResultAdapter(List<UserInfo> userInfoList, Callback callback) {
        super(userInfoList, getDefStates());
        this.mCallback = callback;
        BitmapDiskCache QRCodeDiskCache = App.getInstance().getQRCodeCache();
        RamCache<String, Bitmap> QRCodeRamCache = new RamCache<>();
        this.doubleCache = new DoubleCache<>(QRCodeDiskCache, QRCodeRamCache);
    }

    private static Map<String, Object> getDefStates() {
        Map<String, Object> def = new HashMap<>();
        def.put("expand", false);
        return def;
    }

    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultAdapter.ViewHolder holder, int position) {
        UserInfo item = getItem(position);

        setupQRCodeImage(holder.ivQRCode, item.id);

        holder.tvName.setText(item.name);
        if (item.received) {
            holder.tvState.setText("已签收");
        } else {
            holder.tvState.setText("未签收");
        }
        if (getBooleanState(position, "expand")) {
            holder.expandableLayout.setVisibility(View.VISIBLE);
            holder.ibExpand.setImageResource(R.drawable.ic_expand_less_24dp);
        } else  {
            holder.expandableLayout.setVisibility(View.GONE);
            holder.ibExpand.setImageResource(R.drawable.ic_arrow_down_24dp);
        }
        holder.tvAddress.setText(item.address);
        holder.tvIdNum.setText(item.idNum);
        holder.tvPhone.setText(item.phoneNum);
    }

    private void setupQRCodeImage(final ImageView image, final int userId) {

        userInfoManager.getAttachedQRCodeKey(userId, new UserInfoManager.Callback<String>() {
            @Override
            public void onFinish(String result) {
                Bitmap b = doubleCache.get(result);
                image.setImageBitmap(b);
            }

            @Override
            public void onStart() {

            }
        });
    }

    class ViewHolder extends BaseViewHolder implements View.OnClickListener {

        static final String STATE_EXPAND = "expand";

        PopupMenu popupMenu;

        ImageView ivQRCode;
        ImageButton ibMore;
        ImageButton ibExpand;
        TextView tvName;
        TextView tvState;
        TextView tvAddress;
        TextView tvPhone;
        TextView tvIdNum;
        View expandableLayout;

        public ViewHolder(final View itemView) {
            super(itemView);

            ivQRCode = (ImageView) itemView.findViewById(R.id.iv_qr_code);
            ibMore = (ImageButton) itemView.findViewById(R.id.ib_more);
            popupMenu = new PopupMenu(itemView.getContext(), ibMore);
            popupMenu.getMenuInflater().inflate(R.menu.search_result_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return mCallback != null &&
                            mCallback.onPopupMenuItemClick(item, getItem(getLayoutPosition()));
                }
            });

            System.out.println("width=" + ibMore.getWidth() + ", height=" + ibMore.getHeight());

            ibMore.setOnClickListener(this);

            ibExpand = (ImageButton) itemView.findViewById(R.id.ib_expand);
            ibExpand.setOnClickListener(this);

            tvName = (TextView)itemView.findViewById(R.id.tv_name);
            tvState = (TextView)itemView.findViewById(R.id.tv_state);
            tvAddress = (TextView)itemView.findViewById(R.id.tv_address);
            tvPhone = (TextView)itemView.findViewById(R.id.tv_phone);
            tvIdNum = (TextView)itemView.findViewById(R.id.tv_id_num);

            expandableLayout = itemView.findViewById(R.id.hidden_info);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_more:
                    popupMenu.show();
                    break;
                case R.id.ib_expand:
                    boolean expand = getBooleanState(getLayoutPosition(), STATE_EXPAND);
                    if (expand) {
                        expandableLayout.setVisibility(View.GONE);
                        ibExpand.setImageResource(R.drawable.ic_arrow_down_24dp);
                    } else {
                        expandableLayout.setVisibility(View.VISIBLE);
                        ibExpand.setImageResource(R.drawable.ic_expand_less_24dp);
                    }
                    setState(getLayoutPosition(), STATE_EXPAND, !expand);
                    break;
                default:
                    if (mCallback != null) {
                        mCallback.onViewClick(v, getItem(getLayoutPosition()));
                    }
            }
        }
    }



}
