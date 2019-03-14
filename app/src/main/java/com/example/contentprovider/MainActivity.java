package com.example.contentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {
     private ListView contactnames;
     private static final int REQUEST_CODE_READ_CONTACTS = 1;
     private static boolean READ_CONTACTS_GRANTED = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactnames = (ListView) findViewById(R.id.contacts_names);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS);
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(READ_CONTACTS_GRANTED){
                    String[] Projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    ContentResolver contentResolver = getContentResolver();

                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, //URI,from where the data is coming
                            Projection, //String holding the names of columns that we want to retrieve
                            null, // it is filter that works a 'WHERE clause' to determine which row to retrieve.Null means that all the rows have to be retrieved
                            null, // almost same as above 3rd parameter
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);// it is used to sort the rows according to the parameter given in it.It acts as ORDER BY clause in SQL, passing NULL will sort in default order i.e. unorder, here it is sorting according to primary key

                    if(cursor != null){
                        List<String> contacts = new ArrayList<String>();
                        while(cursor.moveToNext()){
                            contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                        }
                        cursor.close();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.contact_detail, R.id.name, contacts);
                        contactnames.setAdapter(adapter);
                    }
                }
                else
                {
                    Snackbar.make(view, "Please grant permission to your contacts", Snackbar.LENGTH_LONG)
                            .setAction("Action", new View.OnClickListener(){

                                @Override
                                public void onClick(View v) {
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_CONTACTS)){
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                                    }
                                    else{
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        MainActivity.this.startActivity(intent);
                                    }
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_CODE_READ_CONTACTS:
            { // If rquest is cancelled, rest arrays are empty.
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted, do contacts-related tasks you need to do
                    READ_CONTACTS_GRANTED = true;
                }
                else{
                    // permission denied, disable the functionality that depends on this permission
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
