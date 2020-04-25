package com.example.mealplanner;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeActivity extends AppCompatActivity {

    MenuItem starBtn;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String recipeId, name, ingredients, instructions;
    private AlertDialog alertDialog;
    private String selected_day, selected_month, selected_year;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    private SimpleDateFormat mealsFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView mRecipeName = findViewById(R.id.recipe_name_textview);
        TextView mIngredients = findViewById(R.id.ingredients_textview);
        TextView mInstructions = findViewById(R.id.instructions_textview);

        Intent i = getIntent();
        recipeId = i.getStringExtra("recipeId");
        name = i.getStringExtra("recipeName");
        ingredients = i.getStringExtra("ingredients");
        instructions = i.getStringExtra("instructions");

        mRecipeName.setText(name);
        mIngredients.setText(ingredients);
        mInstructions.setText(instructions);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void removeStar(){
        final DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> recipe_ids = new ArrayList<>(Arrays.asList(documentSnapshot.getString("starred").split(",")));
                recipe_ids.remove(recipeId);
                String new_ids;
                if(recipe_ids.size() == 0){
                    new_ids = "";
                }else{
                    new_ids = TextUtils.join(",",recipe_ids);
                }
                documentReference.update("starred",new_ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        starBtn.setTitle("Add Star");
                    }
                });
            }
        });
    }

    private void addStar(){
        final DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String r_ids = documentSnapshot.getString("starred");
                String new_ids;
                if(r_ids == ""){
                    new_ids = recipeId;
                } else {
                    List<String> recipe_ids = new ArrayList<>(Arrays.asList(documentSnapshot.getString("starred").split(",")));
                    recipe_ids.add(recipeId);
                    new_ids = TextUtils.join(",",recipe_ids);
                }
                documentReference.update("starred",new_ids).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        starBtn.setTitle("Remove Star");
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
        popupMenu.inflate(R.menu.recipe_menu);
        Menu temp = popupMenu.getMenu();
        starBtn = temp.findItem(R.id.starMenuItem);
        if(user != null){
            DocumentReference documentReference = fStore.collection("users").document(user.getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    List<String> recipe_ids = Arrays.asList(documentSnapshot.getString("starred").split(","));
                    if(recipe_ids.contains(recipeId)){
                        starBtn.setTitle("Remove Star");
                    }else{
                        starBtn.setTitle("Add Star");
                    }
                }
            });
        }else{
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.starMenuItem:
                        if(starBtn.getTitle().charAt(0) == 'R'){
                            removeStar();
                        }else{
                            addStar();
                        }
                        return true;
                    case R.id.addMenuItem:
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                        builder.setCancelable(true);
                        final View addView = LayoutInflater.from(RecipeActivity.this).inflate(R.layout.recipe_add_meal,null);

                        final EditText notes_et = addView.findViewById(R.id.notes);
                        Button addMealBtn = addView.findViewById(R.id.add_meal_button);
                        final DatePicker datePicker = addView.findViewById(R.id.datePicker);
                        final Calendar today = Calendar.getInstance(Locale.ENGLISH);
                        selected_day = mealsFormat.format(today.getTime());
                        selected_month = monthFormat.format(today.getTime());
                        selected_year = yearFormat.format(today.getTime());
                        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(year,month,dayOfMonth);
                                selected_day = mealsFormat.format(c.getTime());
                                selected_month = monthFormat.format(c.getTime());
                                selected_year = yearFormat.format(c.getTime());
                            }
                        });
                        addMealBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String notes = notes_et.getText().toString();
                                saveMealPlan(recipeId,name,selected_day,selected_month,selected_year,notes);
                                alertDialog.dismiss();
                        }
                        });

                        builder.setView(addView);
                        alertDialog = builder.create();
                        alertDialog.show();

                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void saveMealPlan(String id, String name, String day, String month, String year, String notes){
        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        CollectionReference mealPlanCollection = fStore.collection("mealPlans");
        Map<String,Object> data = new HashMap<>();
        data.put("day",day);
        data.put("month",month);
        data.put("year",year);
        data.put("notes",notes);
        data.put("recipeId",id);
        data.put("recipeName",name);
        data.put("uid",user.getUid());
        mealPlanCollection.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }
        });
    }
}
