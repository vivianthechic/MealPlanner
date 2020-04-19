package com.example.mealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridViewAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    LayoutInflater layoutInflater;

    public GridViewAdapter(@NonNull Context context, List<Date> dates,Calendar currentDate) {
        super(context, R.layout.calendar_cell);
        this.dates = dates;
        this.currentDate = currentDate;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNum = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currMonth = currentDate.get(Calendar.MONTH) + 1;
        int currYear = currentDate.get(Calendar.YEAR);

        View v = convertView;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.calendar_cell, parent, false);
        }

        Calendar today = Calendar.getInstance(Locale.ENGLISH);
        if (displayMonth == today.get(Calendar.MONTH)+1 && displayYear == today.get(Calendar.YEAR) && dayNum == today.get(Calendar.DATE)){
            v.setBackgroundColor((getContext().getResources().getColor(R.color.p_red)));
        } else if(displayMonth == currMonth && displayYear == currYear){
            v.setBackgroundColor(getContext().getResources().getColor(R.color.neutral));
        }else{
            v.setBackgroundColor(getContext().getResources().getColor(R.color.light_gray));
        }

        TextView dayNumber = v.findViewById(R.id.calendar_day);
        dayNumber.setText(String.valueOf(dayNum));

        return v;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
