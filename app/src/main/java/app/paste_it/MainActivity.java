package app.paste_it;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

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
