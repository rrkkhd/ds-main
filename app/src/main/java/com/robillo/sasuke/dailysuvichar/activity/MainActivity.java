package com.robillo.sasuke.dailysuvichar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.utils.SharedPrefs;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rg_language)
    RadioGroup mRgLanguage;
    @BindView(R.id.rb_eng)
    RadioButton mRbEnglish;
    @BindView(R.id.rb_hindi)
    RadioButton mRbHindi;


    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(SharedPrefs.getIsLanguageSet()!=null){
            if(SharedPrefs.getIsLanguageSet().equals("TRUE")){
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    @OnClick(R.id.rb_eng)
    public void selectedEng() {
        Locale l = new Locale("en");
        Locale.setDefault(l);
        Configuration config = new Configuration();
        config.locale = l;
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(config, displayMetrics);
    }

    @OnClick(R.id.rb_hindi)
    public void selectedHindi() {
        Locale l = new Locale("hi");
        Locale.setDefault(l);
        Configuration config = new Configuration();
        config.locale = l;
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(config, displayMetrics);
    }

    @OnClick(R.id.btn_next)
    public void openLoginActivity() {
        SharedPrefs.setIsLanguageSet("TRUE");
        startActivity(LoginActivity.newIntent(this));
    }
}
