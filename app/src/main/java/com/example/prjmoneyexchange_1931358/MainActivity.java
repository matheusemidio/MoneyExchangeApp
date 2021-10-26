package com.example.prjmoneyexchange_1931358;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/3808ca223ddc4c6c54c4d05b/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        iExchangeRate iExchangeRate = retrofit.create(iExchangeRate.class);
        //Call<List<Exchange>> call = jsonPlaceHolderApi.getConversionRates(edBaseCodeInput.toString());
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
                    //Now im inside conversion_rates
                    JsonObject conversion_rates = json.getAsJsonObject("conversion_rates");
                    double target_value = conversion_rates.get("BRL").getAsDouble();
                    tvConvertedValueResult.setText(String.valueOf(target_value));

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

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if(id == R.id.btnConvert)
        {
            //Test call Using Volley
            // Instantiate the RequestQueue.
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String url ="https://v6.exchangerate-api.com/v6/3808ca223ddc4c6c54c4d05b/pair/USD/BRL";
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    //String conversionRate = "";
//                    double conversionRate = 0;
//                    try {
//                        conversionRate = response.getDouble("conversion_rate");
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(MainActivity.this,"Conversion Rate = " + conversionRate, Toast.LENGTH_LONG).show();
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_LONG).show();
//                }
//            });
//            queue.add(request);


        }
    }
}