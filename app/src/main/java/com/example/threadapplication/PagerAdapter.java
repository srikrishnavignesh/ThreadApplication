package com.example.threadapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.threadapplication.events_display.EventsFragment;
import com.example.threadapplication.payment_validation.IdFragment;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position)
        {
            //1st tab
            case 0:return new EventsFragment();

            //2nd tab
            case 1:return new IdFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
