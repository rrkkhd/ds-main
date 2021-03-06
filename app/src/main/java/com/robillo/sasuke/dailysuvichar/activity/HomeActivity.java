package com.robillo.sasuke.dailysuvichar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.fragment.MainFragment;
import com.robillo.sasuke.dailysuvichar.fragment.SettingsFragment;
import com.robillo.sasuke.dailysuvichar.newactivities.NewHomeyActivity;
import com.robillo.sasuke.dailysuvichar.newactivities.NewGurusActivity;
import com.robillo.sasuke.dailysuvichar.newactivities.NewExploreyActivity;
import com.robillo.sasuke.dailysuvichar.utils.SharedPrefs;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_button)
    ImageButton drawerButton;
    @BindView(R.id.imageView)
    CircularImageView drawerDP;

    private static final int DELAY_TIME = 2000;
    private static long back_pressed;
    private StorageReference mStorageReferenceDP;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersDatabase;
    private FirebaseUser mFirebaseUser;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        replaceFragment(MainFragment.newInstance(), MainFragment.class.getName());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageReferenceDP = FirebaseStorage.getInstance().getReference("profile").child("user").child("dp");

        fetchData();
    }

    private void fetchData() {

        mUsersDatabase.child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!HomeActivity.this.isDestroyed()) {
                    if (dataSnapshot.child("photoUrl").getValue() != null) {
                        Glide.with(HomeActivity.this).
                                using(new FirebaseImageLoader())
                                .load(mStorageReferenceDP.child(dataSnapshot.getKey()))
                                .fitCenter()
                                .placeholder(R.drawable.profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(drawerDP);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @OnClick(R.id.drawer_button)
    public void controlDrawer(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (back_pressed + DELAY_TIME > System.currentTimeMillis()) {
            back_pressed = 0;
            super.onBackPressed();
        } else {
            Toast.makeText(this, getResources().getString(R.string.click_back_again_to_exit), Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }

    @OnClick(R.id.nav_home)
    public void onMyFeedClick() {
//        closeDrawer();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                replaceFragment(MainFragment.newInstance(), MainFragment.class.getName());
//            }
//        }, 250);
        startActivity(new Intent(this, NewHomeyActivity.class));
    }
//
//    @OnClick(R.id.nav_updates)
//    public void onMyTripsClick() {
//        closeDrawer();
//        replaceFragment(NotificationFragment.newInstance(), NotificationFragment.class.getName());
//    }

    @OnClick(R.id.nav_profile)
    public void onProfileClick() {
        closeDrawer();
        Intent i =new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra("fromHome",1);
        startActivity(i);
//        finish();
    }

//    @OnClick(R.id.your_feeds)
//    public void onYourFeedsClick() {
//        closeDrawer();
//        replaceFragment(YourFeedsFragment.newInstance(), YourFeedsFragment.class.getName());
//    }

    @OnClick(R.id.nav_settings)
    public void onSettingsClick() {
        closeDrawer();
        replaceFragment(SettingsFragment.newInstance(), SettingsFragment.class.getName());
    }

    @OnClick(R.id.nav_log_out)
    public void onLogOutClick() {
        closeDrawer();
        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null) {
            facebookLogout();
        } else {
            removeUser();
        }
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    public void removeUser() {
        SharedPrefs.clearLoggedInUser();
        Intent intent = LoginActivity.newIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void facebookLogout() {
        LoginManager.getInstance().logOut();
        removeUser();
    }

    private void replaceFragment(final Fragment fragment, final String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        List<Fragment> fragments = manager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null) {
                    transaction.hide(f);
                }
            }
        }
        Fragment f = manager.findFragmentByTag(tag);
        if (f == null) {
            f = fragment;
            transaction.add(R.id.fragment_container, f, tag);
        } else {
            transaction.show(f);
        }
        transaction.commit();
    }

    @OnClick(R.id.home_activity)
    public void homeActivity(){
        startActivity(new Intent(this, NewExploreyActivity.class));
    }

    @OnClick(R.id.gurus_activity)
    public void gurusActivity(){
        startActivity(new Intent(this, NewGurusActivity.class));
    }

    @OnClick(R.id.explore_activity)
    public void exploreActivity(){
        startActivity(new Intent(this, NewHomeyActivity.class));
    }

    @Override
    protected void onDestroy() {
        Glide.clear(drawerDP);
        finish();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
