package com.lewiswilson.kiminojisho;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static String list_selection;//use to collect the "WORD" value and display data in ViewWord
    DatabaseHelper myDB;
    public static Activity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ma=this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.list_jisho);
        FloatingActionButton flbtn_rand = (FloatingActionButton) findViewById(R.id.flbtn_rand);
        myDB = new DatabaseHelper(this);

        ArrayList<String> jishoList = new ArrayList<>();
        Cursor data = myDB.getListContents();

        //Checks if database is empty and lists entries if not
        if(data.getCount() == 0){
            Toast.makeText(MainActivity.this, "The Database is Empty", Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                //ListView Data Layout
                jishoList.add(data.getString(1) + " ; " + data.getString(2) + " ; " +
                        data.getString(3));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jishoList);
                listView.setAdapter(listAdapter);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get "Word" value from listview for using to select db record
                list_selection = (String) listView.getItemAtPosition(position);
                list_selection = list_selection.split(" ;")[0];
                startActivity(new Intent(MainActivity.this, ViewWord.class));
            }
    });

        flbtn_rand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_selection = myDB.random(0);
                startActivity(new Intent(MainActivity.this, ViewWord.class));
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Toolbar Menu Option Activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(MainActivity.this, AddWord.class));
                return true;
            case R.id.action_alarm:
                return true;
            case R.id.action_export:
                Export();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void Export(){
        try {
            String fileToWrite = this.getDatabasePath("kiminojisho.db").toString();

            FileOutputStream output = openFileOutput("kiminojisho.db", Context.MODE_PRIVATE);
            FileInputStream input = new FileInputStream(fileToWrite);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.write(fileToWrite.getBytes());
            output.flush();
            output.close();
            input.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "kiminojisho.db");
            Uri path = FileProvider.getUriForFile(context, "com.lewiswilson.kiminojisho.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("application/x-sqlite3");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "kiminojisho.db");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export Database"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Request Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}