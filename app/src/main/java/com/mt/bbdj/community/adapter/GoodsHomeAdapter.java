package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.CategoryBean;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.GridViewForScrollView;
import com.mt.bbdj.baseconfig.view.MarginDecoration;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/18
 * Description :
 */
public class GoodsHomeAdapter extends RecyclerView.Adapter<GoodsHomeAdapter.GoodsMenuViewHolder> {

    private List<CategoryBean.DataBean> menuList;

    private Context context;

    private OnItemClickListener itemClickListener;


    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public GoodsHomeAdapter(Context context, List<CategoryBean.DataBean> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public GoodsMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_home, parent, false);
        return new GoodsMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsMenuViewHolder holder, final int position) {
        CategoryBean.DataBean dataBean = menuList.get(position);
        holder.menuName.setText(dataBean.getMenuTitle());
        HomeItemAdapter homeItemAdapter = new HomeItemAdapter(context,dataBean.getDataList());
        holder.gridViewForScrollView.setAdapter(homeItemAdapter);
        if (dataBean.getDataList().size() == 0) {
            holder.menuName.setVisibility(View.GONE);
        } else {
            holder.menuName.setVisibility(View.VISIBLE);
        }
        holder.gridViewForScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int itemPosition, long id) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position,itemPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class GoodsMenuViewHolder extends RecyclerView.ViewHolder {

        private TextView menuName;

        private GridViewForScrollView gridViewForScrollView;

        public GoodsMenuViewHolder(View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.blank);
            gridViewForScrollView = itemView.findViewById(R.id.gridView);
        }
    }

    //########################################################################
    public interface OnItemClickListener{
        void onItemClick(int position,int itemPosition);
    }
}
