package com.orestislef.techblogproject;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView NewsRecyclerview;
    RecyclerViewPostAdapter recyclerViewPostAdapter;
    List<PostItem> mData;
    boolean isDark = false;
    ConstraintLayout rootLayout;
    EditText searchInput;
    CharSequence search = "";

    public int postsPerPage = 30;
    public int category = 0;
    private static final String TAG = "MainActivity";
    public String imageUrl;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // let's make this activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // hide the action bar

//        getSupportActionBar().hide();


        // ini view

//        rootLayout = findViewById(R.id.root_layout);
        drawer = findViewById(R.id.drawer_layout);
        searchInput = findViewById(R.id.search_input);
        NewsRecyclerview = findViewById(R.id.news_rv);
        mData = new ArrayList<>();

        // load theme state

        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        drawer.findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        getData();


        // adapter ini and setup

        recyclerViewPostAdapter = new RecyclerViewPostAdapter(this, mData, isDark);
        NewsRecyclerview.setAdapter(recyclerViewPostAdapter);
        NewsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerViewPostAdapter.getFilter().filter(s);
                search = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d(TAG, "onNavigationItemSelected: selected: " + menuItem);
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                category = 0;
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_smartphones:
                category = 8;
                Toast.makeText(this, "smartphone", Toast.LENGTH_SHORT).show();
                break;
        }
        mData.clear();
        getData();

        drawer.closeDrawer(GravityCompat.START);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    public void getData() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources()
                .getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayApi service = retrofit.create(RetrofitArrayApi.class);
        Call<List<WPPost>> call;
        if (category == 0) {
            call = service.getPostPerPage(postsPerPage);
        } else {
            call = service.getPostByCategory(category);
        }

        call.enqueue(new Callback<List<WPPost>>() {
            @Override
            public void onResponse(Call<List<WPPost>> call, Response<List<WPPost>> response) {
                Log.e(TAG, "onResponse: " + response.body());

                for (int i = 0; i < response.body().size(); i++) {
                    int mId = response.body().get(i).getId();
                    Log.d(TAG, "onResponseID: " + mId);
                    final String mediaUrl = response.body().get(i).getLinks().getWpAttachment().get(0).getHref();
                    final String mTitle = response.body().get(i).getTitle().getRendered();
                    String mSubtitle = response.body().get(i).getExcerpt().getRendered();

                    mSubtitle = mSubtitle.replace("<p>", "");
                    mSubtitle = mSubtitle.replace("</p>", "");
                    mSubtitle = mSubtitle.replace("[&hellip;]", "");

                    final String finalMContent = response.body().get(i).getContent().getRendered();

                    final String mDate = response.body().get(i).getDate();

                    Log.d(TAG, "onResponse: "
                            + "\n================================================================================================================================================================================================================================================"
                            + "\nid: \t\t" + mId
                            + "\nTitle: \t\t" + mTitle
                            + "\nSubtitle: \t" + mSubtitle
                            + "\nContent: \t\t" + finalMContent
                            + "\n================================================================================================================================================================================================================================================");

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(getResources().getString(R.string.base_url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitArrayApi service2 = retrofit2.create(RetrofitArrayApi.class);

                    Call<List<WPPostID>> call2 = service2.getWpAttachment(mediaUrl);
                    Log.d(TAG, "getRetrofitImageMediaUrl: " + mediaUrl);
                    final String finalMSubtitle = mSubtitle;
                    call2.enqueue(new Callback<List<WPPostID>>() {
                                      @Override
                                      public void onResponse(Call<List<WPPostID>> call, Response<List<WPPostID>> response) {

                                          Log.e(TAG, "onResponse: " + response.body());

                                          if (response.body().size() != 0) {
                                              imageUrl = response.body().get(0).getMediaDetails().getSizes().getThumbnail().getSourceUrl();
//                                              imageUrl = response.body().get(0).getSourceUrl();
                                              Log.d(TAG, "onResponseImage: " + "\n******************************************************************************************" + "\n\t with media " + imageUrl + "\n******************************************************************************************");
                                          } else {
                                              imageUrl = "";
                                              Log.d(TAG, "onResponseImage: " + "\n******************************************************************************************" + "\n\t null media\n" + imageUrl + "\n******************************************************************************************");
                                          }
                                          mData.add(new PostItem(mTitle, finalMSubtitle, finalMContent, mDate, imageUrl));
                                          recyclerViewPostAdapter.notifyDataSetChanged();
                                      }

                                      @Override
                                      public void onFailure(Call<List<WPPostID>> call, Throwable t) {
                                      }
                                  }
                    );
                }

            }

            @Override
            public void onFailure(Call<List<WPPost>> call, Throwable t) {
            }
        });
    }
}
