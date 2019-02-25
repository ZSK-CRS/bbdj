package com.mt.bbdj.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2019/2/18
 * Description :
 */
public class GoodsMenuAdapter extends RecyclerView.Adapter<GoodsMenuAdapter.GoodsMenuViewHolder> {

    private List<String> menuList;

    private int selectPosition;   //选中item

    private OnItemClickListener itemClickListener;
    public GoodsMenuAdapter(List<String> menuList) {
         this.menuList = menuList;
    }

    public void setSelectItem(int position) {
        this.selectPosition  = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public GoodsMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_menu,parent,false);
        return new GoodsMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsMenuViewHolder holder, final int position) {
        String title = menuList.get(position);
        holder.menuName.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    itemClickListener.onItenClick(position);
                }
            }
        });

        if (selectPosition == position) {
            holder.menuName.setBackgroundColor(Color.WHITE);
            holder.menuName.setTextColor(Color.parseColor("#353535"));
            holder.menuName.setTextSize(16);
            holder.vSelect.setVisibility(View.VISIBLE);
        } else {
            holder.menuName.setBackgroundColor(Color.parseColor("#f4f4f4"));
            holder.menuName.setTextColor(Color.parseColor("#777777"));
            holder.menuName.setTextSize(14);
            holder.vSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class GoodsMenuViewHolder extends RecyclerView.ViewHolder{

        private TextView menuName;
        private View vSelect;

        public GoodsMenuViewHolder(View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.item_name);
            vSelect = itemView.findViewById(R.id.v_item_select);
        }
    }

    //#############################################################################

    public interface OnItemClickListener{
        void onItenClick(int position);
    }
}
