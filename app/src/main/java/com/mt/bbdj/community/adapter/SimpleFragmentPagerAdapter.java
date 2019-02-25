package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2019/1/5
 * Description :  fragment适配器
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mList;
    private ArrayList<String> mListString;
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment>  mList, ArrayList<String> mListString) {
        super(fm);
        this.context = context;
        this.mList = mList;
        this.mListString = mListString;
    }
    private Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        //记录每个position位置最后显示的Fragment
        map.put(position, fragment);
        return fragment;
    }
    //获取指定位置最后显示的Fragment
    public Fragment getCurrentFragment(int index) {
        return map.get(index);
    }
    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mListString.get(position);
    }
}
