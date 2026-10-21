package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.adapters.PantryAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.db.PantryItem;
import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.Listener {
    private DatabaseHelper dbHelper; private PantryAdapter adapter; private RecyclerView recyclerView; private TextView emptyView;
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_pantry_list);Toolbar t=findViewById(R.id.toolbar);setSupportActionBar(t);setTitle(R.string.title_pantry_list);dbHelper=DatabaseHelper.getInstance(this);recyclerView=findViewById(R.id.recycler_pantry);emptyView=findViewById(R.id.text_empty_pantry);adapter=new PantryAdapter(this);recyclerView.setLayoutManager(new LinearLayoutManager(this));recyclerView.setAdapter(adapter);findViewById(R.id.fab_add_item).setOnClickListener(v->startActivity(new Intent(this,AddEditIngredientActivity.class)));}
    @Override protected void onResume(){super.onResume();loadPantryItems();}
    private void loadPantryItems(){List<PantryItem> items=dbHelper.getAllPantryItems();adapter.submitList(items);boolean e=items.isEmpty();emptyView.setVisibility(e?View.VISIBLE:View.GONE);recyclerView.setVisibility(e?View.GONE:View.VISIBLE);}
    @Override public void onItemClicked(PantryItem item){Intent i=new Intent(this,AddEditIngredientActivity.class);i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID,item.getId());startActivity(i);}
    @Override public void onDeleteClicked(PantryItem item){new AlertDialog.Builder(this).setTitle(R.string.delete_confirm_title).setMessage(getString(R.string.delete_confirm_message,item.getName())).setPositiveButton(R.string.action_delete,(d,w)->{dbHelper.deletePantryItem(item.getId());Toast.makeText(this,R.string.toast_deleted,Toast.LENGTH_SHORT).show();loadPantryItems();}).setNegativeButton(R.string.action_cancel,null).show();}
}
