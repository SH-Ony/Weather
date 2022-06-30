package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homerl;
    private ProgressBar pbl;
    private TextView citytv,temptv,conditontv;
    private ImageView bgiv,iconiv,searchiv;
    private TextInputEditText cityedt;
    private RecyclerView rvRv;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityname;
    private double latitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        homerl=findViewById(R.id.RLHome);
        pbl=findViewById(R.id.pb);
        citytv=findViewById(R.id.tvcityname);
        temptv=findViewById(R.id.tvtemp);
        conditontv=findViewById(R.id.tvcondition);
        bgiv=findViewById(R.id.ivbg);
        iconiv=findViewById(R.id.ivicon);
        searchiv=findViewById(R.id.ivsearch);
        cityedt=findViewById(R.id.edtcity);
        rvRv=findViewById(R.id.rv);
        weatherRVModelArrayList= new ArrayList<>();
        weatherAdapter= new WeatherAdapter(this,weatherRVModelArrayList);
        rvRv.setAdapter(weatherAdapter);

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if(location!=null){
//            longitude=location.getLongitude();
//            latitude=location.getLatitude();
//        }
//        else{
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, (android.location.LocationListener) this);
//        }
        if(location!=null){
            longitude=location.getLongitude();
            latitude=location.getLatitude();
        }
        else{
            Log.e("error","location is null");
            Toast.makeText(this, "location is null", Toast.LENGTH_SHORT).show();
        }



        cityname=getCityName(longitude,latitude);
        getWeatherInfo(cityname);

        searchiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=cityedt.getText().toString().trim();
                cityedt.setText("");
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city Name", Toast.LENGTH_SHORT).show();
                }else {
                    citytv.setText(cityname);
                    getWeatherInfo(city);
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude)
    {
        String cityname= "Not Found";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        int i=0;

        try {
            List<Address> addresses= gcd.getFromLocation(latitude,longitude,10);
            for (Address adr: addresses)
            {
                i++;
                if(adr!=null){

                    String city=adr.getLocality();

                    if(city!=null&&!city.equals("")){
                        cityname=city;
                        //Toast.makeText(this, city, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d("tag","city not found");
                        //Toast.makeText(this, "User city not found"+i, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return  cityname;
    }

    private void getWeatherInfo(String cityname) {
        String url="http://api.weatherapi.com/v1/forecast.json?key=894ccd7d65774eaab3f222701220704&q="+cityname+"&days=1&aqi=yes&alerts=yes";
        citytv.setText(cityname);
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbl.setVisibility(View.GONE);
                homerl.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();
                try {
                    String temp=response.getJSONObject("current").getString("temp_c");
                    temptv.setText(temp+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    if(isDay==0){
                        //night
                        Picasso.get().load("https://media.idownloadblog.com/wp-content/uploads/2020/09/Night-sky-wallpaper-iPhone-iDownloadBlog-IEDITWALLS-moon-camping.jpg").into(bgiv);
                    }
                    else{//morning
                        Picasso.get().load("https://i.pinimg.com/originals/62/5b/1e/625b1e48551d310209d80ab1835824ea.jpg").into(bgiv);
                    }
                    String s=String.valueOf(isDay);

                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    Toast.makeText(MainActivity.this, condition, Toast.LENGTH_LONG).show();
                    String conditionicon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionicon)).into(iconiv);
                    conditontv.setText(condition);


                    JSONObject forecastObj= response.getJSONObject("forecast");
                    JSONObject forecast0=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forecast0.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++)
                    {
                        JSONObject hourobj= hourArray.getJSONObject(i);
                        String time=hourobj.getString("time");
                        String temp2=hourobj.getString("temp_c");
                        String img=hourobj.getJSONObject("condition").getString("icon");
                        String windspd=hourobj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temp2,img,windspd));
                    }
                    weatherAdapter.notifyDataSetChanged();




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid city name", Toast.LENGTH_SHORT).show();
                Log.e("error2", String.valueOf(error));
            }
        }
        );

        queue.add(jsonObjectRequest);

    }

}