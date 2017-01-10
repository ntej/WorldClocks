package www.ntej.com.worldclocks;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.ntej.www.TimeAndDateGenerator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import other.CommonFunctionalityClass;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String url = "https://www.currentmillis.com/time/minutes-since-unix-epoch.php";

    RequestQueue queue;
    Timer timer, animationTimer,updateTrackTimer;

    int updateMins =0;

    long millis;
    int aMillisStringSize, aI;
    String aMillisString, aMillisStringResetted;
    boolean isTimerAndQueueEnabled = false;
    boolean isFromOncreate=true;
    ArrayList<String> allZones = new ArrayList<>();
    ArrayList<String> allZones_temp = new ArrayList<>();

    TextView timeTextView;
    TextView snapTextView;
    TextView zonesUpdateTextView;
    ActionProcessButton syncButton;
    ListView allZonesListView;
    CoordinatorLayout coordinatorLayout; //For the snack bar
    Toolbar toolbar; //for search toolbar

    SearchView searchView;

    Snackbar snackbar;

    ArrayAdapter<String> adapter;

    CommonFunctionalityClass commonFunctionalityClass;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                 /*msg to update Millies TextView from thread(timer) started from incrementTimer() method*/
                case 0: timeTextView.setText(Long.toString(millis));
                    break;

                /*msg to update Millies TextView(each character to zero one by one) which is done by thread(animationTimer) started from programmingAnimation() method*/
                case 1 :timeTextView.setText(aMillisStringResetted);
                    break;

                 /*msg to call unixEpochTimeRequest() method from main thread instead of from thread(animationTimer) in method programmingAnimation
    to achieve progress animation on Synchronize button */
                case 2 : unixEpochTimeRequest();
                    break;

                /*msg to update time since timezones are updated, from thread(updateTrackTimer) from zonesSinceUpdatedTimer() method */
                case 3 :zonesUpdateTextView.setText("timezones updated "+ updateMins+"min ago...");
                    break;


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonFunctionalityClass = new CommonFunctionalityClass(MainActivity.this);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        snapTextView = (TextView) findViewById(R.id.snappedMillisTextView);

        zonesUpdateTextView = (TextView) findViewById(R.id.zonesUpdateTextView);

        allZonesListView = (ListView) findViewById(R.id.timeZonesListView);
        allZones = TimeAndDateGenerator.getAllZoneIdsWithTimeAndDate();
        adapter = new ArrayAdapter<>(this,R.layout.zones_list_view, R.id.textView, allZones);
        allZonesListView.setAdapter(adapter);


        syncButton = (ActionProcessButton) findViewById(R.id.syncProcessButton);
        syncButton.setOnClickListener(this);
        syncButton.setClickable(false);
        syncButton.setMode(ActionProcessButton.Mode.ENDLESS);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarPosition);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        zonesUpdateTextView.setText("timezones updated "+ updateMins+"min ago...");
        zonesSinceUpdatedTimer();


    }


    @Override
    protected void onStart() {
        super.onStart();

        //Inistiate the RequestQueue
        queue = Volley.newRequestQueue(getApplicationContext());

        unixEpochTimeRequest();

    }

    private void unixEpochTimeRequest() {
        //request a string response from the provided URL
        syncButton.setProgress(0);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        syncButton.setProgress(0);


                        millis = (Long.parseLong(response) * 60 * 1000);

                        if(isFromOncreate)
                        {
                            isFromOncreate = false;
                            snapTextView.setText(Long.toString(millis) + " mSec");
                            Toast.makeText(MainActivity.this,R.string.app_start_toast,Toast.LENGTH_SHORT).show();

                        }
                        syncButton.setClickable(true);

                        incrementTimer();

                        isTimerAndQueueEnabled = true;


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                displayErrorSnackbar();

            }
        });


        stringRequest.setShouldCache(false); //Disabling cache
        queue.add(stringRequest);
        if (syncButton.getProgress() < 100) {
            syncButton.setProgress(syncButton.getProgress() + 25);
        }


    }

    private void displayErrorSnackbar() {
        snackbar = Snackbar.make(coordinatorLayout, R.string.volley_error_snackbar_title,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.volley_error_snackbar_action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unixEpochTimeRequest();
            }
        });
        snackbar.show();
    }



    private void incrementTimer() {
        timer = new Timer();
        timer.schedule(new TimeIncrementor(), 0, 500);
    }

    class TimeIncrementor extends TimerTask {
        @Override
        public void run() {
            millis = millis + 520;  //20 millis error correction
            handler.sendEmptyMessage(0);
        }


    }

    private void zonesSinceUpdatedTimer()
    {
        updateTrackTimer = new Timer();
        updateTrackTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                updateMins = updateMins +1;
                handler.sendEmptyMessage(3);
            }
        },60000,60000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.syncProcessButton:


                aI = 0; //initilizing to zero here to override aI=1 in last call of run() in animation timer

                //temporary disabled to avoid creating multiple threads
                syncButton.setClickable(false);

                timer.cancel();
                programmingAnimation(millis);

                break;
        }

    }

    public void programmingAnimation(long millis) {


        aMillisString = Long.toString(millis);

        aMillisStringSize = aMillisString.length();

        animationTimer = new Timer();

        animationTimer.schedule(new AnimationTask(), 0, 200);

        if (syncButton.getProgress() < 100) {
            syncButton.setProgress(syncButton.getProgress() + 25);
        }

    }

    class AnimationTask extends TimerTask {
        @Override
        public void run() {


            aMillisStringResetted = aMillisString.replace(aMillisString.charAt(aI), '0');
            aMillisString = aMillisStringResetted;


            handler.sendEmptyMessage(1);


            if (aI == aMillisStringSize - 1) {


                handler.sendEmptyMessage(2);

                animationTimer.cancel();

            }
            aI++;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


            getMenuInflater().inflate(R.menu.tool_bar_menu, menu);

            //Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));


            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.snap:

                snapTextView.setText(Long.toString(millis) + " millis");

                populateTimeZones();

                Toast.makeText(getApplicationContext(), R.string.millis_snap_shorted_toast, Toast.LENGTH_LONG).show();

                updateMins = 0;
                zonesUpdateTextView.setText("timezones updated "+ updateMins+"min ago...");
                updateTrackTimer.cancel();
                zonesSinceUpdatedTimer();

                return true;

            case R.id.info:
                commonFunctionalityClass.ShowAboutDialog();
                return true;

        }return true;

    }

    private void populateTimeZones() {

        allZones.clear();
        allZones_temp.clear();

        allZones_temp = TimeAndDateGenerator.getAllZoneIdsWithTimeAndDate();

        for(int i=0 ;i<allZones_temp.size();i++)
        {
            allZones.add(allZones_temp.get(i));
        }

        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (isTimerAndQueueEnabled == true) {
            timer.cancel();
            queue.cancelAll(this);
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        timer.cancel(); //closing  thread = resource reclaming
        updateTrackTimer.cancel(); //closing thread - resource reclaiming



    }
}