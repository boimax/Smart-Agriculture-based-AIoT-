package com.project.iotdashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {


    TextView txtTemp, txtHumi;
    ToggleButton btnPump, btnFan;
    private MQTTHelper mqttHelper;
    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment sensor.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqttHelper = new MQTTHelper(this.getContext(), new String[]{"thaotran/feeds/sensor1", "thaotran/feeds/sensor2", "thaotran/feeds/actuator1", "thaotran/feeds/actuator2"});
        //Lambda instruction or Asynchronous instruction
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " *** " + message.toString());
                if (topic.contains("sensor1")) {
                    txtTemp.setText(message.toString());
                }
                else if (topic.contains("sensor2")) {
                    txtHumi.setText(message.toString());
                }
                else if (topic.contains("actuator1")) {
                    if(message.toString().equals("1")) {
                        btnPump.setChecked(true);
                    }
                    else
                        btnPump.setChecked(false);
                }
                else if (topic.contains("actuator2")) {
                    if(message.toString().equals("1")) {
                        btnFan.setChecked(true);
                    }
                    else
                        btnFan.setChecked(false);
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    @Override
    public void onViewCreated(View view,Bundle bundle){
        txtTemp = view.findViewById(R.id.txtTemperature);
        txtHumi = view.findViewById(R.id.txtHumidity);
        btnPump = view.findViewById(R.id.btnPUMP);
        btnFan = view.findViewById(R.id.btnFAN);
        btnPump.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mqttHelper.publish("thaotran/feeds/actuator1", "1");
            }
            else {
                mqttHelper.publish("thaotran/feeds/actuator1", "0");
            }
        });

        btnFan.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mqttHelper.publish("thaotran/feeds/actuator2", "1");
            }
            else {
                mqttHelper.publish("thaotran/feeds/actuator2", "0");
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }



}