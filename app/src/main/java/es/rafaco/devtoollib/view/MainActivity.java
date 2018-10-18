package es.rafaco.devtoollib.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import es.rafaco.devtoollib.R;
import es.rafaco.devtoollib.SampleApp;
import es.rafaco.devtoollib.api.Controller;
import es.rafaco.devtoollib.api.SampleApiService;
import es.rafaco.devtools.DevTools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                throw new NullPointerException("A simulated exception from MainActivity fab button");
                /*Intent intent = new Intent(getApplicationContext(), CrashActivity.class);
                intent.putExtra("TITLE", "title");
                intent.putExtra("MESSAGE", "exMessage");
                getApplicationContext().startActivity(intent);*/
            }
        });

        AppCompatButton showTools = findViewById(R.id.show_tools);
        showTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevTools.openTools(false);
            }
        });

        AppCompatButton browseDemo = findViewById(R.id.browse);
        browseDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
                startActivity(intent);
            }
        });

        Controller controller = new Controller();
        controller.start(getApplicationContext());

        Log.d(SampleApp.TAG, "MainActivity onCreate() performed");
        DevTools.breackpoint(this);
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