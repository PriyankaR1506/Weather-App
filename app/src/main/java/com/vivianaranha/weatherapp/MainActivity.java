package com.vivianaranha.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        Downloader downloader = new Downloader();
        downloader.execute();


    }

    public class MyAdapter extends BaseAdapter {


        ArrayList<ArrayList<Results>> results;
        Context context;

        public MyAdapter(Context context, ArrayList<ArrayList<Results>> results){
            this.context = context;
            this.results = results;
        }

        @Override
        public int getCount() {
            int count = results.get(0).size() + results.get(1).size() + 2;
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.single_row, parent, false);
            TextView tv = (TextView) row.findViewById(R.id.titleField);

            int secondSection = results.get(0).size() + 1;
            if(position == 0){
                tv.setText("Hourly");
                row.setMinimumHeight(10);
                tv.setHeight(20);
                tv.setTextColor(Color.WHITE);
                row.setBackgroundColor(Color.BLACK);
            } else if(position == secondSection){
                tv.setText("Daily");
            } else {
                Results result;
                if(position < secondSection) {
                    result = results.get(0).get(position - 1);
                } else {
                    int location = position - secondSection -1;
                    result = results.get(1).get(location);
                }

                tv.setText("Temperature " + result.temp);
            }

            return row;
        }
    }



    public class Results {
        String temp;
        public  Results(String temperature){
            this.temp = temperature;
        }

    }


    public class Downloader extends AsyncTask<Void, Void, ArrayList> {

        String weatherURL = "http://vivianaranha.com/data.json";
        ArrayList<ArrayList<Results>> group = new ArrayList<ArrayList<Results>>(2);


        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            MyAdapter adapter = new MyAdapter(MainActivity.this, arrayList);
            listView.setAdapter(adapter);


        }

        @Override
        protected ArrayList doInBackground(Void... params) {

            URL theUrl = null;
            try {
                theUrl = new URL(weatherURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openConnection().getInputStream(), "UTF-8"));
                String json = reader.readLine();

                JSONObject jsonObject = new JSONObject(json);
                JSONObject hourlyObject = jsonObject.getJSONObject("hourly");
                JSONArray hourlyArray = hourlyObject.getJSONArray("data");

                ArrayList<Results> hourly = new ArrayList<>();
                for (int i = 0; i < hourlyArray.length(); i++) {
                    JSONObject singleObject = hourlyArray.getJSONObject(i);
                    String temperature = singleObject.getString("temperature");
                    Results result = new Results(temperature);
                    hourly.add(result);
                }

                group.add(hourly);

                JSONObject dailyObject = jsonObject.getJSONObject("daily");
                JSONArray dailyArray = dailyObject.getJSONArray("data");

                ArrayList<Results> daily = new ArrayList<>();
                for (int i = 0; i < dailyArray.length(); i++) {
                    JSONObject singleObject = dailyArray.getJSONObject(i);
                    String temperature = singleObject.getString("temperatureMax");
                    Results result = new Results(temperature);
                    daily.add(result);
                }

                group.add(daily);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return group;
        }
    }
}
