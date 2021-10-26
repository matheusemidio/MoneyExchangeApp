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

    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if(id == R.id.btnConvert)
        {
            requestApi();
        }
    }

    //Validation
    private void validate_value()
    {
        try
        {
            baseValue = Double.parseDouble(edConvertingValueInput.getText().toString());
            if(baseValue <= 0)
            {
                throw new Exception("Value input has to be bigger than zero.");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Please, enter a correct input for the value.", Toast.LENGTH_LONG).show();
        }
    }

    private void validate_targetCode()
    {
        try
        {
            targetCode = edTargetCodeInput.getText().toString();
            //Validate Regex for 3 chars and only string
            //Check if target code exists
//            if()
//            {
//                throw new Exception("Target code can only have three chars.");
//                return "";
//            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Please, enter a correct input for the target code.", Toast.LENGTH_LONG).show();
        }
    }

    private void validate_baseCode()
    {
        try
        {
            baseCode = edBaseCodeInput.getText().toString();
            //Validate Regex for 3 chars and only string
            //Check if base code exists
//            if()
//            {
//                throw new Exception("Target code can only have three chars.");
//                return "";
//            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Please, enter a correct input for the base code.", Toast.LENGTH_LONG).show();
        }
    }
    public double convertValue(double rate, double baseValue)
    {
        return (rate * baseValue);
    }

    private void requestApi()
    {
        try
        {
            validate_baseCode();
            validate_targetCode();
            validate_value();

            if(baseCode.equals("") || targetCode.equals("") || baseValue == 0)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/3808ca223ddc4c6c54c4d05b/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iExchangeRate iExchangeRate = retrofit.create(iExchangeRate.class);
        //Call<JsonObject> call = iExchangeRate.getConversionRates("USD");
        Call<JsonObject> call = iExchangeRate.getConversionRates(baseCode);

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
                    JsonObject conversion_rates = json.getAsJsonObject("conversion_rates");
                    double rate = conversion_rates.get("BRL").getAsDouble();
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
    }

}