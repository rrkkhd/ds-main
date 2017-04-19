package com.example.sasuke.dailysuvichar.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.sasuke.dailysuvichar.R;
import com.example.sasuke.dailysuvichar.fragment.LoginFragment;

/**
 * Created by Sasuke on 4/17/2017.
 */

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected String setActionBarTitle() {
        return "Login";
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
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
