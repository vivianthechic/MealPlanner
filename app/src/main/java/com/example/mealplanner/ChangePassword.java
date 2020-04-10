package com.example.mealplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Settings");
        }

        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        final EditText current_pw = findViewById(R.id.cp_currentpassword);
        final EditText new_pw = findViewById(R.id.cp_newpassword);
        final EditText confirm_pw = findViewById(R.id.cp_confirmpassword);
        Button cp_btn = findViewById(R.id.cp_button);
        final ProgressBar progressBar = findViewById(R.id.cp_progressBar);

        cp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPW = current_pw.getText().toString();
                final String newPW = new_pw.getText().toString();
                String confirmPW = confirm_pw.getText().toString();

                if(currentPW.isEmpty()){
                    current_pw.setError("Authentication is required.");
                    return;
                }
                if(newPW.length() < 6){
                    new_pw.setError("New password must be >= 6 characters.");
                    return;
                }
                if(!confirmPW.equals(newPW)){
                    confirm_pw.setError("Does not match new password.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                final FirebaseUser user = fAuth.getCurrentUser();
                if(user != null && user.getEmail() != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPW);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPW).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ChangePassword.this,"Password has been changed.", Toast.LENGTH_SHORT).show();
                                            fAuth.signOut();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(),Login.class));
                                        }else{
                                            Toast.makeText(ChangePassword.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(ChangePassword.this,"Error: Authentication failed.",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else{
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.putExtra("frag",'4');
        startActivityForResult(i, 0);
        return true;
    }
}
