package com.example.daniele.audiototext;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks,NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MyLog_MainActivity";

    //connections
    SpeechService mSpeechService;
    Uri receivedUri;
    Intent receivedIntent;
    InputStream inputStream;

    //db and lists
    static MySQLiteHelper db;
    public LinkedList<Result> resultList;
    public ArrayAdapter<Result> adapter;

    //graphics
    public ProgressDialog loadingDialog;
    Boolean isHomeShown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //graphics
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_home);

        //create db and lists
        db=new MySQLiteHelper(getApplicationContext());
        resultList=(LinkedList<Result>)db.getAllResults();

        //get the received intent if there is one
        receivedIntent = getIntent();
        receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(receivedUri!=null)
        {
            try {
                inputStream = getContentResolver().openInputStream(receivedUri);
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }

        //'first use' dialog
        SharedPreferences pref = MainActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        if(pref.getBoolean("firstStart",true)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(getResources().getString(R.string.tutorial)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstStart",false);
            editor.apply();
        }
    }

    public void ClearLists(){
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.list_cleared),Toast.LENGTH_LONG).show();
        db.deleteAllResults();
        resultList.clear();
        adapter.notifyDataSetChanged();
    }

    private void updateLists(Result r) {
        db.addResult(r);
        resultList.addFirst(r);
        adapter.notifyDataSetChanged();
    }

    //check if an audio has been shared
    public boolean audioShared(){
        return !(inputStream==null);
    }

    //open Whatsapp to share the audio if it is installed
    public void openWhatsapp(){
        try{
            startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.whatsapp")));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.whatsapp_error),Toast.LENGTH_LONG).show();
        }
    }

    //copy the text recognized to the clipboard
    public void copyToClipboard(int index){
        String text=resultList.get(index).getText();
        String label= getResources().getString(R.string.copied_to_clipboard);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(),label ,Toast.LENGTH_LONG).show();
    }

    //show a helping dialog at the first use
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    displaySelectedScreen(R.id.nav_help);
                    break;
            }
        }
    };

    /*----------------------------------------------------------------------------------
                                   ↓NAVIGATION DRAWER↓
    ----------------------------------------------------------------------------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //changing the fragments with the NavigationDrawer's buttons
    private void displaySelectedScreen(int id){
        Fragment fragment=null;
        switch(id){
            case R.id.nav_home:
                fragment=new HomeFragment();
                isHomeShown=true;
                break;
            case R.id.nav_settings:
                fragment=new SettingsFragment();
                isHomeShown=false;
                break;
            case R.id.nav_help:
                fragment=new HelpFragment();
                isHomeShown=false;
                break;
        }

        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;
    }

    //if back button is pressed in the home the app will close, in other cases home fragment will be opened
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isHomeShown)
                super.onBackPressed();
            else
                displaySelectedScreen(R.id.nav_home);
        }
    }

    /*----------------------------------------------------------------------------------
                                   ↓SPEECH RECOGNIZING↓
    ----------------------------------------------------------------------------------*/

    //the service, the listeners and the callbacks are initialized
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mSpeechService.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }
    };

    //the RecognizedInputStream method is called and the loading dialog is opened
    public void Recognize(){
        try {
            final InputStream finalInputStream = inputStream;
            mSpeechService.recognizeInputStream(finalInputStream);
            loadingDialog = ProgressDialog.show(MainActivity.this, "",
                    getResources().getString(R.string.wait), true);
            inputStream=null;
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        // Stop Cloud Speech API
        mSpeechService.setCallbacks(null);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }

    //when the speech has been recognized the results are updated
    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingDialog.dismiss();
                            Result r= new Result(text, new SimpleDateFormat("d MMM yyyy HH:mm:ss").format(new java.util.Date()));
                            updateLists(r);
                        }
                    });
                }
            };

    //method from the ServiceCallbacks interface, this method is called from the service
    public void errorOccurred(String error){
        if(loadingDialog.isShowing())
            loadingDialog.dismiss();
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }
}

//interface implemented to permit to the service to call MainActivity's methods
interface ServiceCallbacks {
    void errorOccurred(String error);
}
