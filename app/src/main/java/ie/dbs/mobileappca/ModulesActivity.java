package ie.dbs.mobileappca;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModulesActivity extends AppCompatActivity {

    public static RequestQueue queue;
    TextView name;
    private String Id;
    private AppDatabase database;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        database = AppDatabase.getDatabase(getApplicationContext());
        user = database.userDAO().getUser();
        Log.v("User", user.toString());

        if(ModulesActivity.queue == null)
        {
            ModulesActivity.queue = Volley.newRequestQueue(getApplicationContext());
        }
        String url = getResources().getString(R.string.api_address)+"/Module/GetModulesForUser";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            Map apiResponse = ModulesActivity.toMap(new JSONObject(response));
                            if (apiResponse.get("status").toString().equals("success"))
                            {
                                Log.v("modules:", apiResponse.get("modules").toString());

                                List<Object> modules = (ArrayList)apiResponse.get("modules");
                                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                RecyclerView.Adapter mAdapter = new ModuleAdapter(modules);
                                recyclerView.setAdapter(mAdapter);
                            }
                            else
                            {
                                Log.v("error:", apiResponse.get("message").toString());
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.v("Error:", ex.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response is:", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
               // params.put("User_ID", Integer.toString(user.User_ID));
                params.put("User_ID", Integer.toString(user.User_ID));
                params.put("ForApp", "true");
                return params;
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ModulesActivity.queue.add(stringRequest);
            }
        }, 200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(!(database.userDAO().getAllUsers().isEmpty())){
            database.userDAO().removeAllUsers();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(ModulesActivity.this,LoginActivity.class);
                    ModulesActivity.this.startActivity(mainIntent);
                    ModulesActivity.this.finish();
                }
    },1000);
        }
        return true;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext())
        {
            String key = keysItr.next();
            Object value = object.get(key);
            if(value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }
            else if (value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array)
    {
        List <Object> list = new ArrayList<Object>();
        try
        {
            for(int i = 0; i < array.length(); i++)
            {
                Object value = array.get(i);
                if(value instanceof JSONArray)
                {
                    value = toList((JSONArray) value);
                }
                else if(value instanceof JSONObject)
                {
                    value = toMap((JSONObject) value);
                }
                list.add(value);
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception", ex.getMessage());
        }
        return list;

    }
}