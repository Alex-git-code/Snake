package org.alexcode.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Switch musicSwitch, fxSoundSwitch;
    private Spinner languageSpinner;
    private String music, fxSounds, language;
    private Button saveSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //get music and fxSounds values for local device
        music = PlayerPreferences.getMusic();
        fxSounds = PlayerPreferences.getFxSounds();
        language = PlayerPreferences.getGameLanguage();
        //initialize the elements
        initPlayerSettings();
        //set switchers
        setSwitchers();
        // set dropdown language list
        setDropDownLanguageList();
        //set save button
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initPlayerSettings() {
        musicSwitch = findViewById(R.id.switchMusic);
        fxSoundSwitch = findViewById(R.id.switchSounds);
        languageSpinner = findViewById(R.id.spinnerLanguageSelector);
        saveSettings = findViewById(R.id.saveSettings);
    }

    private void setDropDownLanguageList() {
        //set values for dropdown languages list
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(Settings.this, R.array.available_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(Settings.this);
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
            default:
                languageSpinner.setSelection(3);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        PlayerPreferences.setLanguage(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void setSwitchers() {
        //set music switch value
        if(music.equals("On")) {
            musicSwitch.setChecked(true);
        } else {
            musicSwitch.setChecked(false);
        }
        //manipulate music switch value and save the value to the local device
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(musicSwitch.isChecked()) {
                    PlayerPreferences.setMusic("On");
                } else {
                    PlayerPreferences.setMusic("Off");
                }
            }
        });
        //set fxSounds switch value
        if(fxSounds.equals("On")) {
            fxSoundSwitch.setChecked(true);
        } else {
            fxSoundSwitch.setChecked(false);
        }
        //manipulate fxSounds switch value and save the value to the local device
        fxSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(fxSoundSwitch.isChecked()) {
                    PlayerPreferences.setFxSounds("On");
                } else {
                    PlayerPreferences.setFxSounds("Off");
                }
            }
        });
    }
}