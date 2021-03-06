package com.robillo.sasuke.dailysuvichar.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.models.Guru;
import com.robillo.sasuke.dailysuvichar.view.adapter.RVGuruAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class GurusFragment extends BaseFragment {

    private RVGuruAdapter mRvGuruAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Guru> guruList;
    private String MOTIVATION_GURU = "Motivation Guru";
    private String AYURVEDA_GURU = "Ayurveda Guru";
    private String YOGA_GURU = "Yoga Guru";
    private String PANDIT = "Pandit";
    private String ASTROLOGY_GURU = "Astrology Guru";
    private DatabaseReference mDatabaseReference,mDatabaseReferenceUsers;
    private StorageReference mStorageReference;
    private static FirebaseUser mFirebaseUser;
    private LinearLayoutManager mLayoutManager;
    Items items;
    HashMap<String, Integer> guruMap;
    private MultiTypeAdapter mAdapter;
    private static ArrayList<String> following;
//    private static ArrayList<String> guruFollowers;
    private static Integer getFollowerCount;


    @BindView(R.id.recyclerview)
    public RecyclerView rv;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_gurus;
    }

    public static GurusFragment newInstance() {
        return new GurusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference("profile").child("user").child("dp");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("gurus").child("official");
        mDatabaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid());

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(gridLayoutManager);

        following= new ArrayList<>();
//        guruFollowers= new ArrayList<>();
        guruList = new ArrayList<>();
        guruMap = new HashMap<>();

        mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Guru guru = postSnapshot.getValue(Guru.class);
                    guru.setGuruUid(postSnapshot.getKey());
                    guru.setStorageReference(mStorageReference.child(guru.getUid()));
                    if(guruMap.containsKey(postSnapshot.getKey())){
                        guruList.set(guruMap.get(postSnapshot.getKey()),guru);
                    }else {
                        guruList.add(guru);
                        guruMap.put(postSnapshot.getKey(), guruList.indexOf(guru));
                    }
                    Log.d(TAG, "onDataChange: count "+guru.getFollowersCount());
//                    if(postSnapshot.child("followers").getValue()!=null){
//                        guruFollowers.addAll((Collection<? extends String>) postSnapshot.child("followers").getValue());
//                    }
//                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                    mRvGuruAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("following").getValue()!=null){
                    following.addAll((Collection<? extends String>) dataSnapshot.child("following").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


//        guruList.add(new Guru("Guru Robin", 721, ASTROLOGY_GURU));
//        guruList.add(new Guru("Shankar Ji", 210, ASTROLOGY_GURU));
//        guruList.add(new Guru("Baba Ramdev", 4324, YOGA_GURU));
//        guruList.add(new Guru("Baba Afsal", 251, MOTIVATION_GURU));
//        guruList.add(new Guru("Baba ABC", 321, YOGA_GURU));
//        guruList.add(new Guru("Guru Tagore", 11, AYURVEDA_GURU));
//        guruList.add(new Guru("Nafsar Guru", 110, PANDIT));
//        guruList.add(new Guru("Guru shiv", 9, MOTIVATION_GURU));
//        guruList.add(new Guru("Guru Kant", 72, AYURVEDA_GURU));
//        guruList.add(new Guru("Guru Narayan", 121, PANDIT));
//
//        guruList.add(new Guru("Guru Robillo", 721, ASTROLOGY_GURU));
//        guruList.add(new Guru("Shanku Chandler", 210, ASTROLOGY_GURU));
//        guruList.add(new Guru("Baba Devram", 4324, YOGA_GURU));
//        guruList.add(new Guru("Baba Afsos", 251, MOTIVATION_GURU));
//        guruList.add(new Guru("Baba DEF", 321, YOGA_GURU));
//        guruList.add(new Guru("Guru Treeger", 11, AYURVEDA_GURU));
//        guruList.add(new Guru("Rachel Mata", 110, PANDIT));
//        guruList.add(new Guru("Guru Ross", 9, MOTIVATION_GURU));
//        guruList.add(new Guru("Guru Joey", 72, AYURVEDA_GURU));
//        guruList.add(new Guru("Guru Monica", 121, PANDIT));

        rv.setAdapter(mRvGuruAdapter);

        return v;
    }

    public static void setFollowing(ArrayList<String> guruFollowers, Integer followerCount, boolean isFollowing, String guruUid){
        DatabaseReference mDatabaseReferenceGuru = FirebaseDatabase.getInstance().getReference("gurus").child("official");
        DatabaseReference mDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid());

        if(guruFollowers ==null){
            guruFollowers = new ArrayList<>();
        }

        if(isFollowing){
            if(!following.contains(guruUid)) {
                following.add(guruUid);
//                mDatabaseReferenceUser.child("following").setValue(following);
            }
            guruFollowers.add(mFirebaseUser.getUid());
            mDatabaseReferenceGuru.child(guruUid).child("followersCount").setValue(followerCount + 1);
            mDatabaseReferenceGuru.child(guruUid).child("followers").setValue(guruFollowers);
        }else{
            if(followerCount>0){
                mDatabaseReferenceGuru.child(guruUid).child("followersCount").setValue(followerCount - 1);
            }
            following.remove(guruUid);
            guruFollowers.remove(mFirebaseUser.getUid());
//            mDatabaseReferenceUser.child("following").setValue(following);
            mDatabaseReferenceGuru.child(guruUid).child("followers").setValue(guruFollowers);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort: {
                showPopup();
                break;
            }
            case R.id.about: {

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Sorting Option:")
                .setItems(new CharSequence[]{"Followers", "Astrology Gurus", "Yoga Gurus", "Pandits",
                "Motivation Gurus", "Ayurveda Gurus", "Show All"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case 0: {
                                //Filter By Followers
                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList);
                                break;
                            }
                            case 1: {
                                //Astrology Gurus1
//                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 1);
                                rv.setAdapter(mRvGuruAdapter);
                                break;
                            }
                            case 2: {
                                //Yoga Gurus2
//                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 2);
                                rv.setAdapter(mRvGuruAdapter);
                                break;
                            }
                            case 3: {
                                //Pandits Gurus3
//                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 3);
                                rv.setAdapter(mRvGuruAdapter);
                                break;
                            }
                            case 4: {
                                //Motivation Gurus4
//                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 4);
                                rv.setAdapter(mRvGuruAdapter);
                                break;
                            }
                            case 5: {
                                //Ayurveda Gurus5
//                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 5);
                                rv.setAdapter(mRvGuruAdapter);
                                break;
                            }
                            case 6: {
                                //SHOW ALL
                                mRvGuruAdapter = new RVGuruAdapter(getActivity(), guruList, 100);
                                rv.setAdapter(mRvGuruAdapter);
                            }
                        }
                    }
                });
        builder.show();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}