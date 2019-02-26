package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/25
 * Description :
 */
public class ClientDetailAdapter extends RecyclerView.Adapter<ClientDetailAdapter.ClientViewHolder> {

    private List<HashMap<String, String>> mapList = new ArrayList<>();
    private Context context;


    public ClientDetailAdapter(Context context, List<HashMap<String, String>> mapList) {
        this.context = context;
        this.mapList = mapList;
    }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {
        public ClientViewHolder(View itemView) {
            super(itemView);
        }
    }
}
