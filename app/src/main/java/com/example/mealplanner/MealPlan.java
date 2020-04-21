package com.example.mealplanner;

class MealPlan {
    private String recipeId;
    private String recipeName;
    private String day;
    private String month;
    private String year;
    private String notes;


    public MealPlan(String recipeId, String recipeName, String day, String month, String year, String notes) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.day = day;
        this.month = month;
        this.year = year;
        this.notes = notes;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getNotes() {
        return notes;
    }
}
