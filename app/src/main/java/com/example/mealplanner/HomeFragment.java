package com.example.mealplanner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ImageButton prevBtn, nextBtn;
    private TextView currDate;
    private GridView gridView;
    private static final int MAX_CAL_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridViewAdapter gridViewAdapter;

    List<Date> dates = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        getActivity().setTitle("MealPlanner");

        context = getContext();
        prevBtn = v.findViewById(R.id.calendar_prev_button);
        nextBtn = v.findViewById(R.id.calendar_next_button);
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
        while(dates.size() < MAX_CAL_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }

        gridViewAdapter = new GridViewAdapter(context,dates,calendar);
        gridView.setAdapter(gridViewAdapter);
    }
}
