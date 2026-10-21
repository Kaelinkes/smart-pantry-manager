package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.db.PantryItem;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID="com.example.smartpantry.EXTRA_ITEM_ID";private static final long NO_ID=-1L;private static final String[] UNITS={"pcs","g","kg","ml","l","tsp","tbsp","cup"};
    private DatabaseHelper dbHelper;private EditText inputName,inputQuantity,inputExpiry;private Spinner spinnerUnit;private long itemId=NO_ID;
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_add_edit_ingredient);Toolbar toolbar=findViewById(R.id.toolbar);setSupportActionBar(toolbar);if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);dbHelper=DatabaseHelper.getInstance(this);inputName=findViewById(R.id.input_name);inputQuantity=findViewById(R.id.input_quantity);inputExpiry=findViewById(R.id.input_expiry);spinnerUnit=findViewById(R.id.spinner_unit);ArrayAdapter<String> unitAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,UNITS);unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinnerUnit.setAdapter(unitAdapter);itemId=getIntent().getLongExtra(EXTRA_ITEM_ID,NO_ID);Button deleteButton=findViewById(R.id.btn_delete);if(itemId!=NO_ID){setTitle("Edit Ingredient");deleteButton.setVisibility(View.VISIBLE);loadExistingItem();}else{setTitle("Add Ingredient");deleteButton.setVisibility(View.GONE);preselectDefaultUnit();}findViewById(R.id.btn_pick_date).setOnClickListener(v->showDatePicker());findViewById(R.id.btn_save).setOnClickListener(v->saveItem());deleteButton.setOnClickListener(v->deleteItem());}
    private void loadExistingItem(){PantryItem item=dbHelper.getPantryItem(itemId);if(item==null){Toast.makeText(this,"Item no longer exists",Toast.LENGTH_SHORT).show();finish();return;}inputName.setText(item.getName());inputQuantity.setText(formatQuantity(item.getQuantity()));inputExpiry.setText(item.getExpiryDate()==null?"":item.getExpiryDate());for(int i=0;i<UNITS.length;i++)if(UNITS[i].equalsIgnoreCase(item.getUnit())){spinnerUnit.setSelection(i);break;}}
    private void preselectDefaultUnit(){String preferred=SettingsActivity.getPreferredUnitSystem(this);String defaultUnit=SettingsActivity.UNITS_IMPERIAL.equals(preferred)?"cup":"g";for(int i=0;i<UNITS.length;i++)if(UNITS[i].equals(defaultUnit)){spinnerUnit.setSelection(i);return;}}
    private void saveItem(){String name=inputName.getText().toString().trim(),quantityText=inputQuantity.getText().toString().trim(),expiry=inputExpiry.getText().toString().trim(),unit=(String)spinnerUnit.getSelectedItem();boolean valid=true;if(name.isEmpty()){inputName.setError(getString(R.string.error_name_required));valid=false;}double quantity=0;try{quantity=Double.parseDouble(quantityText);if(quantity<=0){inputQuantity.setError(getString(R.string.error_quantity_required));valid=false;}}catch(NumberFormatException e){inputQuantity.setError(getString(R.string.error_quantity_required));valid=false;}if(!valid)return;PantryItem item=new PantryItem();item.setId(itemId);item.setName(name);item.setQuantity(quantity);item.setUnit(unit);item.setExpiryDate(expiry.isEmpty()?null:expiry);if(itemId==NO_ID)dbHelper.insertPantryItem(item);else dbHelper.updatePantryItem(item);Toast.makeText(this,R.string.toast_saved,Toast.LENGTH_SHORT).show();finish();}
    private void deleteItem(){if(itemId!=NO_ID){dbHelper.deletePantryItem(itemId);Toast.makeText(this,R.string.toast_deleted,Toast.LENGTH_SHORT).show();}finish();}
    private void showDatePicker(){Calendar c=Calendar.getInstance();new DatePickerDialog(this,(view,y,m,d)->inputExpiry.setText(String.format(Locale.ROOT,"%04d-%02d-%02d",y,m+1,d)),c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();}
    private String formatQuantity(double q){return q==Math.floor(q)?String.valueOf((long)q):String.valueOf(q);}
    @Override public boolean onOptionsItemSelected(MenuItem item){if(item.getItemId()==android.R.id.home){finish();return true;}return super.onOptionsItemSelected(item);}
}
