package com.example.weerappexamen;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase gebruikersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open of maak de gebruikersdatabase aan
        gebruikersDB = this.openOrCreateDatabase("gebruikersDB", MODE_PRIVATE, null);

        // Controleer of de database al bestaat
        File dbFile = getDatabasePath("gebruikersDB");
        if (!dbFile.exists()) {
            // Als de database nog niet bestaat, wordt deze aangemaakt
            Log.i("database", "Database wordt aangemaakt...");
        } else {
            // Als de database al bestaat, wordt dit gelogd
            Log.i("database", "Database bestaat al.");
        }

        // Maak de tabel 'gegevens' aan als deze nog niet bestaat
        gebruikersDB.execSQL("CREATE TABLE IF NOT EXISTS gegevens (inlognaam VARCHAR PRIMARY KEY, wachtwoord VARCHAR);");
    }

    public void onClick(View view) {
        // Verkrijg de ingevoerde gebruikersnaam en wachtwoord
        EditText username = findViewById(R.id.etInlognaam);
        EditText password = findViewById(R.id.etWachtwoord);
        String gebruiker = username.getText().toString().trim();
        String ww = password.getText().toString().trim();

        // Controleer welke knop werd ingedrukt
        switch (view.getId()) {
            case R.id.registreren:
                // Als de knop 'registreren' is ingedrukt, ga naar het registratie-scherm
                Intent intent = new Intent(this, RegistratieActivity.class);
                startActivity(intent);
                break;

            case R.id.inloggen:
                // Als de knop 'inloggen' is ingedrukt, controleer of beide velden zijn ingevuld
                if (gebruiker.isEmpty() || ww.isEmpty()) {
                    // Toon een melding als de velden niet zijn ingevuld
                    Toast.makeText(this, "Vul alle velden in!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Als velden zijn ingevuld, probeer de gebruiker in te loggen
                loginGebruiker(gebruiker, ww);
                break;
        }
    }

    // Deze methode controleert of de gebruikersnaam en het wachtwoord overeenkomen met de gegevens in de database
    private void loginGebruiker(String gebruiker, String ww) {
        try {
            // SQL-query om het wachtwoord op te halen voor de gegeven gebruikersnaam
            String query = "SELECT wachtwoord FROM gegevens WHERE inlognaam = ?";
            Cursor cursor = gebruikersDB.rawQuery(query, new String[]{gebruiker});

            // Controleer of er resultaten zijn gevonden
            if (cursor != null && cursor.moveToFirst()) {
                // Haal het opgeslagen wachtwoord op
                String storedPassword = cursor.getString(0);
                // Vergelijk het ingevoerde wachtwoord met het opgeslagen wachtwoord
                if (storedPassword.equals(ww)) {
                    // Als het wachtwoord klopt, toon een succesmelding en navigeer naar MainActivity2
                    Toast.makeText(this, "Inloggen succesvol", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity2.class);
                    intent.putExtra("username", gebruiker); // Stuur de gebruikersnaam door naar het volgende scherm
                    startActivity(intent);
                } else {
                    // Als het wachtwoord niet klopt, toon een foutmelding
                    Toast.makeText(this, "Fout wachtwoord", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Als de gebruiker niet bestaat in de database, toon een foutmelding
                Toast.makeText(this, "Gebruiker niet gevonden", Toast.LENGTH_SHORT).show();
            }

            // Sluit de cursor
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            // Als er een fout optreedt tijdens het inloggen, toon een foutmelding
            Toast.makeText(this, "Fout bij inloggen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
