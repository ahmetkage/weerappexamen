package com.example.weerappexamen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity2 extends AppCompatActivity {

    EditText etCity, etCountry;
    TextView tvResult, tvWelcome;
    Button btnLogout;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "34bb3506cdc0753617c923b346de31d0";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvResult = findViewById(R.id.tvResult);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        // Haal de gebruikersnaam op uit de intent en verwelkom de gebruiker
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            tvWelcome.setText("Welkom, " + username + "!");
        }

        // Stel de functionaliteit in voor de log uit knop
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigeer naar het inlogscherm (MainActivity) bij uitloggen
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Deze methode wordt aangeroepen wanneer de gebruiker op de knop 'Weer ophalen' klikt
    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = etCity.getText().toString().trim(); // roept de stad
        String country = etCountry.getText().toString().trim(); // roept het land

        // Controleer of de stad is ingevuld
        if (city.isEmpty()) {
            tvResult.setText("Vul een stad in!"); // Toon een foutmelding als de stad leeg is
        } else {
            // Bouw de URL voor de API-aanroep afhankelijk van of het land is ingevuld
            tempUrl = country.isEmpty() ?
                    url + "?q=" + city + "&appid=" + appid :
                    url + "?q=" + city + "," + country + "&appid=" + appid;

            Log.d("WeatherApp", "API URL: " + tempUrl);

            // Maak een HTTP-aanroep naar de weer-API
            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("WeatherApp", "API Response: " + response);

                    try {
                        // Verwerk de API-respons (JSON)
                        JSONObject jsonResponse = new JSONObject(response);

                        // Controleer of de vereiste gegevens aanwezig zijn in de JSON
                        if (!jsonResponse.has("weather") || !jsonResponse.has("main")) {
                            tvResult.setText("Geen weergegevens gevonden voor de opgegeven stad.");
                            return;
                        }

                        // Verkrijg de weergegevens uit de JSON-response
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");

                        // Verkrijg de temperatuur- en andere gegevens uit de JSON
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15; // Zet temperatuur om naar Celsius
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        int pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");

                        // Verkrijg de wind- en bewolkingsgegevens
                        String wind = jsonResponse.getJSONObject("wind").getString("speed");
                        String clouds = jsonResponse.getJSONObject("clouds").getString("all");

                        // Verkrijg de stad- en landnaam
                        String countryName = jsonResponse.getJSONObject("sys").getString("country");
                        String cityName = jsonResponse.getString("name");

                        // Stel de tekstkleur in en toon de weergegevens
                        tvResult.setTextColor(Color.BLACK);
                        String output = "Weer in " + cityName + " (" + countryName + "):\n"
                                + "Temperatuur: " + df.format(temp) + "°C\n"
                                + "Voelt als: " + df.format(feelsLike) + "°C\n"
                                + "Vochtigheid: " + humidity + "%\n"
                                + "Beschrijving: " + description + "\n"
                                + "Wind snelheid: " + wind + " m/s\n"
                                + "Bewolking: " + clouds + "%\n"
                                + "Luchtdruk: " + pressure + " hPa";
                        tvResult.setText(output);
                    } catch (JSONException e) {
                        // Toon een foutmelding als er een probleem is met het verwerken van de JSON
                        Log.e("WeatherApp", "JSON Fout: " + e.getMessage());
                        Toast.makeText(MainActivity2.this, "Fout bij verwerken JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toon een foutmelding als er een probleem is met de API-aanroep
                    Log.e("WeatherApp", "Volley Fout: " + error.toString());
                    Toast.makeText(MainActivity2.this, "Fout bij ophalen weergegevens! Controleer je stadnaam of internetverbinding.", Toast.LENGTH_SHORT).show();
                }
            });

            // Voer de HTTP-aanroep uit via de Volley request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }
}
