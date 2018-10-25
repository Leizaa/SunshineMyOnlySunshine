package com.example.yudi.sunshine;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private ArrayList<ListObject> objects;

    private TextView date;
    private TextView max;
    private TextView min;
    private TextView stat;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(this);
        objects = new ArrayList<>();

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                jsonParser();
            }
        });

//        jsonParser();

        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.ListView);

        date = findViewById(R.id.textView17);
        max = findViewById(R.id.textView15);
        min = findViewById(R.id.textView_bottomMain);
        stat = findViewById(R.id.textView18);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Sunshine");

        MyAdapter adapterKu = new MyAdapter();
        listview.setAdapter(adapterKu);

        objects.add(new ListObject("ha","ha","ha","ha"));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }

    private void jsonParser(){

        //isi link
        String url = "http://api.openweathermap.org/data/2.5/forecast?q=jakarta&APPID=2dc9d6485ba2a280c09d1fa8253ea868&units=metric&cnt=14";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //disini nanti isiin apa yang terjadi kalau sukses
                try {
                    JSONArray jsonArray = response.getJSONArray("list");

                    JSONObject first = jsonArray.getJSONObject(0);
                    long dateTemp = first.getLong("dt");

                    JSONObject temperature = first.getJSONObject("main");
                    double tempMax = temperature.getDouble("temp_max");
                    double tempMin = temperature.getDouble("temp_min");

                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String a = df.format(dateTemp * 1000L);

                    date.setText(a);
                    max.setText(tempMax + " C");
                    min.setText(tempMin + " C");

                    JSONObject statusJson = first.getJSONArray("weather").getJSONObject(0);
                    String status = statusJson.getString("main");
                    stat.setText(status);

                    objects.add(new ListObject(a,status,tempMax+"",tempMin+""));

                    for(int i = 1;i < 14;i++){
                        JSONObject dateObject = jsonArray.getJSONObject(i);
                        JSONObject tempObject = dateObject.getJSONObject("main");
                        JSONObject weatObject = dateObject.getJSONArray("weather").getJSONObject(0);

                        dateTemp = dateObject.getLong("dt");
                        tempMax = tempObject.getDouble("temp_max");
                        tempMin = tempObject.getDouble("temp_min");
                        status = weatObject.getString("main");

                        objects.add(new ListObject(getDate(dateTemp),status,
                                        tempMax + "", + tempMin + ""));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private String getDate(long time){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        time = time * 1000L;
        String date = df.format(time);
        return date;
    }

    class ListObject {
        String date;
        String status;
        String degreeOne;
        String degreeTwo;

        private ListObject(String date, String status, String degreeOne, String degreeTwo){
            this.date = date;
            this.status = status;
            this.degreeOne = degreeOne;
            this.degreeTwo = degreeTwo;
        }
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int i) {
            return objects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlistview,null);

            ImageView imageView = view.findViewById(R.id.imageView);
            TextView dateTextview = view.findViewById(R.id.textView_date);
            TextView maxDegreeTextview = view.findViewById(R.id.textView_degreeOne);
            TextView minDegreeTextview = view.findViewById(R.id.textView_degreeTwo);
            TextView statusTextview = view.findViewById(R.id.textView_status);

            dateTextview.setText(objects.get(i).date);
            maxDegreeTextview.setText(objects.get(i).degreeOne + " C");
            minDegreeTextview.setText(objects.get(i).degreeTwo + " C");
            statusTextview.setText(objects.get(i).status);

            return view;
        }
    }

}
