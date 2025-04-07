package com.example.weerappexamen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistratieActivity extends AppCompatActivity {

    // Database object
    private SQLiteDatabase gebruikersDB;
    // EditText velden voor gebruikersnaam en wachtwoord
    private EditText etGebruikersnaam, etWachtwoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registratie);

        // Initialiseer de EditText velden
        etGebruikersnaam = findViewById(R.id.etGebruikersnaam);
        etWachtwoord = findViewById(R.id.etWachtwoord);

        // Open de gebruikersdatabase (of maak deze aan als deze niet bestaat)
        gebruikersDB = this.openOrCreateDatabase("gebruikersDB", MODE_PRIVATE, null);
    }

    // Deze methode wordt uitgevoerd wanneer de gebruiker op de 'registreer' knop drukt
    public void registreer(View view) {
        // roept de ingevoerde gebruikersnaam en wachtwoord
        String gebruiker = etGebruikersnaam.getText().toString().trim();
        String wachtwoord = etWachtwoord.getText().toString().trim();

        // Controleer of beide velden zijn ingevuld
        if (gebruiker.isEmpty() || wachtwoord.isEmpty()) {
            // Toon een foutmelding als een van de velden leeg is
            Toast.makeText(this, "Vul alle velden in!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Controleer of de gebruiker bestaat in de database
        String query = "SELECT inlognaam FROM gegevens WHERE inlognaam = ?";
        if (gebruikersDB.rawQuery(query, new String[]{gebruiker}).getCount() > 0) {
            // Als de gebruiker al bestaat, toon een foutmelding
            Toast.makeText(this, "Gebruiker bestaat al!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Als de gebruiker nog niet bestaat, voeg deze toe aan de database
        ContentValues values = new ContentValues();
        values.put("inlognaam", gebruiker); // Voeg de gebruikersnaam toe
        values.put("wachtwoord", wachtwoord); // Voeg het wachtwoord toe
        gebruikersDB.insert("gegevens", null, values); // Voegt de gegevens in de tabel 'gegevens' in

        // Toon een succesmelding
        Toast.makeText(this, "Registratie succesvol!", Toast.LENGTH_SHORT).show();

        // Navigeer terug naar het inlogscherm (MainActivity)
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Zorg ervoor dat de huidige activiteit wordt afgesloten
    }
}
