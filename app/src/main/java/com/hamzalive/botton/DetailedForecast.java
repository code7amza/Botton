package com.hamzalive.botton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailedForecast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailedForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_forecast, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if (id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_map) {
            return openSettingsLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean openSettingsLocation()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String city = sp.getString(getString(R.string.preference_location_key)
                , getString(R.string.preference_location_default));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setData(Uri.parse("geo:0,0?q="+city)); // equivalent to
        intent.setData(Uri.parse("geo:0,0").buildUpon().appendQueryParameter("q", city).build());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        return true;
    }

    public static class DetailedForecastFragment extends Fragment {
        public static final String HASHTAG = "#BottonApp #weather";
        public String mForecastDetail;

        private ShareActionProvider mShareActionProvider;

        public DetailedForecastFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.share_detailed_forecast, menu);

            MenuItem item = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if(mShareActionProvider != null)
                mShareActionProvider.setShareIntent(GetWeatherForecastToShare());
            else
                Log.w(DetailedForecastFragment.class.getSimpleName(), "Could not get ActionProvider");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView =  inflater.inflate(R.layout.fragment_detailed_forecast, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                TextView tv = (TextView) rootView.findViewById(R.id.detailed_forecast_text);
                mForecastDetail = intent.getStringExtra(Intent.EXTRA_TEXT);
                tv.setText(mForecastDetail);
            }

            return rootView;
        }

        public Intent GetWeatherForecastToShare()
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mForecastDetail + HASHTAG);
            sendIntent.setType("text/plain");
            return sendIntent;
        }


        private void setShareIntent(Intent shareIntent) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

}
