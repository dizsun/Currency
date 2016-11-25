package com.dizsun.currency;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by sundiz on 16/11/25.
 */

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mActivity;
    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForSpinner, mHomSpinner;



    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ArrayList<String> bogusCurrencies=new ArrayList<String>();
        bogusCurrencies.add("USD|United States Dollar");
        bogusCurrencies.add("EUR|Euro");
        Intent intent=new Intent();
        intent.putExtra(SplashActivity.NAME,bogusCurrencies);
        setActivityIntent(intent);

        mActivity =getActivity();
        mCalcButton=(Button) mActivity.findViewById(R.id.btn_calc);
        mConvertedTextView=(TextView)mActivity.findViewById(R.id.txt_converted);
        mAmountEditText=(EditText)mActivity.findViewById(R.id.edt_amount);
        mForSpinner=(Spinner)mActivity.findViewById(R.id.spn_for);
        mHomSpinner=(Spinner)mActivity.findViewById(R.id.spn_hom);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void proxyCurrencyConverterTask(final String str)throws Throwable{
        final CountDownLatch latch=new CountDownLatch(1);
        mActivity.setCurrencyTaskCallback(new MainActivity.CurrencyTaskCallback() {
            @Override
            public void executionDone() {
                latch.countDown();
                assertEquals(convertToDouble(mConvertedTextView.getText().toString().substring(0,5)),convertToDouble(str));
            }
        });
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAmountEditText.setText(str);
                mForSpinner.setSelection(0);
                mHomSpinner.setSelection(0);
                mCalcButton.performClick();
            }
        });
        latch.await(30, TimeUnit.SECONDS);
    }

    public void testInteger() throws Throwable {
        proxyCurrencyConverterTask("12");
    }

    public void testFloat() throws Throwable {
        proxyCurrencyConverterTask("12..3");

    }

    private double convertToDouble(String str)throws NumberFormatException {
        double dReturn =0;
        try{
            dReturn=Double.parseDouble(str);
        }catch (NumberFormatException e){
            throw e;
        }
        return dReturn;

    }
}
