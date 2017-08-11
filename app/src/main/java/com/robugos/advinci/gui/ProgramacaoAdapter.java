package com.robugos.advinci.gui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Robson on 09/08/2017.
 */

public class ProgramacaoAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ProgramacaoAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ProgramacaoRecFragment tab1 = new ProgramacaoRecFragment();
                return tab1;
            case 1:
                ProgramacaoAllFragment tab2 = new ProgramacaoAllFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
