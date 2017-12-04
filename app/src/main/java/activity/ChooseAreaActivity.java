package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chimaeraqm.simweather.R;

import java.util.ArrayList;
import java.util.List;

import db.SimWeatherDB;
import model.City;
import model.County;
import model.Province;
import util.HttpCallListener;
import util.HttpUtil;
import util.Utility;

/**
 * Created by Administrator on 2017/11/23.
 */

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressBar;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private SimWeatherDB simWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("city_selected",false))
        {
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        simWeatherDB = SimWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(currentLevel == LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY)
                {
                    selectedCity = cityList.get(i);
                    queryCounties();
                }
                else if(currentLevel == LEVEL_COUNTY)
                {
                    String countyCode = countyList.get(i).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces()
    {
        provinceList = simWeatherDB.loadProvinces();
        if(provinceList.size() > 0)
        {
            dataList.clear();
            for(Province province : provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("China");
            currentLevel = LEVEL_PROVINCE;
        }
        else
        {
            queryFromServer(null,"province");
        }
    }

    private void queryCities()
    {
        cityList = simWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size() > 0)
        {
            dataList.clear();
            for(City city : cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
        else
        {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties()
    {
        countyList = simWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size() > 0)
        {
            dataList.clear();
            for(County county : countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
        else
        {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code,final String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        else
        {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressBar();
        HttpUtil.sendHttpRequest(address, new HttpCallListener()
        {
            @Override
            public void onFinish(String response)
            {
                boolean result = false;
                if("province".equals(type))
                {
                    result = Utility.handleProvincesResponse(simWeatherDB,response);
                }
                else if ("city".equals(type))
                {
                    result = Utility.handleCitiesResponse(simWeatherDB,response,selectedProvince.getId());
                }
                else if ("county".equals(type))
                {
                    result = Utility.handleCountiesResponse(simWeatherDB,response,selectedCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            closeProgressBar();
                            if("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if("city".equals(type))
                            {
                                queryCities();
                            }
                            else if("county".equals(type))
                            {
                                queryCounties();
                            }
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
                    public void run() {
                        closeProgressBar();
                        Toast.makeText(ChooseAreaActivity.this,"Load Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void showProgressBar()
    {
        if(progressBar == null)
        {
            progressBar = new ProgressDialog(this);
            progressBar.setMessage("On Progress...");
            progressBar.setCanceledOnTouchOutside(false);
        }
        progressBar.show();
    }

    private void closeProgressBar()
    {
        if(progressBar != null)
        {
            progressBar.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currentLevel == LEVEL_COUNTY)
        {
            queryCities();
        }
        else if(currentLevel == LEVEL_CITY)
        {
            queryProvinces();
        }
        if(currentLevel == LEVEL_PROVINCE)
        {
            finish();
        }
    }
}
