package com.example.mealplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    private String email;
    private String password;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }else {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    email = documentSnapshot.getString("email");
                    password = documentSnapshot.getString("password");
                }
            });
        }

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
        finish();
        startActivity(new Intent(getApplicationContext(),Login.class));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Are you sure you want to deactivate your account?");
        builder.setMessage("Deactivating your account will delete all of your data.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    final String uid = user.getUid();
                    AuthCredential credential = EmailAuthProvider.getCredential(email,password);
                    user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseFirestore.getInstance().collection("users").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "onSuccess: user profile deleted for "+uid);
                                            FirebaseFirestore.getInstance().collection("mealPlans").whereEqualTo("uid",uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                                                    for(int i = 0; i < docs.size(); i++){
                                                        docs.get(i).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                            }
                                                        });
                                                    }
                                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(),"Account deactivated.",Toast.LENGTH_SHORT).show();
                                                                finish();
                                                                startActivity(new Intent(getApplicationContext(),Login.class));
                                                            }else{
                                                                Toast.makeText(getApplicationContext(),"Account could not be deactivated.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                            });
                        }
                    });
                }else{
                    finish();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            }
        });
        builder.show();
    }
}

