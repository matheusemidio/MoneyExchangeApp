package com.example.prjmoneyexchange_1931358;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prjmoneyexchange_1931358.Models.iExchangeRate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvConvertedValueResult;
    EditText edBaseCodeInput,edConvertingValueInput, edTargetCodeInput;
    Button btnConvert;

    String baseCode = "", targetCode = "";
    double baseValue = 0;
    double targetValue = 0;

    JsonObject conversion_rates;
    JsonObject currencies_list;
    double rate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize()
    {
        tvConvertedValueResult = (TextView) findViewById(R.id.tvConvertedValueResult);
        edBaseCodeInput = (EditText) findViewById(R.id.edBaseCodeInput);
        edConvertingValueInput = (EditText) findViewById(R.id.edConvertingValueInput);
        edTargetCodeInput = (EditText) findViewById(R.id.edTargetCodeInput);
        btnConvert = findViewById(R.id.btnConvert);
        btnConvert.setOnClickListener(this);

        //This will make a request to the API using a hardcoded base code. The reason for this call is only for checking if
        //the baseCode and targetCode inputted by the user exists.
        //currencies_list = baseRequestApi();
        baseRequestApi();
    }
    private boolean checkCurrencyExistance(String targetCurrency)
    {
        Boolean test = false;
        try
        {
            //This line will check if the targetCurrency is a key on the list of currencies
            test = currencies_list.has(targetCurrency);
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
        return test;
    }

    //Validation
    private void validate_value() throws Exception
    {
        try
        {
            baseValue = Double.parseDouble(edConvertingValueInput.getText().toString());
            if(String.valueOf(baseValue).equals(null))
            {
                throw new Exception("Value can not be empty");
            }
            if(baseValue <= 0)
            {
                throw new Exception("Value input has to be bigger than zero.");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void validate_targetCode() throws Exception
    {
        try
        {
            targetCode = edTargetCodeInput.getText().toString().toUpperCase();
            //Validate Regex for 3 chars and only string
            //Check if target code exists
            if(targetCode.equals(null))
            {
                throw new Exception("Target code can not be empty.");
            }
            else if(checkCurrencyExistance(targetCode) == false)
            {
                throw new Exception("Target code does not exist");
            }
//            if()
//            {
//                throw new Exception("Target code can only have three chars.");
//            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }


    private void validate_baseCode() throws Exception
    {
        try
        {
            baseCode = edBaseCodeInput.getText().toString().toUpperCase();
            //Validate Regex for 3 chars and only string
            //Check if base code exists
            if(baseCode.equals(null))
            {
                throw new Exception("Base code can not be empty.");
            }
            else if(checkCurrencyExistance(baseCode) == false)
            {
                throw new Exception("Target code does not exist");
            }
//            if()
//            {
//                throw new Exception("Base code can only have three chars.");
//            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public double convertValue(double rate, double baseValue)
    {
        return (rate * baseValue);
    }

    //private JsonObject baseRequestApi()
    private void baseRequestApi()
    {
        //This function is to make a request with a manual hardcoded currency to receive the list of all the currencies and check if the user input really exists
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/3808ca223ddc4c6c54c4d05b/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iExchangeRate iExchangeRate = retrofit.create(iExchangeRate.class);

        //Hardcoded base code to get list of currencies
        Call<JsonObject> call = iExchangeRate.getConversionRates("USD");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(response.code() != 200)
                {
                    tvConvertedValueResult.setText("Code: " + response.code());
                    return;
                }

                //Was successful
                String jsonString = new Gson().toJson(response.body());
                Log.i("Response Body", jsonString);

                //Getting the JSON response and converting it to an object

                //If the JSON returns you an array of objects [], you use JsonArray instead of JsonObject
                JsonParser parser = new JsonParser();
                JsonObject json = (JsonObject) parser.parse(jsonString);

                //Getting access to a specific part of the object
                try
                {
                    //List of currencies
                    currencies_list = json.getAsJsonObject("conversion_rates");
                }
                catch (Exception e)
                {
                    Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.i("Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tvConvertedValueResult.setText(t.getMessage());
            }
        });

//        if(currencies_list.isJsonNull())
//        {
//            return null;
//        }
//        return currencies_list;

    }

    //private JsonObject requestApiConversion(String code)
    private void requestApiConversion(String code)
    {
        //This function is to really get the rates for the inputted currencies

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/3808ca223ddc4c6c54c4d05b/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iExchangeRate iExchangeRate = retrofit.create(iExchangeRate.class);

        Call<JsonObject> call = iExchangeRate.getConversionRates(code);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(response.code() != 200)
                {
                    tvConvertedValueResult.setText("Code: " + response.code());
                    return;
                }

                //Was successful
                String jsonString = new Gson().toJson(response.body());
                Log.i("Response Body", jsonString);

                //Getting the JSON response and converting it to an object

                //If the JSON returns you an array of objects [], you use JsonArray instead of JsonObject
                JsonParser parser = new JsonParser();
                JsonObject json = (JsonObject) parser.parse(jsonString);

                //Getting access to a specific part of the object
                try
                {
                    //Now im inside conversion_rates
                    //JsonObject conversion_rates = json.getAsJsonObject("conversion_rates");
                    conversion_rates = json.getAsJsonObject("conversion_rates");
                    //double rate = conversion_rates.get("BRL").getAsDouble();
                    //rate = conversion_rates.get(baseCode).getAsDouble();
                    //double targetValue = convertValue(baseValue, rate);
                    //tvConvertedValueResult.setText(String.valueOf(targetValue) + " " + targetCode);

                    //double rate = conversion_rates.get("BRL").getAsDouble();
                    rate = conversion_rates.get(targetCode).getAsDouble();
                    //rate = test_rates.get(targetCode).getAsDouble();
                    targetValue = convertValue(baseValue, rate);
                    tvConvertedValueResult.setText(String.valueOf(targetValue) + " " + targetCode);
                }
                catch (Exception e)
                {
                    Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.i("Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tvConvertedValueResult.setText(t.getMessage());
            }
        });
//        if(conversion_rates.isJsonNull())
//        {
//            return null;
//        }
//        return conversion_rates;
    }
    @Override
    public void onClick(View view)
    {
        //double rate = conversion_rates.get("BRL").getAsDouble();
        //double targetValue = convertValue(baseValue, rate);
        //tvConvertedValueResult.setText(String.valueOf(targetValue) + " " + targetCode);
        int id = view.getId();
        if(id == R.id.btnConvert)
        {
            try
            {
                JsonObject test_rates;
                //Validate and fill the base code class variable
                validate_baseCode();
                //Validate and fill the target code class variable
                validate_targetCode();
                //Validate and fill the base value class variable
                validate_value();

                //test_rates = requestApiConversion(baseCode);
                requestApiConversion(baseCode);

            }
            catch (Exception e)
            {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

}