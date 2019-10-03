package com.example.demofirebasecloudmessaging.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demofirebasecloudmessaging.OnLoadMoreListener;
import com.example.demofirebasecloudmessaging.R;
import com.example.demofirebasecloudmessaging.adapter.MyRecyclerViewAdapter;
import com.example.demofirebasecloudmessaging.models.HeroProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import static com.example.demofirebasecloudmessaging.constants.UrlConstants.BASE_URL;

/** for infinite loading data in side recycler view
 * step 1 : create  an interface with abstract method  in my case see OnLoadMoreListener (Interface)
 * step 2 : create  separate xml files for loading xml and dataholder xml
 *          (i): create progress bar xml -> see loading.xml
 *          (ii): create dataholder xml -> see hero_profile_holder.xml
 * step 3: add recycler view in  xml file of main_activity.xml
 * step 4 : see MyRecyclerViewAdapter.java
 * */
public class MainActivity extends AppCompatActivity {
    ArrayList<HeroProfile> heroProfiles;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
private SwipeRefreshLayout swipeRefreshLayout;
DatabaseReference db;
public static final String TAG = "main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseDatabase.getInstance().getReference(BASE_URL);
        swipeRefreshLayout = findViewById(R.id.swape_up);
        heroProfiles = new ArrayList<>();
        setData();
        recyclerView = findViewById(R.id.main_rv);
        GridLayoutManager gridLayoutManager =new GridLayoutManager(this,1 );
        recyclerView.setLayoutManager(gridLayoutManager);
        // passing recycler view object to the recyclerview adapter
        adapter = new MyRecyclerViewAdapter(recyclerView,heroProfiles, this);
        recyclerView.setAdapter(adapter);
        setScrollListener(adapter);
        swapeDown(adapter);

        //set more listener for the RecyclerView adapter
        /**A handler is basically a message queue.
         * You post a message to it, and it will eventually process it by
         * calling its run method and passing the message to it.
         * Since these run calls will always occur in
         * the order of messages received on the same thread,
         * it allows you to serialize events.*/
    }

    private void setScrollListener(final MyRecyclerViewAdapter adapter) {
        adapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMoreData() {
                if (heroProfiles.size()<=20){
                    heroProfiles.add(null);
                    adapter.notifyItemInserted(heroProfiles.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            heroProfiles.remove(heroProfiles.size()-1);
                            adapter.notifyItemRemoved(heroProfiles.size());
                            int index = heroProfiles.size();
                            int end = index+10;
                            for (int i =index;i<end;i++){
                                dataset(i);
                            }
                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        }
                    },3000);
                }else {
                    Toast.makeText(MainActivity.this, "Loading completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void swapeDown(final MyRecyclerViewAdapter adapter) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            adapter.clear();
            setData();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            adapter.setProgressBarInfinite(recyclerView);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setData() {
        for (int i = 0 ;i<10;i++){
            dataset(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent i = new Intent(this,AddProfile.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    public void dataset(int i){
        Log.d(TAG, "dataset: "+ db);
        db.child("heroProfiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            //    Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        HeroProfile heroProfile = new HeroProfile("name "+ i);
        heroProfiles.add(heroProfile);
    }
}
