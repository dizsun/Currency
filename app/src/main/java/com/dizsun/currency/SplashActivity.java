package com.dizsun.currency;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;

public class SplashActivity extends Activity {
    //获取货币汇率的api网址
    public static final String URL_CODES="http://openexchangerates.org/api/currencies.json";
    public static final String NAME = "key_arraylist";
    private ArrayList<String> mCurrencies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        new FetchCodesTask().execute(URL_CODES);
    }

    private class FetchCodesTask extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            return new JSONParser().getJSONFromUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                if(jsonObject==null){
                    throw new JSONException("没有可用的数据！");
                }
                Iterator iterator = jsonObject.keys();
                String key="";
                mCurrencies=new ArrayList<String>();
                while (iterator.hasNext()){
                    key=(String)iterator.next();
                    mCurrencies.add(key+"|"+jsonObject.getString(key));
                }
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainIntent.putExtra(NAME,mCurrencies);
                startActivity(mainIntent);
                finish();
            }
            catch (JSONException e){
                Toast.makeText(SplashActivity.this,"发生了一个JSON异常:"+e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
                finish();
            }
        }
    }

}
