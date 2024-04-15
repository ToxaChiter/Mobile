package com.example.mynotes_pechko;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mynotes_pechko.Fragments.FragmentAdd;
import com.example.mynotes_pechko.Fragments.FragmentDel;
import com.example.mynotes_pechko.Fragments.FragmentShow;
import com.example.mynotes_pechko.Fragments.FragmentUpdate;


public class MyAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;

    public MyAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return FragmentShow.newInstance(position);
            case 1:
                return FragmentAdd.newInstance(position);
            case 2:
                return FragmentDel.newInstance(position);
            case 3:
                return FragmentUpdate.newInstance(position);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
