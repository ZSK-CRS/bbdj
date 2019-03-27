package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.view.CustomHScrollView;
import com.mt.bbdj.community.activity.MoneyFormatManagerActivity;
import com.mt.bbdj.community.activity.YesterdSendPayActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/26
 * Description :
 */
public class YesterDaySendAdapter extends
        RecyclerView.Adapter<YesterDaySendAdapter.RegistrationObserverViewHolder> {

    private List<HashMap<String, String>> data;
    private LinearLayout mHead;
    private Context mContext;
    private double n;
    private int type;

    private String country;
    private CustomHScrollView headSrcrollView;

    //点击标记位
    private int touchPosition = -1;

    public int getTouchPosition() {
        return touchPosition;
    }

    public void setTouchPosition(int touchPosition) {
        this.touchPosition = touchPosition;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view, int position);
    }

    private YesterDaySendAdapter.OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(YesterDaySendAdapter.OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public YesterDaySendAdapter(Context context, List<HashMap<String, String>> data, LinearLayout head, int type) {
        this.data = data;
        this.mHead = head;
        this.mContext = context;
        this.type = type;
        notifyDataSetChanged();
    }

    public YesterDaySendAdapter(Context context, List<HashMap<String, String>> data, int type) {
        this.data = data;
        this.mContext = context;
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public YesterDaySendAdapter.RegistrationObserverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title_, parent, false);
        YesterDaySendAdapter.RegistrationObserverViewHolder viewHolder = new YesterDaySendAdapter.RegistrationObserverViewHolder(view);
        headSrcrollView = (CustomHScrollView) mHead.findViewById(R.id.h_scrollView);
        headSrcrollView.AddOnScrollChangedListener(new YesterDaySendAdapter.OnScrollChangedListenerImp(viewHolder.scrollView));
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(final YesterDaySendAdapter.RegistrationObserverViewHolder holder, final int position) {
        HashMap<String, String> map = data.get(position);

        holder.textView1.setText(map.get("number"));
        holder.textView2.setText(map.get("express_name"));
        holder.textView3.setText(map.get("waybill_number"));
        holder.textView4.setText(map.get("people"));
        holder.textView5.setText(map.get("region"));
        holder.textView6.setText(map.get("weight"));
        holder.textView7.setText(map.get("service_money"));
        holder.textView8.setText(map.get("shipping_money"));
        holder.textView9.setText(map.get("time"));

     /*   holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setTouchPosition(position);
                return false;
            }
        });*/
    }


    class OnScrollChangedListenerImp implements CustomHScrollView.OnScrollChangedListener {
        CustomHScrollView mScrollViewArg;

        public OnScrollChangedListenerImp(CustomHScrollView scrollViewar) {
            mScrollViewArg = scrollViewar;
        }

        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            mScrollViewArg.smoothScrollTo(l, t);
            if (n == 1) {//记录滚动的起始位置，避免因刷新数据引起错乱
                if (type == 1) {
                    ((YesterdSendPayActivity) mContext).setPosData(oldl, oldt);
                    notifyDataSetChanged();
                } else {
                    ((YesterdSendPayActivity) mContext).setPosData(oldl, oldt);
                }
            }
            n++;
        }
    }

    ;

    class RegistrationObserverViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView6;
        private TextView textView7;
        private TextView textView8;
        private TextView textView9;
        private CustomHScrollView scrollView;

        public RegistrationObserverViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.textView_1);
            textView2 = (TextView) itemView.findViewById(R.id.textView_2);
            textView3 = (TextView) itemView.findViewById(R.id.textView_3);
            textView4 = (TextView) itemView.findViewById(R.id.textView_4);
            textView5 = (TextView) itemView.findViewById(R.id.textView_5);
            textView6 = (TextView) itemView.findViewById(R.id.textView_6);
            textView7 = (TextView) itemView.findViewById(R.id.textView_7);
            textView8 = (TextView) itemView.findViewById(R.id.textView_8);
            textView9 = (TextView) itemView.findViewById(R.id.textView_9);
            scrollView = (CustomHScrollView) itemView.findViewById(R.id.h_scrollView);
        }
    }
}