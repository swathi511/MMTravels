package com.hjsoft.mmtravels.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.SessionManager;
import com.hjsoft.mmtravels.adapters.DBAdapter;
import com.hjsoft.mmtravels.adapters.DrawerItemCustomAdapter;
import com.hjsoft.mmtravels.adapters.RecyclerAdapter;
import com.hjsoft.mmtravels.fragments.ShowDutiesFragment;
import com.hjsoft.mmtravels.model.DutyData;
import com.hjsoft.mmtravels.model.NavigationData;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 28/2/17.
 */
public class HomeActivity extends AppCompatActivity implements RecyclerAdapter.AdapterCallback{

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter adapter;
    API REST_CLIENT;
    SessionManager session;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        REST_CLIENT= RestClient.get();

        session=new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        NavigationData[] drawerItem = new NavigationData[2];

        drawerItem[0] = new NavigationData(R.drawable.arrow, "All Duties");
       // drawerItem[1] = new NavigationData(R.drawable.arrow, "Your Rides");
        //drawerItem[2] = new NavigationData(R.drawable.arrow, "Your Profile");
       // drawerItem[2] = new NavigationData(R.drawable.arrow,"Rate Card");
        drawerItem[1] = new NavigationData(R.drawable.arrow,"Logout");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setupDrawerToggle();

        Fragment fragment=new ShowDutiesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment,"all_duties").commit();
        setTitle("All Duties");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;
        adapter.setSelectedItem(position);

        switch (position) {
            case 0:
                // fragment = new TrackCabsFragment();
                break;
            case 1:
                // System.out.println("doing nothing.....");
                logoutUser();


                break;
           /* case 2:
                Intent k=new Intent(this,ProfileActivity.class);
                startActivity(k);
                finish();
                break;*/
            case 2:

                break;
            case 3:

                break;

            default:
                break;
        }

        if (fragment != null) {

            openFragment(fragment,position);

        } else {
            // Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void openFragment(Fragment fragment,int position){

        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (containerFragment.getClass().getName().equalsIgnoreCase(fragment.getClass().getName())) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        else{
            /*
           FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            */
            mDrawerLayout.closeDrawer(mDrawerList);

        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
        //  getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#000000\">" +mTitle + "</font>")));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        /*
        int titleId = getResources().getIdentifier("toolbar", "id", "android");
        TextView abTitle = (TextView) findViewById(titleId);
        abTitle.setTextColor(Color.parseColor("#000000"));*/
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMethodCallback(final int position, ArrayList<DutyData> data,boolean status) {

        DutyData val=data.get(position);

        DBAdapter dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        if(!status) {

            if (dbAdapter.findDSNo(val.getDslipid())) {

                Intent i = new Intent(HomeActivity.this, TrackRideOngoing.class);
                i.putExtra("position", position);
                i.putExtra("list", data);
                startActivity(i);
                finish();
            }
            else {

                Intent i = new Intent(HomeActivity.this, TrackRideActivity.class);
                i.putExtra("position", position);
                i.putExtra("list", data);
                startActivity(i);
                finish();
            }
        }
        else {

            if(dbAdapter.isDSNOpresent(val.getDslipid())) {

                Intent i = new Intent(HomeActivity.this, OfflineActivity.class);
                i.putExtra("position", position);
                i.putExtra("list", data);
                startActivity(i);
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
       /* switch (item.getItemId())
        {
            case R.id.search:  // it is going to refer the search id name in main.xml

                //add your code here

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/

        if(item.getItemId()==R.id.refresh)
        {
            Intent i=new Intent(HomeActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser()
    {
        Log.i("HA","PROFILEID is"+user.get(SessionManager.KEY_ID));
        JsonObject v=new JsonObject();
        v.addProperty("profileid",user.get(SessionManager.KEY_ID));
        Call<Pojo> call=REST_CLIENT.logoutUser(v);

        call.enqueue(new Callback<Pojo>() {
            @Override
            public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                if(response.isSuccessful())
                {
                    SessionManager s=new SessionManager(getApplicationContext());
                    s.logoutUser();
                    Toast.makeText(HomeActivity.this,"Successfully Logged out!",Toast.LENGTH_SHORT).show();
                    Intent l=new Intent(HomeActivity.this,LoginActivity.class);
                    l.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    l.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(l);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Pojo> call, Throwable t) {

                Toast.makeText(HomeActivity.this,"No Internet Connection!\nPlease try again.",Toast.LENGTH_SHORT).show();

            }
        });
    }


}
