package com.example.mealplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchFragment extends Fragment {

    private List<Recipe> recipeList = new ArrayList<>();
    private String searchQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search,container,false);
        getActivity().setTitle("Search");

        // fill list from API
        try {
            new RecipeTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        final RecyclerView recyclerView = v.findViewById(R.id.search_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),recipeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        final EditText editText = v.findViewById(R.id.searchText);
        ImageView imageView = v.findViewById(R.id.searchButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeList.clear();
                searchQuery = encodeValue(editText.getText().toString());
                try {
                    new SearchTask().execute().get();
                }catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                RecyclerViewAdapter recyclerViewAdapter1 = new RecyclerViewAdapter(getContext(),recipeList);
                recyclerView.setAdapter(recyclerViewAdapter1);
            }
        });
        return v;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return "";
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

    private void addRecipesToList(JSONArray jsonArray){
        try{
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
        }catch (JSONException e){
            Log.e("Error",e.toString());
            e.printStackTrace();
        }
    }
    private class SearchTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?number=20&offset=0&query="+searchQuery)
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "9a61e45873mshe1327d3335539b4p164aa2jsn65b46c3bc77e")
                        .build();
                Response response = client.newCall(request).execute();
                JSONObject results = new JSONObject(response.body().string());
                JSONArray resultsArray = results.getJSONArray("results");
                String idlist="";
                for(int i = 0; i < resultsArray.length();i++){
                    idlist += Integer.toString(resultsArray.getJSONObject(i).getInt("id"));
                    if(i!=resultsArray.length()-1){ idlist += ","; }
                }
                idlist = encodeValue(idlist);
                request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/informationBulk?ids="+idlist)
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "9a61e45873mshe1327d3335539b4p164aa2jsn65b46c3bc77e")
                        .build();
                response = client.newCall(request).execute();
                JSONArray jsonArray  = new JSONArray(response.body().string());
                addRecipesToList(jsonArray);
            }catch (IOException | JSONException e) {
                Log.e("Search Error",e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }

    private class RecipeTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number=10")
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "9a61e45873mshe1327d3335539b4p164aa2jsn65b46c3bc77e")
                        .build();
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                addRecipesToList(jsonArray);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
