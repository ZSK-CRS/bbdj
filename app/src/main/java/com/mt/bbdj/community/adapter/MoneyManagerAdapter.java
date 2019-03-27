package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/26
 * Description :
 */
public class MoneyManagerAdapter extends RecyclerView.Adapter<MoneyManagerAdapter.MoneyManagerViewHolder> {

    private List<HashMap<String,String>> mList;

    public MoneyManagerAdapter(List<HashMap<String,String>> mList) {
        this.mList = mList;
    }


    @Override
    public MoneyManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MoneyManagerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MoneyManagerViewHolder extends RecyclerView.ViewHolder{

        public MoneyManagerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
