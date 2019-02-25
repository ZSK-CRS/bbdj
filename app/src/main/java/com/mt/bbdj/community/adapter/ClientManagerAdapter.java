package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/25
 * Description :
 */
public class ClientManagerAdapter extends RecyclerView.Adapter<ClientManagerAdapter.ClientViewHolder> {

    private Context context;
    private List<HashMap<String, String>> mapList;

    public ClientManagerAdapter(Context context, List<HashMap<String, String>> mapList) {
        this.context = context;
        this.mapList = mapList;
    }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_manager,parent,false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public ClientViewHolder(View itemView) {
            super(itemView);
        }
    }
}
