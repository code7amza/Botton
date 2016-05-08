package com.hamzalive.botton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    public static ArrayAdapter<String> ADAPTER;
    public static ArrayList<String> FORECAST;
    public ForecastFragment() {
    }

    private void updateWeatherForecast()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String city = sp.getString(getString(R.string.preference_location_key)
                , getString(R.string.preference_location_default));
        String units = sp.getString(getString(R.string.preference_units_key)
                , getString(R.string.preference_units_default));

        new WeatherModel().GetWeatherFromCity(city, units);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        updateWeatherForecast();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FORECAST = new WeatherModel().GetWeatherSample();

        ADAPTER = new ArrayAdapter<String>( getActivity() // current context
                                        , R.layout.list_item_forecast  // layout of the list items
                                        , R.id.list_item_forecast_textview // id of the text view
                                        , FORECAST); // raw data

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView;
        listView = (ListView) rootView.findViewById(R.id.ListViewForecast);
        listView.setAdapter(ADAPTER);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(getActivity().getApplicationContext()
//                        , "Clicked on Forecast :  " + ForecastFragment.FORECAST.get(position)
//                        , Toast.LENGTH_SHORT).show();

                Intent detail = new Intent(getActivity(), DetailedForecast.class);
                detail.putExtra(Intent.EXTRA_TEXT, ForecastFragment.FORECAST.get(position));
                startActivity(detail);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeatherForecast();
            return true;
        } else if ( id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
