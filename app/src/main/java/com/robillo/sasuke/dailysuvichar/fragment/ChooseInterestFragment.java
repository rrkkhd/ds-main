package com.robillo.sasuke.dailysuvichar.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.activity.SubInterestActivity;
import com.robillo.sasuke.dailysuvichar.utils.SharedPrefs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class ChooseInterestFragment extends BaseFragment implements BubblePickerListener {

    @BindView(R.id.picker)
    BubblePicker mPicker;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private static final int BUBBLE_COLOR = 0xffab110b;
    private static final int BUBBLE_SIZE = 20;
    private static final BubbleGradient BUBBLE_GRADIENT = null; //ADD GRADIENT IF NEEDED IN FUTURE
    private static final int TEXT_SIZE = 40;
    private static final int TEXT_COLOR = 0xffffffff;
    private static final float OVERLAY_ALPHA = 0.2f;
    private static final boolean ICON_ON_TOP = true;

    private ArrayList<String> diet, yoga, health, religion, motivation, ayurveda, astrology;

    private ArrayList<String> mSelectedInterests;
    private ArrayList<String> mSelectedSubInterests;
    private HashMap<String, ArrayList<String>> mAllInterests;
    private HashMap<String, ArrayList<String>> mMap;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_choose_interest;
    }

    public static ChooseInterestFragment newInstance() {
        return new ChooseInterestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mSelectedInterests = new ArrayList<>();
        mAllInterests = new HashMap<>();
        mSelectedSubInterests = new ArrayList<>();
        mMap = new HashMap<>();

        arraysInit();
        mMapInit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPicker.setItems(getItems());
        mPicker.setBubbleSize(BUBBLE_SIZE);
        mPicker.setCenterImmediately(true);
        mPicker.setListener(this);
    }

    private void mMapInit() {
        mMap.put(getString(R.string.diet__), diet);
        mMap.put(getString(R.string.yoga__), yoga);
        mMap.put(getString(R.string.health__), health);
        mMap.put(getString(R.string.religion__), religion);
        mMap.put(getString(R.string.motivation__), motivation);
        mMap.put(getString(R.string.ayurveda__), ayurveda);
        mMap.put(getString(R.string.astrology__), astrology);
    }

    private void arraysInit() {
        diet = stringToList(diet, getResources().getStringArray(R.array.diet_array));
        yoga = stringToList(yoga, getResources().getStringArray(R.array.yoga_array));
        health = stringToList(health, getResources().getStringArray(R.array.health_array));
        religion = stringToList(religion, getResources().getStringArray(R.array.religion_array));
        motivation = stringToList(motivation, getResources().getStringArray(R.array.motivation_array));
        ayurveda = stringToList(ayurveda, getResources().getStringArray(R.array.ayurveda_array));
        astrology = stringToList(astrology, getResources().getStringArray(R.array.astrology_array));
    }

    private ArrayList<String> stringToList(ArrayList<String> list, String[] array) {
        list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(i, array[i]);
        }
        return list;
    }

    private void initNull(String[] headInterest, boolean bool){

    }


    @Override
    public void onResume() {
        super.onResume();
        mPicker.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPicker.onPause();
    }

    @Override
    public void onBubbleSelected(@NotNull PickerItem pickerItem) {
        if (pickerItem.getTitle() != null) {
            mSelectedInterests.add(pickerItem.getTitle().toLowerCase());
            mAllInterests.put(pickerItem.getTitle().toLowerCase(), mMap.get(pickerItem.getTitle().toLowerCase()));
            mSelectedSubInterests.addAll(mMap.get(pickerItem.getTitle().toLowerCase()));
        }
    }

    @Override
    public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
        if (pickerItem.getTitle() != null) {
            mSelectedInterests.remove(pickerItem.getTitle().toLowerCase());
            mAllInterests.remove(pickerItem.getTitle().toLowerCase());
            mSelectedSubInterests.removeAll(mMap.get(pickerItem.getTitle().toLowerCase()));
        }
    }

    @OnClick(R.id.tv_next)
    public void openSubInterestActivity() {
        if (mSelectedInterests.size() == 0) {
            Toast.makeText(getContext(), R.string.atleasttt, Toast.LENGTH_SHORT).show();
        }
        else if(mSelectedInterests.size() == 1){
            Toast.makeText(getContext(), R.string.atleat, Toast.LENGTH_SHORT).show();
        }

        else{
            if(isOnline()) {

//            User user = new User(mSelectedSubInterests, mAllInterests);
                mDatabase.child(mFirebaseUser.getUid()).child("mAllInterests").setValue(mAllInterests);
                mDatabase.child(mFirebaseUser.getUid()).child("mSelectedSubInterests").setValue(mSelectedSubInterests);
//            mDatabase.child(mFirebaseUser.getUid()).setValue(user);
                SharedPrefs.setIsInterestsSelected("TRUE");
                startActivity(SubInterestActivity.newIntent(getActivity()));
            }else{
                Toast.makeText(getActivity(), getString(R.string.no_inter), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<PickerItem> getItems() {
        ArrayList<PickerItem> itemList = new ArrayList<>();
        itemList.add(new PickerItem(getResources().getString(R.string.astrology), getResources().getDrawable(R.drawable.ic_astrology),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR,
                TEXT_SIZE, getResources().getDrawable(R.drawable.astrology)));

        itemList.add(new PickerItem(getResources().getString(R.string.diet), getResources().getDrawable(R.drawable.ic_diet),
                ICON_ON_TOP, BUBBLE_COLOR,BUBBLE_GRADIENT , OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR, TEXT_SIZE,
                getResources().getDrawable(R.drawable.diet)));

        itemList.add(new PickerItem(getResources().getString(R.string.religion), getResources().getDrawable(R.drawable.ic_god),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR, TEXT_SIZE,
                getResources().getDrawable(R.drawable.religion)));

        itemList.add(new PickerItem(getResources().getString(R.string.yoga), getResources().getDrawable(R.drawable.ic_yoga),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR,
                TEXT_SIZE, getResources().getDrawable(R.drawable.yoga)));

        itemList.add(new PickerItem(getResources().getString(R.string.motivation), getResources().getDrawable(R.drawable.ic_motivation),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR,
                TEXT_SIZE, getResources().getDrawable(R.drawable.motivation)));

        itemList.add(new PickerItem(getResources().getString(R.string.health), getResources().getDrawable(R.drawable.ic_health),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR,
                TEXT_SIZE, getResources().getDrawable(R.drawable.health)));

        itemList.add(new PickerItem(getResources().getString(R.string.ayurveda), getResources().getDrawable(R.drawable.ic_health),
                ICON_ON_TOP, BUBBLE_COLOR, BUBBLE_GRADIENT, OVERLAY_ALPHA, Typeface.DEFAULT_BOLD, TEXT_COLOR,
                TEXT_SIZE, getResources().getDrawable(R.drawable.ayurveda)));

        return itemList;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
