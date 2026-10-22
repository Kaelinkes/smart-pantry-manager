package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapters.PantryAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.db.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * The app's launcher Activity and home screen. Shows everything currently in the pantry in a
 * RecyclerView backed by SQLite, and is the entry point to adding, editing and deleting items.
 *
 * Lifecycle note: the list is refreshed in onResume() rather than onCreate(), because the user
 * returns to this screen after adding or editing an item in AddEditIngredientActivity and the
 * data must be re-read from the database at that point.
 */
public class PantryListActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_pantry_list);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyView = findViewById(R.id.text_empty_pantry);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_item);
        fab.setOnClickListener(v -> {
            // No item id extra = the Add/Edit screen opens in "add new" mode.
            Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Returning via Back leaves the nav bar highlighting the tab we left from, so re-check ours.
        ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                .getMenu().findItem(R.id.nav_pantry).setChecked(true);
        adapter.setHighlightExpiring(SettingsActivity.isExpiryAlertsEnabled(this));
        loadPantryItems();
    }

    /** Reads every pantry row from SQLite and hands it to the adapter. */
    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.submitList(items);

        // Show a friendly empty state instead of a blank screen.
        boolean isEmpty = items.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemClicked(PantryItem item) {
        // Pass the row id through the Intent so the next screen can load and edit that record.
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, item.getName()))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /** Wires up the bottom navigation bar shared by the three top-level screens. */
    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_pantry);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_suggestions) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
