package ie.dbs.mobileappca;

import android.content.Intent;
import android.os.Handler;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.arch.persistence.room.Dao;

public class Splash extends AppCompatActivity {

    public AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        database = AppDatabase.getDatabase(getApplicationContext());


        if(database.userDAO().getAllUsers().isEmpty()){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent mainIntent = new Intent(Splash.this, LoginActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();


                }
            }, 1000);
        }

        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(Splash.this,ModulesActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
            }, 1000);
        }
    }
}
