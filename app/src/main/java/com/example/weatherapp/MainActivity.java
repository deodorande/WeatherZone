package com.example.weatherapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//http://api.openweathermap.org/data/2.5/weather?q=Katowice&lang=PL&units=metric&appid=a71fa7369a61aae850d392b6c8d96807



//tutaj
import android.location.LocationListener;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.Button;
//tutaj


public class MainActivity extends AppCompatActivity {

    //tutaj
    private Button saveButton, loadButton, locButton;
    //private TextView locTextView;
    private LocationManager locManager;
    private LocationListener locListener;
    public static double lat, lon;
    private TextView getCityName;
    public String current_city;
    public String save_city_name = "Warszawa";
    private boolean switcher;
    //tutaj

    TextView selectCity, cityField, detailsField,
            currentTemperatureField, minTempField, maxTempField,
            humidityField, pressureField, windField,
            weatherIcon, dateField;

    ProgressBar loader;
    Typeface weatherFont;
    public static String city = "Katowice";
    public static String city_last = "Katowice";
    String OPEN_WEATHER_MAP_API = "a71fa7369a61aae850d392b6c8d96807";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        loader = (ProgressBar) findViewById(R.id.loader);
        selectCity = (TextView) findViewById(R.id.select_city);
        cityField = (TextView) findViewById(R.id.city_field);
        dateField = (TextView) findViewById(R.id.date_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temp_field);
        minTempField = (TextView) findViewById(R.id.min_temp_field);
        maxTempField = (TextView) findViewById(R.id.max_temp_field);
        windField = (TextView) findViewById(R.id.wind_field);
        humidityField = (TextView) findViewById(R.id.humidity_field);
        pressureField = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);


        //tutaj
        final Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        locButton = (Button) findViewById(R.id.loc_button);
        saveButton = (Button) findViewById(R.id.save_button);
        loadButton = (Button) findViewById(R.id.load_button);

        getCityName = (TextView) findViewById(R.id.cityName);

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = (location.getLatitude());
                lon = (location.getLongitude());

                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                current_city = addresses.get(0).getLocality();
                if(switcher){
                    taskLoadUp(current_city);
                    switcher = false;
                }
                //cityField.setText(current_city);
                //taskLoadUp(current_city);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
        locButton.performClick();
        //tutaj



        //tutaj
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_city_name = cityField.getText().toString();
            }
        });


        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskLoadUp(save_city_name);
            }
        });
        //tutaj



        //taskLoadUp(save_city_name);


        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change city");
                final EditText input = new EditText(MainActivity.this);
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

    }


    //tutaj
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                switcher = true;
                locManager.requestLocationUpdates("gps", 5000, 0, locListener);
            }
        });
    }
    //tutaj



    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.executeGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");

                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.getDefault()) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.getDefault()));
                    currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + " °C");

                    minTempField.setText(String.format("%.2f", main.getDouble("temp_min")) + " °C");
                    maxTempField.setText(String.format("%.2f", main.getDouble("temp_max")) + " °C");

                    windField.setText(wind.getInt("speed")*3.6 + " km/h");

                    humidityField.setText(main.getString("humidity") + "%");
                    pressureField.setText(main.getString("pressure") + " hPa");
                    dateField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000 )));

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                //Toast.makeText(getApplicationContext(), "     Error!\nCheck City", Toast.LENGTH_SHORT).show();
                openCityError();

            }
        }
    }

    public void openCityError(){
        Intent intent = new Intent(this, CityError.class);
        startActivity(intent);
    }
}