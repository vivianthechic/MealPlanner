package com.example.mealplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchFragment extends Fragment {

    List<Recipe> recipeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search,container,false);
        // turn toolbar into search bar
        getActivity().setTitle("Search");

        // fill list from API
        try {
            new RecipeTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = v.findViewById(R.id.search_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),recipeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }


    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class RecipeTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number=5")
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "9a61e45873mshe1327d3335539b4p164aa2jsn65b46c3bc77e")
                        .build();
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                for(int i = 0; i < jsonArray.length();i++){
                    JSONObject recipe = jsonArray.getJSONObject(i);

                    int id = recipe.getInt("id");
                    String name = recipe.getString("title");
                    Bitmap image = getBitmapFromURL(recipe.getString("image"));
                    JSONArray ingredientsArray = recipe.getJSONArray("extendedIngredients");
                    String ingredients = "";
                    for(int j = 0; j<ingredientsArray.length();j++){
                        ingredients += ingredientsArray.getJSONObject(j).getString("original")+"\n";
                    }
                    JSONArray stepsArray = recipe.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
                    String instructions = "";
                    for(int j = 0; j < stepsArray.length(); j++){
                        instructions += Integer.toString(j + 1)+". "+stepsArray.getJSONObject(j).getString("step")+"\n\n";
                    }

                    recipeList.add(new Recipe(id,name,ingredients,instructions,image));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
