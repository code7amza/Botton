package com.hamzalive.botton;

import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hamza on 4/30/16.
 */


public class WeatherModel {
    public class FetchWeatherTask extends AsyncTask<URL, Void, ArrayList<String> >
    {
        private final String sLogTag = FetchWeatherTask.class.getSimpleName();
        @Override
        protected ArrayList<String> doInBackground(URL ... url) {
            String currentWeatherJson = null;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            ArrayList<String> res = new ArrayList<>();
            try {
                // connect

                connection = (HttpURLConnection) url[0].openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                // read output
                InputStream requestResponse = connection.getInputStream();
                if (requestResponse == null)
                    return res;
                reader = new BufferedReader(new InputStreamReader(requestResponse));

                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (0 == buffer.length())
                    return res;

                currentWeatherJson = buffer.toString();
            } catch (IOException e) {
                Log.e(sLogTag, "Error", e);
            } finally{
                if (connection != null)
                    connection.disconnect();
                if(reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(sLogTag, "Error closing stream", e);
                    }
                }
            }
            //Log.i(sLogTag, "Got weather data : "+currentWeatherJson);
            return getWeeklyForecastFromJson(currentWeatherJson);
        }

        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            if (result.isEmpty()) {
                Log.w(sLogTag, "No weather data fetched");
            } else {
                Log.i(sLogTag, "Fetching Weather info complete");
                // update the array and notify
                ForecastFragment.FORECAST.clear();
                ForecastFragment.FORECAST.addAll(result);
                ForecastFragment.ADAPTER.notifyDataSetChanged();

                // OR Update the adapter directly
                //ForecastFragment.ADAPTER.clear();
                //ForecastFragment.ADAPTER.addAll(result);
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }


    public static String sJsonExample = "{\"city\":{\"id\":2988507,\"name\":\"Paris\",\"coord\":{\"lon\":2.3488,\"lat\":48.853409},\"country\":\"FR\",\"population\":0},\"cod\":\"200\",\"message\":0.0088,\"cnt\":7,\"list\":[{\"dt\":1462014000,\"temp\":{\"day\":8.1,\"min\":3.14,\"max\":8.24,\"night\":3.14,\"eve\":8.24,\"morn\":8.1},\"pressure\":1022.65,\"humidity\":93,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":5.11,\"deg\":357,\"clouds\":88,\"rain\":0.46},{\"dt\":1462100400,\"temp\":{\"day\":12.38,\"min\":2.61,\"max\":13.4,\"night\":3.99,\"eve\":11.99,\"morn\":2.61},\"pressure\":1030.61,\"humidity\":75,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":5.21,\"deg\":3,\"clouds\":0},{\"dt\":1462186800,\"temp\":{\"day\":14.63,\"min\":3.32,\"max\":15.71,\"night\":8.56,\"eve\":14.54,\"morn\":3.32},\"pressure\":1027.77,\"humidity\":79,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"02d\"}],\"speed\":2.31,\"deg\":265,\"clouds\":8},{\"dt\":1462273200,\"temp\":{\"day\":13.53,\"min\":5.07,\"max\":13.53,\"night\":5.07,\"eve\":12.37,\"morn\":9.71},\"pressure\":1027.54,\"humidity\":80,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.9,\"deg\":338,\"clouds\":12,\"rain\":0.88},{\"dt\":1462359600,\"temp\":{\"day\":14.9,\"min\":4.86,\"max\":15.91,\"night\":7.67,\"eve\":14.69,\"morn\":4.86},\"pressure\":1030.01,\"humidity\":76,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":3.81,\"deg\":104,\"clouds\":0},{\"dt\":1462446000,\"temp\":{\"day\":19.16,\"min\":8.24,\"max\":21.3,\"night\":17.23,\"eve\":21.3,\"morn\":8.24},\"pressure\":1019.83,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":4.27,\"deg\":154,\"clouds\":0},{\"dt\":1462532400,\"temp\":{\"day\":24.48,\"min\":17.15,\"max\":24.48,\"night\":20.41,\"eve\":23.88,\"morn\":17.15},\"pressure\":1012.22,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":6.36,\"deg\":192,\"clouds\":74,\"rain\":0.26}]}";
    public static String sRequestURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Paris,fr&mode=json&units=metric&cnt=7";
    public static ArrayList<String> sDefaultResponse = new ArrayList<String>(Arrays.asList(new String[] {"Could not forecast"}));
    public static String sLogTag = "WeatherModel";

    public static ArrayList<String> GetInfoFromJson(String json)
    {
        ArrayList<String> res = new ArrayList<>();
        res.add("Today - Sunny");
        res.add("Tomorrow - Cold");
        res.add("Monday - Sunny");
        res.add("Tuesday - Rainy");
        res.add("Thursday - Windy");
        res.add("Friday - Sunny");
        return res;
    }

    public ArrayList<String> GetWeatherSample()
    {
        return getWeeklyForecastFromJson(sJsonExample);
    }

    private void GetWeatherFromUrl(String url)
    {
        try{
            new FetchWeatherTask().execute(new URL(url));

        }catch(Exception e) {
            Log.e(sLogTag, "Connection issue");
        }
    }

    public void GetWeatherCurrent()
    {
        GetWeatherFromUrl(WeatherModel.sRequestURL);
    }

    public void GetWeatherFromCity(String city)
    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.openweathermap.org")
                .appendPath("data").appendPath("2.5").appendPath("forecast").appendPath("daily")
                .appendQueryParameter("q",city)
                .appendQueryParameter("units", "metrics").appendQueryParameter("mode", "json")
                .appendQueryParameter("cnt", "7");

        String resUrl = builder.build().toString();
        // Log.i(sLogTag, "Generated url : " + resUrl);
        GetWeatherFromUrl(resUrl);
    }

    public static ArrayList<String> getWeeklyForecastFromJson(String jsonStr)
    {
        ArrayList<String> res = new ArrayList<>();
        try
        {
            if(jsonStr == null){
                Log.w(sLogTag, "Invoked with empty");
                return res;
            }
            //Log.i(sLogTag, "in the beginning there was light with " + jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            JSONArray the_list = json.getJSONArray("list");

            Calendar cal = GregorianCalendar.getInstance();

            for(int idx = 0; idx < 7; ++idx){
                JSONObject element = the_list.getJSONObject(idx);
                JSONObject temp = element.getJSONObject("temp");
                long the_max = Math.round(temp.getDouble("max"));
                long the_min = Math.round(temp.getDouble("min"));
                JSONObject weather = element.getJSONArray("weather").getJSONObject(0);
                String the_main = weather.getString("main");

                long dt = element.getLong("dt");
                cal.setTimeInMillis(dt*1000);

                String day_of_week = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE);
                day_of_week = day_of_week.substring(0, 1).toUpperCase() + day_of_week.substring(1, 3);
                String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.FRANCE);
                month = month.substring(0, 1).toUpperCase() + month.substring(1, 3);

                String the_day = String.format("%s, %s %s"  , day_of_week
                                                            , month
                                                            , cal.get(Calendar.DAY_OF_MONTH));

                res.add(String.format("%s - %s - %d/%d", the_day, the_main, the_max, the_min));
            }
        } catch(final JSONException e){
            Log.e(sLogTag, "Error parsing JSON" + e);
        }
        return res;
    }
}
