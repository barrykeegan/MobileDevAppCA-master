package ie.dbs.mobileappca;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import static java.util.stream.Collectors.toList;

public class LoginActivity extends AppCompatActivity {

    Broadcast br = new Broadcast();
    private Button SubmitButton;
    public EditText Email;
    public EditText Password;
    public TextView mResult;
    public static RequestQueue queue;
    public AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SubmitButton = findViewById(R.id.submit);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);


        SubmitButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(LoginActivity.queue == null)
                {
                    LoginActivity.queue = Volley.newRequestQueue(getApplicationContext());
                }
                String url = getResources().getString(R.string.api_address)+"/User/Login";
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                try
                                {
                                    Map loginResponse = LoginActivity.toMap(new JSONObject(response));
                                    if (loginResponse.get("status").toString().equals("success"))
                                    {
                                        Log.v("user:", loginResponse.get("user").toString());

                                        Map loginUser = (HashMap)loginResponse.get("user");
                                        database = AppDatabase.getDatabase(getApplicationContext());


                                        String id = loginUser.get("User_ID").toString();
                                        String name = loginUser.get("FullName").toString();
                                        String email_1 =loginUser.get("Email").toString();
                                        String username = loginUser.get("Username").toString();
                                        String password_1 = loginUser.get("Password").toString();
                                        String userType = loginUser.get("User_Type").toString();
                                        String avatar = loginUser.get("Avatar").toString();
                                        String createDate = loginUser.get("DateCreated").toString();
                                        String lastLogin = loginUser.get("LastLogin").toString();
                                        String active = loginUser.get("Active").toString();

                                        User user = new User(Integer.parseInt(id), name, email_1, username, password_1, userType, avatar, createDate, lastLogin, Integer.parseInt(active));
                                        database.userDAO().addUser(user);

                                        Intent intent = new Intent(LoginActivity.this, ModulesActivity.class);
                                        intent.putExtra("Name",  name);
                                        intent.putExtra("UserID", id );
                                        startActivity(intent);


                                    }
                                    else
                                    {
                                        Log.v("error", loginResponse.get("message").toString());
                                        Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.v("Error:", "Error Creating JSON object");
                                    mResult.setText("Error Creating JSON object");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Response is:", "That didn't work");

                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", Email.getText().toString());
                        params.put("password", Password.getText().toString());
                        return params;
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoginActivity.queue.add(stringRequest);
                    }
                }, 200);
            }
        });




    }

    @Override
    protected  void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(br,filter);
    }

    @Override
    protected  void onStop(){
        super.onStop();
        unregisterReceiver(br);
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
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
            map.put(key,value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array)
    {
        List<Object> list = new ArrayList<Object>();
        try
        {
            for(int i = 0; i<array.length(); i++)
            {
                Object value = array.get(i);
                if(value instanceof  JSONArray)
                {
                    value = toList((JSONArray) value);
                }
                else if (value instanceof  JSONObject)
                {
                    value = toMap((JSONObject) value);
                }
                list.add(value);
            }
        }
        catch (Exception e)
        {
            Log.e("Exception", e.getMessage());
        }
        return list;
    }

}


