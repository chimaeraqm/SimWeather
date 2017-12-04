package activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chimaeraqm.simweather.R;

import util.HttpCallListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;

    private TextView cityNameText;

    private TextView publishText;

    private TextView weatherDespText;

    private TextView temp1Text;

    private TextView temp2Text;

    private TextView currentDateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        /**
         * initial every interface in the layout
         */
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_data);
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode))
        {
            publishText.setText("Synchronizing...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }
        else
        {
            showWeather();
        }
    }

    /**
     * query weather code responsed to county code
     */
    private void queryWeatherCode(String countyCode)
    {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    /**
     * query specific weather responsed to weather code
     */
    private void queryWeatherInfo(String weatherCode)
    {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    /**
     * Query weather code and info in webserve according to input address and type
     */
    private void queryFromServer(final String address,final String type)
    {
        HttpUtil.sendHttpRequest(address, new HttpCallListener()
        {
            @Override
            public void onFinish(String response)
            {
                if("countyCode".equals(type))
                {
                    String[] array = response.split("\\|");
                    if(array != null && array.length == 2)
                    {
                        String weatherCode = array[1];
                        queryWeatherInfo(weatherCode);
                    }
                } else if ("weatherCode".equals(type))
                {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        publishText.setText("Synchronized failed!");
                    }
                });
            }
        });
    }

    /**
     * Read stored weather info from SharedPreferences file, and display it.
     */
    private void showWeather()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name",""));
        temp1Text.setText(sharedPreferences.getString("temp1",""));
        temp2Text.setText(sharedPreferences.getString("temp2",""));
        weatherDespText.setText(sharedPreferences.getString("weather_desp",""));
        publishText.setText("Today" + sharedPreferences.getString("publish_time","") + "Released");
        currentDateText.setText(sharedPreferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
