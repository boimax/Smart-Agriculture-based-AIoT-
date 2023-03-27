package com.project.iotdashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantHealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantHealthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    TextView healthPlant;
    ImageView imgPlant;
    private MQTTHelper mqttHelper;
    public PlantHealthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment plant_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlantHealthFragment newInstance(String param1, String param2) {
        PlantHealthFragment fragment = new PlantHealthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqttHelper = new MQTTHelper(this.getContext(), new String[]{"thaotran/feeds/plant-image", "thaotran/feeds/plant-health"});
        //Lambda instruction or Asynchronous instruction
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " *** " + message.toString());
                if (topic.contains("plant-health")) {
                    healthPlant.setText(message.toString());
                }
                else if (topic.contains("plant-image")) {
                    String strBase64 = message.toString();
                    byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imgPlant.setImageBitmap(decodedByte);
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plant_health, container, false);
    }
    @Override
    public  void onViewCreated(View view,Bundle bundle){
        imgPlant = view.findViewById(R.id.imageView6);
        healthPlant = view.findViewById(R.id.textView5);

    }
}