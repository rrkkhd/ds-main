package com.robillo.sasuke.dailysuvichar.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.fragment.LoginFragment;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.login_activity_title);
    }

    @Override
    protected boolean showActionBar() {
        return true;
    }

    @Override
    protected int setActionBarColor() {
        return R.color.blue_text;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}
