package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch musicSwitch, fxSoundSwitch;
    private Spinner languageSpinner;
    private String music, fxSounds, language;
    private Button saveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //get music, fxSounds and language values for local device
        music = PlayerPreferences.getMusic();
        fxSounds = PlayerPreferences.getFxSounds();
        language = PlayerPreferences.getGameLanguage();
        //initialize the elements
        initPlayerSettings();
        // set dropdown language list
        setDropDownLanguageList();
        //set switchers
        setSwitchers();
        //set save button
        saveSettings.setOnClickListener(view -> {
            setLocale();
            Intent intent =  new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void initPlayerSettings() {
        musicSwitch = findViewById(R.id.switchMusic);
        fxSoundSwitch = findViewById(R.id.switchSounds);
        languageSpinner = findViewById(R.id.spinnerLanguageSelector);
        saveSettings = findViewById(R.id.saveSettings);
    }

    //set values for dropdown languages list
    private void setDropDownLanguageList() {
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(Settings.this, R.array.available_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(Settings.this);
        //set spinner start position
        switch (language) {
            case "English":
                languageSpinner.setSelection(0);
                break;
            case "Spanish":
                languageSpinner.setSelection(1);
                break;
            case "German":
                languageSpinner.setSelection(2);
                break;
            case "Italian":
                languageSpinner.setSelection(3);
                break;
            default:
                languageSpinner.setSelection(4);
                break;
        }
    }

    //change shared pref language value
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                PlayerPreferences.setLanguage("English");
                language = PlayerPreferences.getGameLanguage();
                break;
            case 1:
                PlayerPreferences.setLanguage("Spanish");
                language = PlayerPreferences.getGameLanguage();
                break;
            case 2:
                PlayerPreferences.setLanguage("German");
                language = PlayerPreferences.getGameLanguage();
                break;
            case 3:
                PlayerPreferences.setLanguage("Italian");
                language = PlayerPreferences.getGameLanguage();
                break;
            default:
                PlayerPreferences.setLanguage("French");
                language = PlayerPreferences.getGameLanguage();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //do nothing
    }

    public void setSwitchers() {
        //set music switch value
        if(music.equals("On")) {
            musicSwitch.setChecked(true);
        } else {
            musicSwitch.setChecked(false);
        }
        //manipulate music switch value and save the value to the local device
        musicSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(musicSwitch.isChecked()) {
                PlayerPreferences.setMusic("On");
            } else {
                PlayerPreferences.setMusic("Off");
            }
        });
        //set fxSounds switch value
        if(fxSounds.equals("On")) {
            fxSoundSwitch.setChecked(true);
        } else {
            fxSoundSwitch.setChecked(false);
        }
        //manipulate fxSounds switch value and save the value to the local device
        fxSoundSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(fxSoundSwitch.isChecked()) {
                PlayerPreferences.setFxSounds("On");
            } else {
                PlayerPreferences.setFxSounds("Off");
            }
        });
    }

    private void setLocale() {
        String lang;
        int langId = languageSpinner.getSelectedItemPosition();
        //set locale string
        if(langId == 0) {
            lang = "en";
        } else if(langId == 1) {
            lang = "es";
        } else if(langId == 2) {
            lang = "de";
        } else if(langId == 3) {
            lang = "it";
        } else {
            lang = "fr";
        }
        //change configuration
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //restart app with the new language
        Intent refresh = new Intent(this, Settings.class);
        finish();
        startActivity(refresh);
    }
}