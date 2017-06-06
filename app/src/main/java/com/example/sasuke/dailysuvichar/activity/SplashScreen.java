package com.example.sasuke.dailysuvichar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.sasuke.dailysuvichar.newactivities.NewMainActivity;
import com.example.sasuke.dailysuvichar.utils.SharedPrefs;
import com.facebook.AccessToken;

/**0
 * Created by Sasuke on 4/27/2017.
 */

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPrefs.setDefaults();

        if (SharedPrefs.getLoginToken() != null) {
            Log.e("CASE", "LOGIN TOKEN NOT NULL");

            if(SharedPrefs.getIsProfileSet()==null){
                startActivity(MainActivity.newIntent(this));
            }
            else {
                startActivity(MainActivity.newIntent(this));
            }

        }
        else {
            Log.e("CASE", "LOGIN TOKEN NULL");
            if (AccessToken.getCurrentAccessToken() != null) {
                if(SharedPrefs.getIsProfileSet()==null){
                    startActivity(NewMainActivity.newIntent(this));
                }
                else {
                    startActivity(NewMainActivity.newIntent(this));
                }
            } else {
                Log.e("CASE", "VERY START");
                startActivity(MainActivity.newIntent(this));
            }
        }
    }
}