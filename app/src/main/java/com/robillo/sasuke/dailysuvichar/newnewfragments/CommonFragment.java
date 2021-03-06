package com.robillo.sasuke.dailysuvichar.newnewfragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.robillo.sasuke.dailysuvichar.R;
import com.robillo.sasuke.dailysuvichar.activity.AboutActivity;
import com.robillo.sasuke.dailysuvichar.models.CustomVideo;
import com.robillo.sasuke.dailysuvichar.models.Photo;
import com.robillo.sasuke.dailysuvichar.models.Status;
import com.robillo.sasuke.dailysuvichar.view.adapter.CustomVideoAdapter;
import com.robillo.sasuke.dailysuvichar.view.adapter.PhotoItemAdapter;
import com.robillo.sasuke.dailysuvichar.view.adapter.StatusItemAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragment extends Fragment {


    private static final String GURU_UID_USER = "KKdovrQ6nla0E6Npx7nEzmcm3Bh2";
    @BindView(R.id.swiperefresh_common)
    SwipeRefreshLayout mPullToRefresh;
    @BindView(R.id.alternate_layout_common)
    LinearLayout alternateLayout;

    private MultiTypeAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;
    private static FirebaseUser mFirebaseUser;
    private String uid;
    private AVLoadingIndicatorView avi;
    private HashMap<String, String> userStatus;
    private static DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferencePosts, mDatabaseReferenceGuru;
    private StorageReference mStorageReference;
    private ArrayList<String> mSelectedSubInterests;
    private ArrayList<Status> statusYouraList, statusHomeaList;
    private StorageReference mStorageReferenceDP;
    private HashMap mAllInterests;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersDatabase;
    private ArrayList<String> mSelectedGurus;

    private static final String TAG = "ALLSTATUS", STATUS = "status";
    private static final int PICK_IMAGE_REQUEST = 250;
    private Uri filePath;
    Items items;
    private Animation slide_down;
    private Animation slide_up;
    private int CHECK = 1;
    private String intentDBReference = null;
    private HashMap<String, Long> isDone;
    private HashMap<String, Status> statusHashMapStore;
    private HashMap<String, Long> statusHashMap;
    private String from = "HOME";
    private HashMap<String, Boolean> isStatusDone, isStatusDoneGuru;

    @BindView(R.id.recyclerview_common)
    RecyclerView mRvHome;

    private int lang = 2;
    private int sort = 0;
    private String sortBy = "timestamp";
    boolean bool = true;
    CustomVideoAdapter customVideoAdapter;


    public CommonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ButterKnife.bind(getActivity());
        View v = inflater.inflate(R.layout.fragment_common, container, false);

        setHasOptionsMenu(true);

        if (sort == 0) {
            sortBy = "timestamp";
        } else {
            sortBy = "likes";
        }

        String from = getArguments().getString("from");

        mRvHome = (RecyclerView) v.findViewById(R.id.recyclerview_common);
        mPullToRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh_common);
        alternateLayout = (LinearLayout) v.findViewById(R.id.alternate_layout_common);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageReferenceDP = FirebaseStorage.getInstance().getReference("profile").child("user").child("dp");

//        fetchDP();

//        avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        mRvHome.setVisibility(View.VISIBLE);

        uid = mFirebaseUser.getUid();

        Log.e(TAG, uid);

        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        customVideoAdapter = new CustomVideoAdapter();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setItemPrefetchEnabled(true);
        mLayoutManager.setInitialPrefetchItemCount(10);
        mRvHome.setLayoutManager(mLayoutManager);
        mAdapter = new MultiTypeAdapter();
        mAdapter.register(Status.class, new StatusItemAdapter());
        mAdapter.register(Photo.class, new PhotoItemAdapter());
        mAdapter.register(CustomVideo.class, new CustomVideoAdapter());
        mRvHome.setAdapter(mAdapter);

        items = new Items();

        if (isOnline()) {
            if (from.equals(getString(R.string.title_home))) {
                Log.e("FROM", from);

                isDone = new HashMap<>();
                mSelectedGurus = new ArrayList<>();

                mDatabaseReferenceGuru = FirebaseDatabase.getInstance().getReference("users");

                refresh();

                mDatabaseReferenceGuru.child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("following").getValue() != null) {
                            mSelectedGurus.addAll((Collection<? extends String>) dataSnapshot.child("following").getValue());
                        }
                        fetchGuruPostsFromFirebase(sortBy);
                        Log.d(TAG, "onDataChange: SELGURU " + mSelectedGurus);
                        alternateLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                fetchGuruPostsFromFirebase(sortBy);

                refresh();

            } else if (from.equals(getString(R.string.title_explore))) {

                isDone = new HashMap<>();
                mAllInterests = new HashMap();

                Log.e("FROM", from);

//                if(bool) {

                mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
                Log.e(TAG, uid);
                Log.e(TAG, mDatabaseReference.toString());
                bool = false;

                mDatabaseReference.child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("mAllInterests").getValue() != null) {
//                            mSelectedSubInterests.addAll((Collection<? extends String>) dataSnapshot.child("mSelectedSubInterests").getValue());
                            mAllInterests.putAll((Map<? extends String, ? extends ArrayList<String>>) dataSnapshot.child("mAllInterests").getValue());
                        }
                        fetchExplorePostsFromFirebase(sortBy);
                        alternateLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
//                }
                fetchExplorePostsFromFirebase(sortBy);

                refresh();

            } else if (from.equals(getString(R.string.title_your_feeds))) {
                Log.e("FROM", from);
                isDone = new HashMap<>();
                alternateLayout.setVisibility(View.INVISIBLE);

                fetchYourPostsFromFirebase(sortBy);

                refresh();
            }
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void fetchGuruPostsFromFirebase(String sortOption) {

        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");


        if (mSelectedGurus != null && mSelectedGurus.size() > 0) {

//            mDatabaseReferencePostsDSGuru.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");
//
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                        if (!isDone.containsKey(postSnapshot.getKey())) {
//
//                            if (postSnapshot.child("type").getValue().equals("status")) {
//                                Log.d(TAG, "onDataChange: DATA trooguru");
//                                Status statusSnap = postSnapshot.getValue(Status.class);
//                                statusSnap.setPostUid(postSnapshot.getKey());
//
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//                                items.add(statusSnap);
//
//                                Log.d(TAG, "onDataChange: trooguru " + items + " " + isDone + " " + statusSnap.getStatus());
//                            } else if (postSnapshot.child("type").getValue().equals("photo")) {
//                                Photo photoSnap = postSnapshot.getValue(Photo.class);
//                                photoSnap.setPostUid(postSnapshot.getKey());
//                                photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//
//                                items.add(photoSnap);
//                            } else if (postSnapshot.child("type").getValue().equals("video")) {
//                                final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
//                                if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
//                                    videoSnap.setPostUid(postSnapshot.getKey());
//                                    isDone.put(postSnapshot.getKey(), (long) 1);
//                                    items.add(videoSnap);
//                                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
//                                    mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            videoSnap.setVideoURI(uri.toString());
//                                        }
//                                    });
//                                }
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
//                }
//            });

            if(mSelectedGurus.contains("KKdovrQ6nla0E6Npx7nEzmcm3Bh2F")){
                mSelectedGurus.add("KKdovrQ6nla0E6Npx7nEzmcm3Bh2");
            }
            for (String guru : mSelectedGurus) {

                Log.d(TAG, "fetchGuruPostsFromFirebase: OOOO"+mSelectedGurus);
                mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(guru).child("userPosts");

                mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d(TAG, "onDataChange: OOOO "+dataSnapshot.getChildrenCount());

                        mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());

                            if (!isDone.containsKey(postSnapshot.getKey())) {

                                if (postSnapshot.child("type").getValue().equals("status")) {
                                    Log.d(TAG, "onDataChange: DATA troo");
                                    Status statusSnap = postSnapshot.getValue(Status.class);
                                    statusSnap.setPostUid(postSnapshot.getKey());
                                    Log.d(TAG, "onDataChange: OOO "+statusSnap.getUid());

                                    isDone.put(postSnapshot.getKey(), (long) 1);
                                    items.add(statusSnap);
                                } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                    Photo photoSnap = postSnapshot.getValue(Photo.class);
                                    photoSnap.setPostUid(postSnapshot.getKey());
                                    photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                    isDone.put(postSnapshot.getKey(), (long) 1);

                                    items.add(photoSnap);
                                } else if (postSnapshot.child("type").getValue().equals("video")) {
                                    final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                    if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                        videoSnap.setPostUid(postSnapshot.getKey());
                                        isDone.put(postSnapshot.getKey(), (long) 1);
                                        items.add(videoSnap);
                                        videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                        mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                videoSnap.setVideoURI(uri.toString());
                                            }
                                        });
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
            }

            Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void fetchYourPostsFromFirebase(String sortOption) {

        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

        mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid()).child("userPosts");

        Log.d(TAG, "fetchStatusFromFirebase: URLL " + mDatabaseReferencePosts);
        mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    if (!isDone.containsKey(postSnapshot.getKey())) {

                        if (postSnapshot.child("type").getValue().equals("status")) {
                            Log.d(TAG, "onDataChange: DATA troo");
                            Status statusSnap = postSnapshot.getValue(Status.class);
                            statusSnap.setPostUid(postSnapshot.getKey());

                            isDone.put(postSnapshot.getKey(), (long) 1);
                            items.add(statusSnap);
                        } else if (postSnapshot.child("type").getValue().equals("photo")) {
                            Photo photoSnap = postSnapshot.getValue(Photo.class);
                            photoSnap.setPostUid(postSnapshot.getKey());
                            photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                            isDone.put(postSnapshot.getKey(), (long) 1);

                            items.add(photoSnap);
                        } else if (postSnapshot.child("type").getValue().equals("video")) {
                            final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                            if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                videoSnap.setPostUid(postSnapshot.getKey());
                                isDone.put(postSnapshot.getKey(), (long) 1);
                                items.add(videoSnap);
                                videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        videoSnap.setVideoURI(uri.toString());
                                    }
                                });
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }


    private void fetchExplorePostsFromFirebase(String sortOption) {
        if (mAllInterests != null && mAllInterests.keySet().size() > 0) {
            final ArrayList<String> interests = new ArrayList<>();
            interests.addAll(mAllInterests.keySet());

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + interests);
            mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("allPosts");

            mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Log.d(TAG, "onDataChange: DATA " + postSnapshot);
                        Log.d(TAG, "onDataChange: DATA " + postSnapshot.child("type"));

                        if (!isDone.containsKey(postSnapshot.getKey())) {
                            if (!Collections.disjoint(interests, (Collection<?>) postSnapshot.child("tags").getValue())) {
                                if (postSnapshot.child("type").getValue().equals("status")) {
                                    Log.d(TAG, "onDataChange: DATA troo");
                                    Status statusSnap = postSnapshot.getValue(Status.class);
                                    if (!Objects.equals(statusSnap.getUid(), GURU_UID_USER)) {
                                        statusSnap.setPostUid(postSnapshot.getKey());
                                        isDone.put(postSnapshot.getKey(), (long) 1);
                                        items.add(statusSnap);
                                    }
                                } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                    Photo photoSnap = postSnapshot.getValue(Photo.class);
                                    if (!Objects.equals(photoSnap.getUid(), GURU_UID_USER)) {
                                        photoSnap.setPostUid(postSnapshot.getKey());
                                        photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                        isDone.put(postSnapshot.getKey(), (long) 1);
                                    }
                                    items.add(photoSnap);
                                } else if (postSnapshot.child("type").getValue().equals("video")) {
                                    final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                    if (!Objects.equals(videoSnap.getUid(), GURU_UID_USER)) {
                                        if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                            videoSnap.setPostUid(postSnapshot.getKey());
                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                            items.add(videoSnap);
                                            videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                            mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    videoSnap.setVideoURI(uri.toString());
                                                }
                                            });
                                        }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();

                            }
                        } else {

                        }

                    }
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "fetchExplorePostsFromFirebase: NULL");
        }

    }

    private void refresh() {
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                Handler handler1 = new Handler();
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        //CALL DATA HERE
                        if (isOnline()) {

                            if (lang == 2) {

                                if (from.equals(getString(R.string.title_home))) {
                                    Log.e("FROM", from);

                                    fetchGuruPostsFromFirebase(sortBy);
                                } else if (from.equals(getString(R.string.title_explore))) {

                                    Log.e("FROM", from);

                                    fetchExplorePostsFromFirebase(sortBy);

                                } else if (from.equals(getString(R.string.title_your_feeds))) {
                                    Log.e("FROM", from);

                                    fetchYourPostsFromFirebase(sortBy);
                                }
                            } else if (lang == 1) {

                                if (from.equals(getString(R.string.title_home))) {
                                    Log.e("FROM", from);

                                    fetchGuruPostsFromFirebaseHindi(sortBy);
                                } else if (from.equals(getString(R.string.title_explore))) {

                                    Log.e("FROM", from);

                                    fetchExplorePostsFromFirebaseHindi(sortBy);

                                } else if (from.equals(getString(R.string.title_your_feeds))) {
                                    Log.e("FROM", from);

                                    fetchYourPostsFromFirebaseHindi(sortBy);
                                }
                            } else if (lang == 0) {

                                if (from.equals(getString(R.string.title_home))) {
                                    Log.e("FROM", from);

                                    fetchGuruPostsFromFirebaseEnglish(sortBy);
                                } else if (from.equals(getString(R.string.title_explore))) {

                                    Log.e("FROM", from);

                                    fetchExplorePostsFromFirebaseEnglish(sortBy);

                                } else if (from.equals(getString(R.string.title_your_feeds))) {
                                    Log.e("FROM", from);

                                    fetchYourPostsFromFirebaseEnglish(sortBy);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.no_inter), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefresh.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    private void fetchYourPostsFromFirebaseEnglish(String sortOption) {


        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

        mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid()).child("userPosts");

        Log.d(TAG, "fetchStatusFromFirebase: URLL " + mDatabaseReferencePosts);
        mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("english")) {

                        if (!isDone.containsKey(postSnapshot.getKey())) {

                            if (postSnapshot.child("type").getValue().equals("status")) {
                                Log.d(TAG, "onDataChange: DATA troo");
                                Status statusSnap = postSnapshot.getValue(Status.class);
                                statusSnap.setPostUid(postSnapshot.getKey());
                                isDone.put(postSnapshot.getKey(), (long) 1);
                                items.add(statusSnap);
                            } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                Photo photoSnap = postSnapshot.getValue(Photo.class);
                                photoSnap.setPostUid(postSnapshot.getKey());
                                photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                isDone.put(postSnapshot.getKey(), (long) 1);

                                items.add(photoSnap);
                            } else if (postSnapshot.child("type").getValue().equals("video")) {
                                final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                    videoSnap.setPostUid(postSnapshot.getKey());
                                    isDone.put(postSnapshot.getKey(), (long) 1);
                                    items.add(videoSnap);
                                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                    mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            videoSnap.setVideoURI(uri.toString());
                                        }
                                    });
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();

    }

    private void fetchExplorePostsFromFirebaseEnglish(String sortOption) {

        if (mAllInterests != null && mAllInterests.keySet().size() > 0) {
            final ArrayList<String> interests = new ArrayList<>();
            interests.addAll(mAllInterests.keySet());

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + interests);
            mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("allPosts");

            mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Log.d(TAG, "onDataChange: DATA " + postSnapshot);
                        Log.d(TAG, "onDataChange: DATA " + postSnapshot.child("type"));

                        if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("english")) {

                            if (!isDone.containsKey(postSnapshot.getKey())) {

                                if (!Collections.disjoint(interests, (Collection<?>) postSnapshot.child("tags").getValue())) {


                                    if (postSnapshot.child("type").getValue().equals("status")) {
                                        Log.d(TAG, "onDataChange: DATA troo");
                                        Status statusSnap = postSnapshot.getValue(Status.class);
                                        if (!Objects.equals(statusSnap.getUid(), GURU_UID_USER)) {
                                            statusSnap.setPostUid(postSnapshot.getKey());
                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                            items.add(statusSnap);
                                        }
                                    } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                        Photo photoSnap = postSnapshot.getValue(Photo.class);
                                        if (!Objects.equals(photoSnap.getUid(), GURU_UID_USER)) {

                                            photoSnap.setPostUid(postSnapshot.getKey());
                                            photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                            isDone.put(postSnapshot.getKey(), (long) 1);

                                            items.add(photoSnap);
                                        }
                                    } else if (postSnapshot.child("type").getValue().equals("video")) {
                                        final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                        if (!Objects.equals(videoSnap.getUid(), GURU_UID_USER)) {

                                            if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                                videoSnap.setPostUid(postSnapshot.getKey());
                                                isDone.put(postSnapshot.getKey(), (long) 1);
                                                items.add(videoSnap);
                                                videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                                mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        videoSnap.setVideoURI(uri.toString());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {

                        }

                    }
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "fetchExplorePostsFromFirebase: NULL");
        }

    }


    private void fetchGuruPostsFromFirebaseEnglish(String sortOption) {

        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

//        DatabaseReference mDatabaseReferencePostsDSGuru = FirebaseDatabase.getInstance().getReference("users").child(GURU_UID_USER).child("userPosts");

        if (mSelectedGurus != null && mSelectedGurus.size() > 0) {

//            mDatabaseReferencePostsDSGuru.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");
//
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                        if (!isDone.containsKey(postSnapshot.getKey())) {
//
//                            if (postSnapshot.child("type").getValue().equals("status")) {
//                                Log.d(TAG, "onDataChange: DATA trooguru");
//                                Status statusSnap = postSnapshot.getValue(Status.class);
//                                statusSnap.setPostUid(postSnapshot.getKey());
//
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//                                items.add(statusSnap);
//
//                                Log.d(TAG, "onDataChange: trooguru " + items + " " + isDone + " " + statusSnap.getStatus());
//                            } else if (postSnapshot.child("type").getValue().equals("photo")) {
//                                Photo photoSnap = postSnapshot.getValue(Photo.class);
//                                photoSnap.setPostUid(postSnapshot.getKey());
//                                photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//
//                                items.add(photoSnap);
//                            } else if (postSnapshot.child("type").getValue().equals("video")) {
//                                final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
//                                if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
//                                    videoSnap.setPostUid(postSnapshot.getKey());
//                                    isDone.put(postSnapshot.getKey(), (long) 1);
//                                    items.add(videoSnap);
//                                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
//                                    mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            videoSnap.setVideoURI(uri.toString());
//                                        }
//                                    });
//                                }
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
//                }
//            });

            if(mSelectedGurus.contains("KKdovrQ6nla0E6Npx7nEzmcm3Bh2F")){
                mSelectedGurus.add("KKdovrQ6nla0E6Npx7nEzmcm3Bh2");
            }

            for (String guru : mSelectedGurus) {

                mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(guru).child("userPosts");

                mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("english")) {

                                if (!isDone.containsKey(postSnapshot.getKey())) {

                                    if (postSnapshot.child("type").getValue().equals("status")) {
                                        Log.d(TAG, "onDataChange: DATA troo");
                                        Status statusSnap = postSnapshot.getValue(Status.class);
                                        statusSnap.setPostUid(postSnapshot.getKey());

                                        isDone.put(postSnapshot.getKey(), (long) 1);
                                        items.add(statusSnap);
                                    } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                        Photo photoSnap = postSnapshot.getValue(Photo.class);
                                        photoSnap.setPostUid(postSnapshot.getKey());
                                        photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                        isDone.put(postSnapshot.getKey(), (long) 1);

                                        items.add(photoSnap);
                                    } else if (postSnapshot.child("type").getValue().equals("video")) {
                                        final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                        if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                            videoSnap.setPostUid(postSnapshot.getKey());
                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                            items.add(videoSnap);
                                            videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                            mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    videoSnap.setVideoURI(uri.toString());
                                                }
                                            });
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
            }

            Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void fetchYourPostsFromFirebaseHindi(String sortOption) {

        Log.d(TAG, "fetchYourPostsFromFirebaseHindi: HUEHUE");

        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

        mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid()).child("userPosts");

        Log.d(TAG, "fetchStatusFromFirebase: URLL " + mDatabaseReferencePosts);
        mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: LEN " + dataSnapshot.exists());
                Log.d(TAG, "onDataChange: LEN " + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("hindi")) {

                        if (!isDone.containsKey(postSnapshot.getKey())) {

                            Log.d(TAG, "onDataChangeYOUR: " + postSnapshot.getValue());

                            if (postSnapshot.child("type").getValue().equals("status")) {
                                Log.d(TAG, "onDataChange: DATA troo");
                                Status statusSnap = postSnapshot.getValue(Status.class);
                                statusSnap.setPostUid(postSnapshot.getKey());

                                isDone.put(postSnapshot.getKey(), (long) 1);
                                items.add(statusSnap);
                            } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                Photo photoSnap = postSnapshot.getValue(Photo.class);
                                photoSnap.setPostUid(postSnapshot.getKey());
                                photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                isDone.put(postSnapshot.getKey(), (long) 1);

                                items.add(photoSnap);
                            } else if (postSnapshot.child("type").getValue().equals("video")) {
                                final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                    videoSnap.setPostUid(postSnapshot.getKey());
                                    isDone.put(postSnapshot.getKey(), (long) 1);
                                    items.add(videoSnap);
                                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                    mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            videoSnap.setVideoURI(uri.toString());
                                        }
                                    });
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();

    }

    private void fetchExplorePostsFromFirebaseHindi(String sortOption) {

        if (mAllInterests != null && mAllInterests.keySet().size() > 0) {
            final ArrayList<String> interests = new ArrayList<>();
            interests.addAll(mAllInterests.keySet());

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + interests);
            mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("allPosts");

            mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                    final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Log.d(TAG, "onDataChange: DATA " + postSnapshot);
                        Log.d(TAG, "onDataChange: DATA " + postSnapshot.child("type"));

                        if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("hindi")) {

                            if (!isDone.containsKey(postSnapshot.getKey())) {

                                if (!Collections.disjoint(interests, (Collection<?>) postSnapshot.child("tags").getValue())) {

                                    if (postSnapshot.child("type").getValue().equals("status")) {
                                        Log.d(TAG, "onDataChange: DATA troo");
                                        Status statusSnap = postSnapshot.getValue(Status.class);
                                        if (!Objects.equals(statusSnap.getUid(), GURU_UID_USER)) {

                                            statusSnap.setPostUid(postSnapshot.getKey());

                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                            items.add(statusSnap);
                                        }
                                    } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                        Photo photoSnap = postSnapshot.getValue(Photo.class);
                                        if (!Objects.equals(photoSnap.getUid(), GURU_UID_USER)) {

                                            photoSnap.setPostUid(postSnapshot.getKey());
                                            photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                        }

                                        items.add(photoSnap);
                                    } else if (postSnapshot.child("type").getValue().equals("video")) {
                                        final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                        if (!Objects.equals(videoSnap.getUid(), GURU_UID_USER)) {

                                            if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                                videoSnap.setPostUid(postSnapshot.getKey());
                                                isDone.put(postSnapshot.getKey(), (long) 1);
                                                items.add(videoSnap);
                                                videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                                mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        videoSnap.setVideoURI(uri.toString());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {

                        }

                    }
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });

            Log.d(TAG, "fetchExplorePostsFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "fetchExplorePostsFromFirebase: NULL");
        }

    }

    private void fetchGuruPostsFromFirebaseHindi(String sortOption) {


        final StorageReference mStorageReferenceVideo = FirebaseStorage.getInstance().getReference("posts").child("videos");

//        DatabaseReference mDatabaseReferencePostsDSGuru = FirebaseDatabase.getInstance().getReference("users").child(GURU_UID_USER).child("userPosts");

        if (mSelectedGurus != null && mSelectedGurus.size() > 0) {

//            mDatabaseReferencePostsDSGuru.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");
//
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                        if (!isDone.containsKey(postSnapshot.getKey())) {
//
//                            if (postSnapshot.child("type").getValue().equals("status")) {
//                                Log.d(TAG, "onDataChange: DATA trooguru");
//                                Status statusSnap = postSnapshot.getValue(Status.class);
//                                statusSnap.setPostUid(postSnapshot.getKey());
//
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//                                items.add(statusSnap);
//
//                                Log.d(TAG, "onDataChange: trooguru " + items + " " + isDone + " " + statusSnap.getStatus());
//                            } else if (postSnapshot.child("type").getValue().equals("photo")) {
//                                Photo photoSnap = postSnapshot.getValue(Photo.class);
//                                photoSnap.setPostUid(postSnapshot.getKey());
//                                photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
//                                isDone.put(postSnapshot.getKey(), (long) 1);
//
//                                items.add(photoSnap);
//                            } else if (postSnapshot.child("type").getValue().equals("video")) {
//                                final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
//                                if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
//                                    videoSnap.setPostUid(postSnapshot.getKey());
//                                    isDone.put(postSnapshot.getKey(), (long) 1);
//                                    items.add(videoSnap);
//                                    videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
//                                    mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            videoSnap.setVideoURI(uri.toString());
//                                        }
//                                    });
//                                }
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
//                }
//            });

            if(mSelectedGurus.contains("KKdovrQ6nla0E6Npx7nEzmcm3Bh2F")){
                mSelectedGurus.add("KKdovrQ6nla0E6Npx7nEzmcm3Bh2");
            }

            for (String guru : mSelectedGurus) {

                mDatabaseReferencePosts = FirebaseDatabase.getInstance().getReference("users").child(guru).child("userPosts");

                mDatabaseReferencePosts.orderByChild(sortOption).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mStorageReference = FirebaseStorage.getInstance().getReference("posts").child("images");

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            if (postSnapshot.child("language").exists() && postSnapshot.child("language").getValue().equals("hindi")) {
                                if (!isDone.containsKey(postSnapshot.getKey())) {

                                    if (postSnapshot.child("type").getValue().equals("status")) {
                                        Log.d(TAG, "onDataChange: DATA troo");
                                        Status statusSnap = postSnapshot.getValue(Status.class);
                                        statusSnap.setPostUid(postSnapshot.getKey());

                                        isDone.put(postSnapshot.getKey(), (long) 1);
                                        items.add(statusSnap);
                                    } else if (postSnapshot.child("type").getValue().equals("photo")) {
                                        Photo photoSnap = postSnapshot.getValue(Photo.class);
                                        photoSnap.setPostUid(postSnapshot.getKey());
                                        photoSnap.setStorageReference(mStorageReference.child(postSnapshot.getKey()));
                                        isDone.put(postSnapshot.getKey(), (long) 1);

                                        items.add(photoSnap);
                                    } else if (postSnapshot.child("type").getValue().equals("video")) {
                                        final CustomVideo videoSnap = postSnapshot.getValue(CustomVideo.class);
                                        if (mStorageReferenceVideo.child(postSnapshot.getKey()) != null) {
                                            videoSnap.setPostUid(postSnapshot.getKey());
                                            isDone.put(postSnapshot.getKey(), (long) 1);
                                            items.add(videoSnap);
                                            videoSnap.setStorageReference(mStorageReferenceVideo.child(postSnapshot.getKey()));
                                            mStorageReferenceVideo.child(postSnapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    videoSnap.setVideoURI(uri.toString());
                                                }
                                            });
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
            }

            Log.d(TAG, "fetchStatusFromFirebase: " + items.size());
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_common, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_language: {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.choose_lang)
                        .items(new String[]{getString(R.string.english_lang), getString(R.string.hindi_lang), getString(R.string.both_lang)})
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                from = getArguments().getString("from");

                                switch (which) {
                                    case 0: {
                                        lang = 0;

                                        isDone.clear();
                                        items.clear();

//                                        mRvHome.getRecycledViewPool().clear();
                                        mAdapter.notifyDataSetChanged();
                                        isDone = new HashMap<String, Long>();
                                        items = new Items();

                                        //FETCH ENGLISH FEEDS
                                        if (from.equals(getString(R.string.title_home))) {
                                            //FETCH ENGLISH FEEDS FOR HOME
                                            fetchGuruPostsFromFirebaseEnglish(sortBy);
                                        } else if (from.equals(getString(R.string.title_explore))) {
                                            //FETCH ENGLISH FEEDS FOR EXPLORE
                                            fetchExplorePostsFromFirebaseEnglish(sortBy);
                                        } else if (from.equals(getString(R.string.title_your_feeds))) {
                                            //FETCH ENGLISH FEEDS FOR YOUR FEEDS
                                            fetchYourPostsFromFirebaseEnglish(sortBy);
                                        }
                                        break;
                                    }
                                    case 1: {
                                        lang = 1;
                                        isDone.clear();
                                        items.clear();

                                        Log.d(TAG, "onSelection: FROM " + from);

//                                        mRvHome.getRecycledViewPool().clear();
                                        mAdapter.notifyDataSetChanged();
                                        isDone = new HashMap<String, Long>();
                                        items = new Items();
                                        //FETCH HINDI FEEDS
                                        if (from.equals(getString(R.string.title_home))) {
                                            //FETCH HINDI FEEDS FOR HOME
                                            fetchGuruPostsFromFirebaseHindi(sortBy);
                                        } else if (from.equals(getString(R.string.title_explore))) {
                                            //FETCH HINDI FEEDS FOR EXPLORE
                                            fetchExplorePostsFromFirebaseHindi(sortBy);
                                        } else if (from.equals(getString(R.string.title_your_feeds))) {
                                            //FETCH HINDI FEEDS FOR YOUR FEEDS
                                            fetchYourPostsFromFirebaseHindi(sortBy);
                                        }
                                        break;
                                    }
                                    case 2: {
                                        lang = 2;
                                        isDone.clear();
                                        items.clear();

//                                        mRvHome.getRecycledViewPool().clear();
                                        mAdapter.notifyDataSetChanged();
                                        isDone = new HashMap<String, Long>();
                                        items = new Items();
                                        //FETCH BOTH LANG FEEDS
                                        if (from.equals(getString(R.string.title_home))) {
                                            //FETCH BOTH LANG FEEDS FOR HOME
                                            fetchGuruPostsFromFirebase(sortBy);
                                        } else if (from.equals(getString(R.string.title_explore))) {
                                            //FETCH BOTH LANG FEEDS FOR EXPLORE
                                            fetchExplorePostsFromFirebase(sortBy);
                                        } else if (from.equals(getString(R.string.title_your_feeds))) {
                                            //FETCH BOTH LANG FEEDS FOR YOUR FEEDS
                                            fetchYourPostsFromFirebase(sortBy);
                                        }
                                        break;
                                    }
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .show();
                break;
            }
            case R.id.about: {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            }
//            case R.id.action_sort:{

//                new MaterialDialog.Builder(getActivity())
//                        .title(R.string.sort_by)
//                        .items(new String[]{getString(R.string.most_recent),getString(R.string.popularity)})
//                        .itemsCallbackSingleChoice(sort, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                                from = getArguments().getString("from");
//
//                                switch (which) {
//                                    case 0: {
//                                        sort = 0;
//
//                                        sortBy = "timestamp";
//
//                                        isDone.clear();
//                                        items.clear();
//
////                                        mRvHome.getRecycledViewPool().clear();
//                                        mAdapter.notifyDataSetChanged();
//                                        isDone = new HashMap<String, Long>();
//                                        items = new Items();
//
//                                        if (from.equals(getString(R.string.title_home))) {
//                                            fetchGuruPostsFromFirebase(sortBy);
//                                        } else if (from.equals(getString(R.string.title_explore))) {
//                                            fetchExplorePostsFromFirebase(sortBy);
//                                        } else if (from.equals(getString(R.string.title_your_feeds))) {
//                                            fetchYourPostsFromFirebase(sortBy);
//                                        }
//                                        break;
//                                    }
//                                    case 1: {
//                                        sort = 1;
//                                        sortBy = "likes";
//                                        isDone.clear();
//                                        items.clear();
//
//                                        Log.d(TAG, "onSelection: FROM " + from);
//
////                                        mRvHome.getRecycledViewPool().clear();
//                                        mAdapter.notifyDataSetChanged();
//                                        isDone = new HashMap<String, Long>();
//                                        items = new Items();
//
//                                        if (from.equals(getString(R.string.title_home))) {
//                                            fetchGuruPostsFromFirebase(sortBy);
//                                        } else if (from.equals(getString(R.string.title_explore))) {
//                                            fetchExplorePostsFromFirebase(sortBy);
//                                        } else if (from.equals(getString(R.string.title_your_feeds))) {
//                                            fetchYourPostsFromFirebase(sortBy);
//                                        }
//                                        break;
//                                    }
//
//                                }
//                                return true;
//                            }
//                        })
//                        .positiveText(R.string.choose)
//                        .show();

//                break;
//            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (customVideoAdapter != null) {
            customVideoAdapter.releaseVideo();
        }
        super.onPause();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
