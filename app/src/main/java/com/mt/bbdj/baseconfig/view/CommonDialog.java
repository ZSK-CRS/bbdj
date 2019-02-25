package com.mt.bbdj.baseconfig.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.List;


/**
 * Created by JYY on 2017/8/4.
 */

public class CommonDialog {

    public static class Builder {

        private Context mContext;
        private Dialog mDialog;
        private ViewHolder mViewHolder;

        private View mView;
        private boolean hasPos = false, hasNeg = false;
        private boolean messageType = false;    //false : 只显示一条信息  true ：显示三条信息
        private int type = -1;    //根据类型判断加载的布局  1： 表示的只有一个选项。2：表示的是有两个选项

        public Builder(Activity context, int type, boolean messageType) {
            mContext = context;
            this.messageType = messageType;
            this.type = type;
            initView();
        }

        public Builder setTitle(CharSequence title) {
            mViewHolder.tvTitle.setText(title);
            return this;
        }

        public Builder setTitle(CharSequence title, int color) {
            mViewHolder.tvTitle.setText(title);
            mViewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public Builder setTitle(int resid) {
            mViewHolder.tvTitle.setText(resid);
            return this;
        }

        public Builder setTitle(int resid, int color) {
            mViewHolder.tvTitle.setText(resid);
            mViewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public Builder setMessage(CharSequence title) {
            mViewHolder.tvMessage.setText(title);
            return this;
        }

        public Builder setMessage(List<String> list) {
            mViewHolder.tvMessage.setText(list.get(0));
            mViewHolder.tvMessage1.setText(list.get(1));
            mViewHolder.tvMessage2.setText(list.get(2));
            mViewHolder.tvMessage3.setText(list.get(3));
            return this;
        }
        public Builder setMessage2(List<String> list) {
            mViewHolder.tvMessage.setText(list.get(0));
            mViewHolder.tvMessage1.setText(list.get(1));
            mViewHolder.tvMessage2.setText(list.get(2));
            return this;
        }
        public Builder setMessage1(List<String> list) {
            mViewHolder.tvMessage.setText(list.get(0));
            mViewHolder.tvMessage1.setText(list.get(1));
            return this;
        }
        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setMessage(CharSequence title, int color) {
            mViewHolder.tvMessage.setText(title);
            mViewHolder.tvMessage.setTextColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public Builder setMessage(int resid) {
            mViewHolder.tvMessage.setText(resid);
            return this;
        }

        public Builder setMessage(int resid, int color) {
            mViewHolder.tvMessage.setText(resid);
            mViewHolder.tvMessage.setTextColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
            mViewHolder.tvPositiveButton.setVisibility(View.VISIBLE);
            hasPos = true;
            mViewHolder.tvPositiveButton.setText(text);
            mViewHolder.tvPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener, int color) {
            mViewHolder.tvPositiveButton.setVisibility(View.VISIBLE);
            hasPos = true;
            mViewHolder.tvPositiveButton.setText(text);
            mViewHolder.tvPositiveButton.setTextColor(ContextCompat.getColor(mContext, color));
            mViewHolder.tvPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
            mViewHolder.tvNegativeButton.setVisibility(View.VISIBLE);
            hasNeg = true;
            mViewHolder.tvNegativeButton.setText(text);
            mViewHolder.tvNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener, int color) {
            mViewHolder.tvNegativeButton.setVisibility(View.VISIBLE);
            hasNeg = true;
            mViewHolder.tvNegativeButton.setText(text);
            mViewHolder.tvNegativeButton.setTextColor(ContextCompat.getColor(mContext, color));
            mViewHolder.tvNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            return this;
        }

        public Builder setCancelable(boolean flag) {
            mDialog.setCancelable(flag);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean flag) {
            mDialog.setCanceledOnTouchOutside(flag);
            return this;
        }

        public Dialog create() {
            return mDialog;
        }

        public void show() {
            if (mDialog != null) {
                if (hasPos || hasNeg) {
                    mViewHolder.line1.setVisibility(View.VISIBLE);
                }
                if (hasPos && hasNeg) {
                    mViewHolder.line2.setVisibility(View.VISIBLE);
                }
                mDialog.show();
            }
        }

        public void dismiss() {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }

        private void initView() {
            mDialog = new Dialog(mContext, R.style.EasyDialogStyle);
            mView = LayoutInflater.from(mContext).inflate(R.layout.layout_common_select_dialog, null);
            mViewHolder = new ViewHolder(mView);
            mDialog.setContentView(mView);

            DisplayMetrics dm = new DisplayMetrics();   //获取屏幕的大小
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);//获取WindowManager
            windowManager.getDefaultDisplay().getMetrics(dm);   //是获取到Activity的实际屏幕信息
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.width = (int) (dm.widthPixels * 0.85);
            mDialog.getWindow().setAttributes(lp);
        }


        class ViewHolder {

            TextView tvTitle;
            TextView tvMessage,tvMessage1,tvMessage2,tvMessage3;
            TextView tvPositiveButton, tvNegativeButton;
            LinearLayout vgLayout;
            View line1, line2;

            public ViewHolder(View view) {
                tvTitle = (TextView) view.findViewById(R.id.dialog_title);
                tvMessage = (TextView) view.findViewById(R.id.dialog_message);
                tvMessage1 = (TextView) view.findViewById(R.id.dialog_message_1);
                tvMessage2 = (TextView) view.findViewById(R.id.dialog_message_2);
                tvMessage3 = (TextView) view.findViewById(R.id.dialog_message_3);
                if (messageType) {
                    tvMessage1.setVisibility(View.VISIBLE);
                    tvMessage2.setVisibility(View.VISIBLE);
                    tvMessage3.setVisibility(View.VISIBLE);
                } else {
                    tvMessage1.setVisibility(View.GONE);
                    tvMessage2.setVisibility(View.GONE);
                    tvMessage3.setVisibility(View.GONE);
                }
                if (type == 1) {
                    tvPositiveButton = (TextView) view.findViewById(R.id.dialog_positive_one);
                    tvNegativeButton = (TextView) view.findViewById(R.id.dialog_negative_one);
                } else {
                    tvPositiveButton = (TextView) view.findViewById(R.id.dialog_positive);
                    tvNegativeButton = (TextView) view.findViewById(R.id.dialog_negative);
                }
                vgLayout = (LinearLayout) view.findViewById(R.id.dialog_layout);
                line1 = view.findViewById(R.id.dialog_line1);
                line2 = view.findViewById(R.id.dialog_line2);
            }
        }

    }

}
