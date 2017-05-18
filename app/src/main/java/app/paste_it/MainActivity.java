package app.paste_it;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //This boolean will be set to true once a sync operation has completed.
    //This is being done to prevent starting a service every time the activity
    //is recreated.
    public static boolean PASTES_SYNCED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if(savedInstanceState==null){
            if(getResources().getBoolean(R.bool.sw600dp)){

            }
            else{
                Fragment fragment = MainFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
            }
        }
    }
}
