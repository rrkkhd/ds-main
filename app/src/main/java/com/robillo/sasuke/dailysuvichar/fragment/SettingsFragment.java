package com.robillo.sasuke.dailysuvichar.fragment;

import com.robillo.sasuke.dailysuvichar.R;

/**
 * Created by Sasuke on 4/30/2017.
 */

public class SettingsFragment extends BaseFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_gurus;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
}
