package com.example.kareem.spotthatfire;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.kareem.spotthatfire.Connection.Request;
import com.example.kareem.spotthatfire.Connection.ServerConnection;
import com.example.kareem.spotthatfire.Connection.VolleyRequest;
import com.example.kareem.spotthatfire.Model.WeatherData;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationTrackerFragment {

    private SwipeRefreshLayout swipe;
    private TextView tempTextView;
    private TextView locationTextView;
    private TextView cloudTextView;
    private TextView windTextView;
    private TextView humidityTextView;
    private TextView rainTextView;
    private Button reportButton;
    private Location mLastKnownLocation;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setUi(view);
        return view;
    }
    private void setUi(View view)
    {
         swipe = view.findViewById(R.id.swipe);
         tempTextView = view.findViewById(R.id.temp_textView);
         locationTextView = view.findViewById(R.id.location_textView);
         cloudTextView = view.findViewById(R.id.cloud_textView);
         windTextView = view.findViewById(R.id.wind_speed_textView);
         humidityTextView = view.findViewById(R.id.humidity_textView);
         rainTextView = view.findViewById(R.id.rain_textView);
         reportButton = view.findViewById(R.id.report_button);

         setListeners();
    }

    private void setListeners() {

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                swipe.setRefreshing(false);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Fire Reported", Toast.LENGTH_SHORT).show();
                ServerConnection.getInstance().sendRequest(new Request(Consts.REPORT_FIRE, "_"));
            }
        });
    }

    private void updateData()
    {
        if (tempTextView == null) return;
        // check for lat
        if (mLastKnownLocation == null)
        {
            Toast.makeText(getActivity(), "Waiting for location", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String > params = new HashMap<>();
        params.put("lat", mLastKnownLocation.getLatitude() + "" );
        params.put("lon", mLastKnownLocation.getLongitude() + "" );
        params.put("APPID", Config.API_KEY);
        VolleyRequest volleyRequest = new VolleyRequest(Consts.BASE_URL
                + "?lat=" + mLastKnownLocation.getLatitude()
                + "&lon=" + mLastKnownLocation.getLongitude()
                + "&APPID=" + Config.API_KEY
                , params, getActivity()) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {
                Log.e("HomeFragment", "onResponse: " + response );
                WeatherData weatherData = new Gson().fromJson(response, WeatherData.class);
                tempTextView.setText(String.valueOf(weatherData.getMain().getTemp() - 273.15 + " \u2103"));
                locationTextView.setText(String.valueOf(weatherData.getName()));
                cloudTextView.setText(String.valueOf(weatherData.getWeather().get(0).getDescription()));
                windTextView.setText(String.valueOf(weatherData.getWind().getSpeed() + " m/s"));
                humidityTextView.setText(String.valueOf(weatherData.getMain().getHumidity() + "%"));
                rainTextView.setText(String.valueOf(weatherData.getRain().get3h() + " m\u2073"));
                ServerConnection.getInstance().sendRequest(
                        new Request(Consts.UPLOAD_DATA, new Gson().toJson(weatherData)));
            }

        };
        volleyRequest.start();
    }

    @Override
    public void onLocationChange(Location location) {
        if (mLastKnownLocation == null)
        {
//            ServerConnection.getInstance().sendRequest(new Request());
            mLastKnownLocation = location;
            updateData();
        }
        else mLastKnownLocation = location;
    }
}
