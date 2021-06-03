package com.llw.scandemo;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by fly on 2017/4/6.
 */

public class DataCharAdapter extends BaseQuickAdapter<CharSequence, BaseViewHolder> {
    public DataCharAdapter() {
        super(R.layout.serial_item_data_char);
    }
    int i=0;
    @Override
    protected void convert(final BaseViewHolder helper, CharSequence item) {
        helper.setText(R.id.tv_data, item);
        helper.setText(R.id.index, ""+(i++)+" ");
    }


}
