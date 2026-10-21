package com.example.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME="smart_pantry_prefs", KEY_EXPIRY_ALERTS="expiry_alerts_enabled", KEY_UNIT_SYSTEM="unit_system";
    public static final String UNITS_METRIC="metric", UNITS_IMPERIAL="imperial";
    public static boolean isExpiryAlertsEnabled(Context context){return prefs(context).getBoolean(KEY_EXPIRY_ALERTS,true);}
    public static String getPreferredUnitSystem(Context context){return prefs(context).getString(KEY_UNIT_SYSTEM,UNITS_METRIC);}
    private static SharedPreferences prefs(Context context){return context.getApplicationContext().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);}
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_settings);Toolbar toolbar=findViewById(R.id.toolbar);setSupportActionBar(toolbar);setTitle(R.string.title_settings);SwitchMaterial expiry=findViewById(R.id.switch_expiry_alerts);expiry.setChecked(isExpiryAlertsEnabled(this));expiry.setOnCheckedChangeListener((v,checked)->prefs(this).edit().putBoolean(KEY_EXPIRY_ALERTS,checked).apply());RadioGroup group=findViewById(R.id.radio_group_units);RadioButton metric=findViewById(R.id.radio_metric),imperial=findViewById(R.id.radio_imperial);if(UNITS_IMPERIAL.equals(getPreferredUnitSystem(this)))imperial.setChecked(true);else metric.setChecked(true);group.setOnCheckedChangeListener((g,id)->prefs(this).edit().putString(KEY_UNIT_SYSTEM,id==R.id.radio_imperial?UNITS_IMPERIAL:UNITS_METRIC).apply());setupBottomNavigation();}
    @Override protected void onResume(){super.onResume();((BottomNavigationView)findViewById(R.id.bottom_navigation)).getMenu().findItem(R.id.nav_settings).setChecked(true);}
    private void setupBottomNavigation(){BottomNavigationView nav=findViewById(R.id.bottom_navigation);nav.setSelectedItemId(R.id.nav_settings);nav.setOnItemSelectedListener(item->{int id=item.getItemId();if(id==R.id.nav_settings)return true;if(id==R.id.nav_pantry){startActivity(new Intent(this,PantryListActivity.class));overridePendingTransition(0,0);finish();return true;}if(id==R.id.nav_suggestions){startActivity(new Intent(this,SuggestedRecipesActivity.class));overridePendingTransition(0,0);finish();return true;}return false;});}
}
