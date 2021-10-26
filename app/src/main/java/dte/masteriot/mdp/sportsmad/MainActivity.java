package dte.masteriot.mdp.sportsmad;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final List<dte.masteriot.mdp.sportsmad.SportCenter> generalList = new ArrayList<dte.masteriot.mdp.sportsmad.SportCenter>();
    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private static final String CONTENT_TYPE_JSON = "application/json";
    private SportCenterParser sportCenterParser = new SportCenterParser(this);;
    private static final String URL_JSON = "https://datos.madrid.es/portal/site/egob/menuitem.ac61933d6ee3c31cae77ae7784f1a5a0/?vgnextoid=00149033f2201410VgnVCM100000171f5a0aRCRD&format=json&file=0&filename=200186-0-polideportivos&mgmtid=4a5fbef4b2503410VgnVCM2000000c205a0aRCRD&preview=full";
    private TextView messageInfo;
    private MyOnItemActivatedListener onItemActivatedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("num..",generalList.size()+"");
        messageInfo=findViewById(R.id.messageInfo);

        loadRecyclerView();

        loadSportCenters();

    }
    private void loadRecyclerView(){

        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new MyAdapter(this, generalList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Tracker
        onItemActivatedListener = new MyOnItemActivatedListener(this,generalList);
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);
        recyclerViewAdapter.notifyDataSetChanged();
    }
    private void loadSportCenters(){
        //Handler to receive Sport Centers
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                String string_result;
                super.handleMessage(msg);
                Log.d(logTag, "message received from background thread");
                if(msg.getData().getBoolean("result")) {
                    generalList.clear();
                    for (dte.masteriot.mdp.sportsmad.SportCenter sc: sportCenterParser.getParse()) {
                        generalList.add(sc);
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    messageInfo.setText("");
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(this,handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }
}