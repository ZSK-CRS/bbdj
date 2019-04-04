package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/2
 * Description :
 */
public class ChangeManager2Adapter extends RecyclerView.Adapter<ChangeManager2Adapter.ChangeManagerViewHolder> {

    private List<HashMap<String,String>> mList;

    public ChangeManager2Adapter(List<HashMap<String,String>> mList) {
        this.mList = mList;
    }

    private OnItemAddmarkClick onItemAddmarkClick;
    //添加备注信息
    public void setOnItemAddmarkClick(OnItemAddmarkClick onItemAddmarkClick) {
        this.onItemAddmarkClick = onItemAddmarkClick;
    }

    @Override
    public ChangeManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_mananger,parent,false);
        return new ChangeManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChangeManagerViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        holder.tvYundan.setText(map.get("waybill_number"));
        holder.tvDingdanTime.setText(map.get("time"));
        holder.tvKuaidi.setText(map.get("express_name"));
        holder.tvPersson.setText(map.get("person"));
        holder.tvAddress.setText(map.get("address"));
        holder.tvWeight.setText(map.get("goods_weight")+"kg");
        String conntent = map.get("content");
        String isShowAddMark = map.get("isShowAddMark");

        //判断是否显示添加备注按钮
     /*   if ("0".equals(isShowAddMark)) {
            holder.tvAddMark.setVisibility(View.VISIBLE);
        } else{
            holder.tvAddMark.setVisibility(View.GONE);
        }*/

        //是否显示备注布局
       /* if ("".equals(conntent)) {
            holder.ll_mark.setVisibility(View.GONE);
        } else {
            holder.ll_mark.setVisibility(View.VISIBLE);
            holder.tvMark.setText(conntent);
        }*/

        String type = map.get("type");
        if ("1".equals(type)) {
            holder.ll_change_time.setVisibility(View.GONE);
        } else {
            holder.ll_change_time.setVisibility(View.VISIBLE);
        }
        holder.tvChangeTime.setText(map.get("handover_time"));

        //添加备注信息
        holder.tvAddMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemAddmarkClick != null) {
                    onItemAddmarkClick.addMarkClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ChangeManagerViewHolder extends RecyclerView.ViewHolder{

        TextView  tvYundan;    //运单号
        TextView tvDingdanTime;   //运单时间
        TextView tvKuaidi;      //快递公司
        TextView tvPersson;      //收件人和寄件人
        TextView tvAddress;    //寄件人收件人
        TextView tvWeight;    //重量
        TextView tvChangeTime;   //交接时间
        LinearLayout ll_change_time;   //交接时间
        LinearLayout ll_mark;   //备注布局
        TextView tvMark;   //备注
        TextView tvAddMark;    //添加备注

       public ChangeManagerViewHolder(View itemView) {
           super(itemView);
           tvYundan = itemView.findViewById(R.id.tv_yundan);
           tvDingdanTime = itemView.findViewById(R.id.tv_dingdan_time);
           tvKuaidi = itemView.findViewById(R.id.tv_kuaidi);
           tvPersson = itemView.findViewById(R.id.tv_persson);
           tvAddress = itemView.findViewById(R.id.tv_address);
           tvWeight = itemView.findViewById(R.id.tv_weight);
           tvChangeTime = itemView.findViewById(R.id.tv_change_time);
           ll_change_time = itemView.findViewById(R.id.ll_change_time);
           ll_mark = itemView.findViewById(R.id.ll_mark);
           tvMark = itemView.findViewById(R.id.tv_mark);
           tvAddMark = itemView.findViewById(R.id.tv_add_mark);
       }
   }


   //###############################  添加备注  ###############################
   public interface OnItemAddmarkClick{
        void addMarkClick(int position);
   }
}
