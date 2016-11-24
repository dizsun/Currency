package com.dizsun.currency;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //定义layout上的view
    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForSpinner, mHomSpinner;

    private String[] mCurrencies;
    public static final String FOR = "FOR_CURRENCY";
    public static final String HOM = "HOM_CURRENCY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //将货币ArrayList解压并转化为数组
        ArrayList<String> arrayList =
                (ArrayList<String>) getIntent().getSerializableExtra(SplashActivity.NAME);
        Collections.sort(arrayList);
        mCurrencies = arrayList.toArray(new String[arrayList.size()]);

        //空间初始化
        mConvertedTextView = (TextView) findViewById(R.id.txt_converted);
        mAmountEditText = (EditText) findViewById(R.id.edt_amount);
        mCalcButton = (Button) findViewById(R.id.btn_calc);
        mForSpinner = (Spinner) findViewById(R.id.spn_for);
        mHomSpinner = (Spinner) findViewById(R.id.spn_hom);

        //List的适配器
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_closed, mCurrencies);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHomSpinner.setAdapter(arrayAdapter);
        mForSpinner.setAdapter(arrayAdapter);
        //将监听器加到MainActivity上
        mHomSpinner.setOnItemSelectedListener(this);
        mForSpinner.setOnItemSelectedListener(this);

        //设置默认偏好
        if(savedInstanceState==null&&
                (PrefsMgr.getString(this,FOR)==null&&PrefsMgr.getString(this,HOM)==null)){
            mForSpinner.setSelection(findPositionGivenCode("USD",mCurrencies));
            mHomSpinner.setSelection(findPositionGivenCode("CNY",mCurrencies));
            PrefsMgr.setString(this,FOR,"USD");
            PrefsMgr.setString(this,HOM,"CNY");
        }else {
            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,FOR),mCurrencies));
            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,HOM),mCurrencies));
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) return true;
        return false;
    }

    private void launchBrowser(String strUri) {
        if (isOnline()) {
            Uri uri = Uri.parse(strUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void invertCurrencies() {
        int nFor = mForSpinner.getSelectedItemPosition();
        int nHom = mHomSpinner.getSelectedItemPosition();
        mForSpinner.setSelection(nHom);
        mHomSpinner.setSelection(nFor);
        mConvertedTextView.setText("");
        PrefsMgr.setString(this,FOR,extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
        PrefsMgr.setString(this,HOM,extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
    }

    /**
     * 根据给的货币代码查找在spinner上的位置
     *
     * @param code       给定的货币代码
     * @param currencies 货币聚合字符串数组
     * @return 在spinner上的位置，默认为第一位
     */
    private int findPositionGivenCode(String code, String[] currencies) {
        for (int i = 0; i < currencies.length; i++) {
            if (extractCodeFromCurrency(currencies[i]).equalsIgnoreCase(code)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 从聚合字符串中抽取货币代码
     *
     * @param currency 聚合字符串
     * @return 货币代码
     */
    private String extractCodeFromCurrency(String currency) {
        return (currency).substring(0, 3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 当spinner的选项被选中时
     *
     * @param item 被选中的选项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mun_invert:
                invertCurrencies();
                break;
            case R.id.mun_codes:
                launchBrowser(SplashActivity.URL_CODES);
                break;
            case R.id.mun_exit:
                finish();
                break;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spn_for:
                PrefsMgr.setString(this,FOR,extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
                break;
            case R.id.spn_hom:
                PrefsMgr.setString(this,HOM,extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
                break;
            default:
                break;
        }
        mConvertedTextView.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
