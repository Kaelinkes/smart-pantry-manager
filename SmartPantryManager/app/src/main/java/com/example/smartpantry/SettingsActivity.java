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

/**
 * Settings / profile screen. Preferences are small key-value flags rather than relational data,
 * so they are stored in SharedPreferences instead of SQLite - the database is reserved for the
 * pantry and recipe records themselves.
 *
 * The static getters here are used by the other Activities so the preference keys only ever
 * appear in one place.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_IMPERIAL = "imperial";

    /** True if the user wants items expiring within 3 days highlighted in the pantry list. */
    public static boolean isExpiryAlertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    /** Returns UNITS_METRIC or UNITS_IMPERIAL. */
    public static String getPreferredUnitSystem(Context context) {
        return prefs(context).getString(KEY_UNIT_SYSTEM, UNITS_METRIC);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_settings);

        SwitchMaterial expirySwitch = findViewById(R.id.switch_expiry_alerts);
        expirySwitch.setChecked(isExpiryAlertsEnabled(this));
        expirySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs(this).edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        RadioGroup unitGroup = findViewById(R.id.radio_group_units);
        RadioButton metricButton = findViewById(R.id.radio_metric);
        RadioButton imperialButton = findViewById(R.id.radio_imperial);

        if (UNITS_IMPERIAL.equals(getPreferredUnitSystem(this))) {
            imperialButton.setChecked(true);
        } else {
            metricButton.setChecked(true);
        }

        unitGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String value = (checkedId == R.id.radio_imperial) ? UNITS_IMPERIAL : UNITS_METRIC;
            prefs(this).edit().putString(KEY_UNIT_SYSTEM, value).apply();
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Returning via Back leaves the nav bar highlighting the tab we left from, so re-check ours.
        ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                .getMenu().findItem(R.id.nav_settings).setChecked(true);
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_settings) {
                return true;
            } else if (id == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryListActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_suggestions) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
