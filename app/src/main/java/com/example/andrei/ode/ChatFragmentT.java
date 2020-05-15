package com.example.andrei.ode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.andrei.ode.ChatFragment.choose_distance;
import static com.example.andrei.ode.ChatFragment.ws;

public class ChatFragmentT extends Fragment {

    public static long CurrID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.white_chat, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((SeekBar)view.findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                choose_distance = progress;
                switch (ChatFragment.choose_distance) {
                    case 0:
                        ((TextView)view.findViewById(R.id.textView3)).setText("10 м");
                        ChatFragment.choosen_distance = 10;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(10));
                        }
                        break;
                    case 1:
                        ((TextView)view.findViewById(R.id.textView3)).setText("50 м");
                        ChatFragment.choosen_distance = 10;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(50));
                        }
                        break;
                    case 2:
                        ((TextView)view.findViewById(R.id.textView3)).setText("100 м");
                        ChatFragment.choosen_distance = 50;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(100));
                        }
                        break;
                    case 3:
                        ((TextView)view.findViewById(R.id.textView3)).setText("250 м");
                        ChatFragment.choosen_distance = 250;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(250));
                        }
                        break;
                    case 4:
                        ((TextView)view.findViewById(R.id.textView3)).setText("1000 м");
                        ChatFragment.choosen_distance = 1000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(1000));
                        }
                        break;
                    case 5:
                        ((TextView)view.findViewById(R.id.textView3)).setText("5000 м");
                        ChatFragment.choosen_distance = 5000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(5000));
                        }
                        break;
                    case 6:
                        ((TextView)view.findViewById(R.id.textView3)).setText("20 км");
                        ChatFragment.choosen_distance = 20000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(20000));
                        }
                        break;
                    case 7:
                        ((TextView)view.findViewById(R.id.textView3)).setText("100 км");
                        ChatFragment.choosen_distance = 100000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(100000));
                        }
                        break;
                    case 8:
                        ((TextView)view.findViewById(R.id.textView3)).setText("500 км");
                        ChatFragment.choosen_distance = 500000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(500000));
                        }
                        break;
                    case 9:
                        ((TextView)view.findViewById(R.id.textView3)).setText("1000 км");
                        ChatFragment.choosen_distance = 1000000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(1000000));
                        }
                        break;
                    case 10:
                        ((TextView)view.findViewById(R.id.textView3)).setText("Планета");
                        ChatFragment.choosen_distance = 50000000;
                        if (ws != null && ws.isOpen()) {
                            ws.sendBinary(ChatFragment.toByteArray(50000000));
                        }
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void change_distance() {
    }
}