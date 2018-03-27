package com.example.andrei.ode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventFragmentT extends Fragment {

    public static long CurrID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.white_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (System.currentTimeMillis() / 1000 > Event.Events.get(EventFragment.CurrID).TimeBegin) {
            ((TextView) view.findViewById(R.id.textView4)).setText(String.valueOf(Event.Events.get(EventFragment.CurrID).Pa_count));
            ((TextView) view.findViewById(R.id.textView6)).setText(String.valueOf(Event.Events.get(EventFragment.CurrID).Pa_rating));
        } else {
            ((TextView) view.findViewById(R.id.textView4)).setText(String.valueOf(Event.Events.get(EventFragment.CurrID).Fu_count));
            ((TextView) view.findViewById(R.id.textView6)).setText(String.valueOf(Event.Events.get(EventFragment.CurrID).Fu_rating));
        }
    }
}