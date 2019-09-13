package com.acs.btdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.acs.btdemo.ReaderActivity.no_WTR;

public class ListWtrActivity extends AppCompatActivity {

    private View heroImageView;
    private CoordinatorLayout mCLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCToolbarLayout;
    private Button btn_konfirm;
    private RecyclerView listView;

    /* Reader to be connected. */
    private String mDeviceName;
    private String mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    /** set up adapter **/
    ArrayList<WtrModel> dataItem = new ArrayList<>();
    Adapter<WtrModel, WtrViewHolder> adapter = new Adapter<WtrModel, WtrViewHolder>
            (R.layout.list_wtr_child, WtrViewHolder.class, dataItem) {

        @Override
        protected void bindView(WtrViewHolder holder, final WtrModel model, int position) {
            holder.onBind(ListWtrActivity.this,model);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String no_WTR2 = model.getTitle();
                    Log.d("URL_re", "onClick: gget title"+no_WTR);
                    final Intent intent = new Intent(ListWtrActivity.this, ReaderActivity.class);
                    intent.putExtra(ListWtrActivity.EXTRAS_DEVICE_NAME, mDeviceName);
                    intent.putExtra(ListWtrActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress );
                    intent.putExtra("no_WTR", no_WTR2 );
                    startActivity(intent);
                }
            });
        }
    };


    Adapter.Callback adapterListeners = new Adapter.Callback() {

        @Override
        public void onImageClick(int position) {
            //code here setOnClickListener()
        }

        @Override
        public void onRemoveItem(int position) {
            //code here setOnClickListener()
        }
    };

    /** start activity **/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_wtr_main);

        /* Initialise list view, hero image, and sticky view */
        listView = (RecyclerView) findViewById(R.id.rv_wtr);
        heroImageView = findViewById(R.id.heroImageView_wtr);

        // Get the widget reference from XML layout
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_wtr);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_wtr);
        mCToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_wtr);

        // Set a title for collapsing toolbar layout
        mCToolbarLayout.setTitleEnabled(true);
        mCToolbarLayout.setTitle("List WTR");

        // Define the collapsing toolbar title text color
        mCToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCToolbarLayout.setExpandedTitleColor(Color.WHITE);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        /** Call data **/
        requestJsonObjectA(new ReaderActivity.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                for(int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject jsonObject = result.getJSONObject(i);
                        String judul= jsonObject.getString("no_WTR");
                        String tanggal=jsonObject.getString("waktu");
                        Log.d("wtr_no", judul);
                        dataItem.add(new WtrModel(judul,tanggal));
                    }
                    catch(JSONException e) {
                        dataItem.add(new WtrModel("Error: " + e.getLocalizedMessage(),"error_tgl"));
                    }
                }
                listView = (RecyclerView) findViewById(R.id.rv_wtr);

                /** set up layout View **/
                setUpView();
            }
        });

        setUpView();
    }

    /** Calling Data Function **/
    private void requestJsonObjectA(final ReaderActivity.VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.1.250.116/rest-api/index.php/api/Item_2/";
        //String url ="http://192.168.0.4/rest-api/index.php/api/Item_2/";

        Log.d("ItemPickedwtr", url);
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                callback.onSuccess(jsonArray);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Eroor", "Error " + error.getMessage());
            }
        });
        queue.add(request);
    }

    private void setUpView() {
        listView.setLayoutManager(new LinearLayoutManager(ListWtrActivity.this, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setCallback(adapterListeners);
    }

}
