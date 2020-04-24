package com.example.mealplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FirebaseUser user;
    private AlertDialog alertDialog;
    private TextView currDate;
    private GridView gridView;
    private static final int MAX_CAL_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private Context context;

    private RecyclerView showMealsRecycler;
    private List<Date> dates = new ArrayList<>();
    private List<MealPlan> mealplan = new ArrayList<>();
    private List<String> starred_ids = new ArrayList<>();
    private List<String> starred_names = new ArrayList<>();
    private String selected_recipe_id, selected_day, selected_month, selected_year;
    private ArrayAdapter<String> adapter;
    private GridViewAdapter gridViewAdapter;
    private MealRecyclerAdapter mealRecyclerAdapter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    private SimpleDateFormat mealsFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        getActivity().setTitle("MealPlanner");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            getActivity().finish();
            startActivity(new Intent(getContext(),Login.class));
        }
        context = getContext();
        ImageButton prevBtn = v.findViewById(R.id.calendar_prev_button);
        ImageButton nextBtn = v.findViewById(R.id.calendar_next_button);
        currDate = v.findViewById(R.id.currDate_textview);
        gridView = v.findViewById(R.id.calendar_grid);
        setUpCalendar();

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                setUpCalendar();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                setUpCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                String day = mealsFormat.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_show_meals,null);
                showMealsRecycler = showView.findViewById(R.id.mealplan_recyclerview);
                showMealsRecycler.setLayoutManager(new LinearLayoutManager(showView.getContext()));
                showMealsRecycler.setHasFixedSize(true);
                List<MealPlan> dayPlan = collectMealsPerDay(day);
                mealRecyclerAdapter = new MealRecyclerAdapter(showView.getContext(),dayPlan);
                showMealsRecycler.setAdapter(mealRecyclerAdapter);

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });
            }
        });

        Button showToday = v.findViewById(R.id.showToday_button);
        Button showMonth = v.findViewById(R.id.showMonth_button);
        Button addNew = v.findViewById(R.id.addNew_button);

        showToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = mealsFormat.format(Calendar.getInstance(Locale.ENGLISH).getTime());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(context).inflate(R.layout.calendar_show_meals,null);
                showMealsRecycler = showView.findViewById(R.id.mealplan_recyclerview);
                showMealsRecycler.setLayoutManager(new LinearLayoutManager(showView.getContext()));
                showMealsRecycler.setHasFixedSize(true);
                List<MealPlan> dayPlan = collectMealsPerDay(day);
                mealRecyclerAdapter = new MealRecyclerAdapter(showView.getContext(),dayPlan);
                showMealsRecycler.setAdapter(mealRecyclerAdapter);

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });
            }
        });

        showMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(context).inflate(R.layout.calendar_show_meals,null);
                showMealsRecycler = showView.findViewById(R.id.mealplan_recyclerview);
                showMealsRecycler.setLayoutManager(new LinearLayoutManager(showView.getContext()));
                showMealsRecycler.setHasFixedSize(true);
                collectMealsPerMonth(monthFormat.format(calendar.getTime()),yearFormat.format(calendar.getTime()));
                mealRecyclerAdapter = new MealRecyclerAdapter(showView.getContext(),mealplan);
                showMealsRecycler.setAdapter(mealRecyclerAdapter);

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });

            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_recipe_id = "0";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(context).inflate(R.layout.calendar_add_meal,null);
                Spinner spinner = addView.findViewById(R.id.recipe_spinner);
                adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,starred_names);
                populateStarred();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_recipe = (String) parent.getItemAtPosition(position);
                        selected_recipe_id = starred_ids.get(starred_names.indexOf(selected_recipe));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

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
                });                addMealBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selected_recipe_id.equals("0")){
                            Toast.makeText(context,"Please select a recipe",Toast.LENGTH_SHORT).show();
                        }else{
                            String notes = notes_et.getText().toString();
                            saveMealPlan(selected_recipe_id,starred_names.get(starred_ids.indexOf(selected_recipe_id)),selected_day,selected_month,selected_year,notes);
                            setUpCalendar();
                            alertDialog.dismiss();
                        }
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return v;
    }

    private void setUpCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        currDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        collectMealsPerMonth(monthFormat.format(calendar.getTime()),yearFormat.format(calendar.getTime()));

        while(dates.size() < MAX_CAL_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }

        gridViewAdapter = new GridViewAdapter(context, dates, calendar,mealplan);
        gridView.setAdapter(gridViewAdapter);
    }

    private void populateStarred(){
        starred_names.clear();
        starred_ids.clear();
        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference userDoc = fStore.collection("users").document(user.getUid());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.getString("starred").equals("")){
                    List<String> temp = new ArrayList<>(Arrays.asList(documentSnapshot.getString("starred").split(",")));
                    for(int i = 0; i < temp.size();i++){
                        starred_ids.add(temp.get(i));
                        DocumentReference recipeDoc = fStore.collection("recipes").document(temp.get(i));
                        recipeDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String name = documentSnapshot.getString("title");
                                starred_names.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
        starred_names.add(0,"Please select from starred recipes");
        starred_ids.add(0,"0");
        adapter.notifyDataSetChanged();
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

    private void collectMealsPerMonth(final String querymonth, final String queryyear){
        mealplan.clear();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        CollectionReference mealPlanCollection = fStore.collection("mealPlans");
        mealPlanCollection.whereEqualTo("month",querymonth).whereEqualTo("year",queryyear).whereEqualTo("uid",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < docs.size(); i++){
                    DocumentSnapshot doc = docs.get(i);
                    String id = doc.getString("recipeId");
                    String name = doc.getString("recipeName");
                    String day = doc.getString("day");
                    String notes = doc.getString("notes");
                    MealPlan mp = new MealPlan(id,name,day,querymonth,queryyear,notes);
                    mealplan.add(mp);
                    Collections.sort(mealplan);
                    gridViewAdapter.notifyDataSetChanged();
                    if(mealRecyclerAdapter != null){
                        mealRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private List<MealPlan> collectMealsPerDay(final String date){
        final List<MealPlan> list = new ArrayList<>();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        CollectionReference mealPlanCollection = fStore.collection("mealPlans");
        mealPlanCollection.whereEqualTo("day",date).whereEqualTo("uid",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < docs.size(); i++) {
                    DocumentSnapshot doc = docs.get(i);
                    String id = doc.getString("recipeId");
                    String name = doc.getString("recipeName");
                    String month = doc.getString("month");
                    String year = doc.getString("year");
                    String notes = doc.getString("notes");
                    MealPlan mp = new MealPlan(id, name, date, month, year, notes);
                    list.add(mp);
                    mealRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        return list;
    }
}
