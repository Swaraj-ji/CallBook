package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    MyDBHandler dm;
    ListView lv_contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dm = new MyDBHandler(this);
        lv_contacts = findViewById(R.id.lv_contacts);
        ArrayList<HashMap<String,Object>> al = new ArrayList<HashMap<String,Object>>();
        Cursor cur;
        {
            cur = dm.getInfo();
            if (cur.getCount() == 0)
                Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
            else {
                while (cur.moveToNext()) {
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("names", cur.getString(0).trim());
                    hm.put("number", cur.getString(1).trim());
                    al.add(hm);
                }
            }
        }
        String [] from= {"names","number"};
        int [] to = {R.id.tv_name,R.id.tv_number};
        SimpleAdapter SA= new SimpleAdapter(this,al,R.layout.contact_detail,from,to);
        lv_contacts.setAdapter(SA);
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name =((TextView)view.findViewById(R.id.tv_name)).getText().toString().trim();
                String number =((TextView)view.findViewById(R.id.tv_number)).getText().toString().trim();
                AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();
                View inflater = getLayoutInflater().inflate(R.layout.more_action,null);
                builder.setCancelable(true);
                builder.setView(inflater);
                // Add action buttons
                ImageButton ib_delete = inflater.findViewById(R.id.ib_delete);
                ImageButton ib_edit = inflater.findViewById(R.id.ib_edit);
                ImageButton ib_call = inflater.findViewById(R.id.ib_call);
                ib_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.cancel();
                        Log.i(null,"Clicked Delete button===========================");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setCancelable(true)
                                .setMessage("Delete this item ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Boolean i = dm.delete_data(number);
                                        Log.i(null,"Delete_data function executed");
                                        if(i)
                                            Toast.makeText(MainActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MainActivity.this,"Unsuccessfully",Toast.LENGTH_SHORT).show();
                                        recreate();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder1.show();
                    }
                });
                ib_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(null,"Clicked Edit button===========================");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        View inflater1 = getLayoutInflater().inflate(R.layout.activity_edit,null);
                        builder1.setCancelable(true);
                        builder1.setView(inflater1);
                        EditText et_new_name = inflater1.findViewById(R.id.et_new_name);
                        EditText et_new_number = inflater1.findViewById(R.id.et_new_number);
                        et_new_name.setText(name);
                        et_new_number.setText(number);
                        builder1.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(et_new_name.getText().toString().isEmpty()){
                                    et_new_name.setError("Empty");
                                    et_new_name.requestFocus(); }
                                else if(et_new_number.getText().toString().isEmpty()){
                                    et_new_number.setError("Empty");
                                    et_new_number.requestFocus();
                                }
                                else {
                                    String new_name = et_new_name.getText().toString().trim();
                                    String new_number = et_new_number.getText().toString().trim();
                                    Boolean i = dm.update_data(new_number, new_name);
                                    if (i)
                                        Toast.makeText(MainActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this, "Unsuccessful, Try Again", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder1.show();
                    }
                });
                ib_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+number));//change the number
                        startActivity(callIntent);
                        finish();
                    }
                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
                builder.show();
            }
        });
    }

    public void floatingAction(View view) {
        // Insert Element Using Custom Alert Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View inflater = getLayoutInflater().inflate(R.layout.custom_dailog,null);
        builder.setCancelable(true);
        builder.setView(inflater)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText et_name = inflater.findViewById(R.id.et_name);
                        EditText et_number = inflater.findViewById(R.id.et_number);
                        if(et_name.getText().toString().isEmpty()){
                            et_name.setError("Empty");
                            et_name.requestFocus();
                        }
                        else if(et_number.getText().toString().isEmpty()){
                            et_number.setError("Empty");
                            et_number.requestFocus();
                        }
                        else{
                            String name = et_name.getText().toString().trim();
                            String number = et_number.getText().toString().trim();
                            boolean i = dm.insert_data(name,number);
                            if(i)
                                Toast.makeText(MainActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this,"Number Already Exist",Toast.LENGTH_LONG).show();
                            et_name.setText("");
                            et_number.setText("");
                            recreate();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}