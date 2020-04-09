package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        char frag = getIntent().getCharExtra("frag",'0');
        fragmentManager = getSupportFragmentManager();
        switch(frag){
            case '1':
                fragmentManager.beginTransaction().add(R.id.container_fragment,new SearchFragment()).commit();
                break;
            case '2':
                fragmentManager.beginTransaction().add(R.id.container_fragment,new StarredFragment()).commit();
                break;
            case '3':
                fragmentManager.beginTransaction().add(R.id.container_fragment,new InventoryFragment()).commit();
                break;
            case '4':
                fragmentManager.beginTransaction().add(R.id.container_fragment,new SettingsFragment()).commit();
                break;
            default:
                fragmentManager.beginTransaction().add(R.id.container_fragment,new HomeFragment()).commit();
        }
    }

    public void logout(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentManager = getSupportFragmentManager();
        switch(item.getItemId()){
            case R.id.search:
                fragmentManager.beginTransaction().replace(R.id.container_fragment,new SearchFragment()).commit();
                break;
            case R.id.starred:
                fragmentManager.beginTransaction().replace(R.id.container_fragment,new StarredFragment()).commit();
                break;
            case R.id.inventory:
                fragmentManager.beginTransaction().replace(R.id.container_fragment,new InventoryFragment()).commit();
                break;
            case R.id.settings:
                fragmentManager.beginTransaction().replace(R.id.container_fragment,new SettingsFragment()).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.container_fragment,new HomeFragment()).commit();
        }
        return true;
    }

    public void changePassword(View v){
        startActivity(new Intent(getApplicationContext(),ChangePassword.class));
    }

    public void deactivate(View v){

    }
}
