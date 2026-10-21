package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.adapters.PantryAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.db.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.Listener {
    private DatabaseHelper dbHelper; private PantryAdapter adapter; private RecyclerView recycler; private TextView empty;
    @Override protected void onCreate(Bundle b) { super.onCreate(b); setContentView(R.layout.activity_pantry_list);
        Toolbar t=findViewById(R.id.toolbar); setSupportActionBar(t); setTitle(R.string.title_pantry_list);
        dbHelper=DatabaseHelper.getInstance(this); recycler=findViewById(R.id.recycler_pantry); empty=findViewById(R.id.text_empty_pantry);
        adapter=new PantryAdapter(this); recycler.setLayoutManager(new LinearLayoutManager(this)); recycler.setAdapter(adapter);
        findViewById(R.id.fab_add_item).setOnClickListener(v -> { /* add screen arrives in the next stage */ }); loadPantryItems(); }
    @Override protected void onResume(){super.onResume(); loadPantryItems();}
    private void loadPantryItems(){List<PantryItem> items=dbHelper.getAllPantryItems(); adapter.submitList(items); boolean e=items.isEmpty(); empty.setVisibility(e?View.VISIBLE:View.GONE); recycler.setVisibility(e?View.GONE:View.VISIBLE);}
    @Override public void onItemClicked(PantryItem item) {}
    @Override public void onDeleteClicked(PantryItem item){dbHelper.deletePantryItem(item.getId()); loadPantryItems();}
}
