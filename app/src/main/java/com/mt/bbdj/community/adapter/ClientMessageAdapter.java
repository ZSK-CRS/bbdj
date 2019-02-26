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
public class ClientMessageAdapter extends RecyclerView.Adapter<ClientMessageAdapter.ClientMessageViewHolder> {

    private List<HashMap<String,String>> mList ;

    private Context context;

    private OnItemDeleteListener onItemDeleteListener;

    private OnItemEditListener onItemEditListener;

    public void setOnItemEditListener(OnItemEditListener onItemEditListener) {
        this.onItemEditListener = onItemEditListener;
    }


    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public ClientMessageAdapter(Context context, List<HashMap<String,String>> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public ClientMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_message_detail,parent,false);
        return new ClientMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientMessageViewHolder holder, final int position) {
        HashMap<String,String> map = mList.get(position);
        holder.name.setText(map.get("customer_realname"));
        holder.phone.setText(map.get("customer_telephone"));
        holder.company.setText(map.get("company_name"));
        holder.address.setText(map.get("customer_region")+map.get("customer_address"));

        //删除
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(position);
                }
            }
        });

        //编辑
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEditListener != null) {
                    onItemEditListener.onItemEdit(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ClientMessageViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView phone;
        TextView company;
        TextView address;
        TextView edit;
        TextView delete;

        public ClientMessageViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            phone = itemView.findViewById(R.id.tv_item_phone);
            company = itemView.findViewById(R.id.tv_item_company);
            address = itemView.findViewById(R.id.tv_item_address);
            edit = itemView.findViewById(R.id.tv_item_edit);
            delete = itemView.findViewById(R.id.tv_item_delete);
        }
    }

    //************************************* 接口回调 **********************************

    public interface OnItemDeleteListener{
        void onItemDelete(int posiiton);
    }

    public interface OnItemEditListener{
        void onItemEdit(int position);
    }
}
