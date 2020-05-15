package com.example.andrei.ode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


public class ChatFragment extends Fragment {


    public static double CurrLatitude = 0, CurrLongitude = 0;
    public static ScrollView ScrollChat;
    public static LinearLayout Chat;
    public static int choose_distance = 3;
    public static double choosen_distance = 250;
    boolean isSwiping = true;
    public static WebSocket ws;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.This);

        mLocationManager = (LocationManager) MainActivity.This.getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,
                2, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                2, mLocationListener);


        Chat = view.findViewById(R.id.linear);
        ScrollChat = (ScrollView) view.findViewById(R.id.scrollChat);
        ScrollChat.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (ScrollChat != null) {
                    if (ScrollChat.getChildAt(0).getBottom() <= (ScrollChat.getHeight() + ScrollChat.getScrollY())) {
                        isSwiping = true;
                    } else {
                        isSwiping = false;
                    }
                }
            }
        });
        ((ImageView) view.findViewById(R.id.imageView11)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebSocket ss = ws.sendBinary(concate(toByteArray(CurrLatitude), toByteArray(CurrLongitude),
                        ((TextView) view.findViewById(R.id.editText3)).getText().toString().getBytes()));
                byte[] gg = ((TextView) view.findViewById(R.id.editText3)).getText().toString().getBytes();
                Log.d("WS", String.valueOf(((TextView) view.findViewById(R.id.editText3)).getText().toString().getBytes()));
                ((TextView) view.findViewById(R.id.editText3)).setText("");
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            CurrLatitude = location.getLatitude();
            CurrLongitude = location.getLongitude();
            if (ws != null && ws.isOpen()) {
                ws.sendBinary(concate(toByteArray(CurrLatitude), toByteArray(CurrLongitude)));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static byte[] concate(byte[]... arrays) {
        int size = 0;
        for (byte[] b : arrays) {
            size += b.length;
        }
        byte[] done = new byte[size];

        size = 0;
        for (byte[] bs : arrays) {
            for (byte b : bs) {
                done[size] = b;
                size++;
            }
        }
        return done;
    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getLong();
    }

    public void connectToChat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ws = new WebSocketFactory().createSocket("ws://139.99.98.213/ws?main_id=" + String.valueOf(MainActivity.MyID) + "&token="
                            + URLEncoder.encode(MainActivity.MyToken, "utf-8"));
                    ws.addListener(new SoketListner());
                    ws.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
                    ws.setPingInterval(5000);
                    ws.connectAsynchronously();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ws == null || !ws.isOpen()) {
            connectToChat();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ws.isOpen())
            ws.sendClose();
    }

    private class SoketListner extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            //super.onConnected(websocket, headers);
            Log.d("WS", "CONNECTED");
            ws.sendBinary(toByteArray(choosen_distance));
            if (ActivityCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            CurrLatitude = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            CurrLongitude = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            ws.sendBinary(concate(toByteArray(CurrLatitude), toByteArray(CurrLongitude)));
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            //super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Log.d("WS", "DISCONNECTED");
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            //super.onTextMessage(websocket, text);
            Log.d("WS", text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            //super.onBinaryMessage(websocket, binary);
            ByteBuffer wrapped = ByteBuffer.wrap(binary);


            String message = new String(Arrays.copyOfRange(binary, 8, binary.length));
            long id = bytesToLong(Arrays.copyOfRange(binary, 0, 8));
            final TextView btn = new TextView(MainActivity.This);
            btn.setText(message);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (id == MainActivity.MyID) {
                llp.setMargins(64, 8, 8, 8);
                llp.gravity = Gravity.END;
                btn.setLayoutParams(llp);
                btn.setBackgroundResource(R.drawable.bubble_i);
            } else {
                llp.setMargins(8, 8, 64, 8);
                llp.gravity = Gravity.START;
                btn.setLayoutParams(llp);
                btn.setBackgroundResource(R.drawable.bubble_t);
            }
            btn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            MainActivity.This.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Chat.addView(btn);
                    if (isSwiping)
                        ScrollChat.post(new Runnable() {
                            @Override
                            public void run() {
                                ScrollChat.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                }
            });
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            //super.onConnectError(websocket, exception);
            Log.d("WS", "ConnectError");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            //super.onError(websocket, cause);
            Log.d("WS", cause.getMessage());
        }
    }
}