package com.project.iotdashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
//    private NavHostFragment navHostFragment;
    private static final String tag1 = "tag1";
    private static final String tag2 = "tag2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                String chosen_tag = null;
                Fragment fragment = null;
                NavDirections action = null;
                switch (item.getItemId()) {
                    case R.id.menu_sensor:
//                        action = SensorFragmentDirections.actionSensorFragmentSelf();
                        action = PlantHealthFragmentDirections.actionPlantHealthFragmentToSensorFragment();
                        navController.navigate(action);
//                        fragment = fragmentManager.findFragmentByTag(tag1);
//                        if(fragment == null){
//                            Log.i("frag","creating frag");
//                            fragment = new SensorFragment();
//                        }
//                        chosen_tag = tag1;
                        break;
                    case R.id.menu_plant_health:
                        action = SensorFragmentDirections.actionSensorFragmentToPlantHealthFragment();
                        navController.navigate(action);
//                        fragment = fragmentManager.findFragmentByTag(tag2);
//                        if(fragment == null){
//                            fragment = new PlantHealthFragment();
//                        }
//                        chosen_tag = tag2;
                        break;
                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment,chosen_tag).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.menu_sensor);
    }
}