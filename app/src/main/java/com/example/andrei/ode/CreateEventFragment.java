package com.example.andrei.ode;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateEventFragment extends Fragment {

    EditText txtTimeBeginD, txtTimeEndD, txtTimeBeginT, txtTimeEndT, txtPlace;
    ConstraintLayout PickPlace, btnDone;
    Spinner spinner;
    int DIALOG_DATE = 1;
    int myYearB = 2011;
    int myMonthB = 02;
    int myDayB = 03;
    int myHourB = 0;
    int myMinuteB = 0;
    int myYearE = 2011;
    int myMonthE = 02;
    int myDayE = 03;
    int myHourE = 0;
    int myMinuteE = 0;
    static public String[] data = {"Спорт", "Театр", "Кино", "Скидки", "Прочее", "Музей", "Лекция", "Ярмарка", "Концерт"};

    boolean isBegin = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myYearB = Calendar.getInstance().get(Calendar.YEAR);
        myMonthB = Calendar.getInstance().get(Calendar.MONTH);
        myDayB = Calendar.getInstance().get(Calendar.DATE);
        myYearE = Calendar.getInstance().get(Calendar.YEAR);
        myMonthE = Calendar.getInstance().get(Calendar.MONTH);
        myDayE = Calendar.getInstance().get(Calendar.DATE);

        btnDone = (ConstraintLayout) view.findViewById(R.id.btnDone);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        txtPlace = (EditText) view.findViewById(R.id.place);
        PickPlace = (ConstraintLayout) view.findViewById(R.id.btnPickPlace);
        txtTimeBeginD = (EditText) view.findViewById(R.id.timeBeginD);
        txtTimeEndD = (EditText) view.findViewById(R.id.timeEndD);
        txtTimeBeginT = (EditText) view.findViewById(R.id.timeBeginT);
        txtTimeEndT = (EditText) view.findViewById(R.id.timeEndT);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.MainContext, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Выберите категорию");

        PickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.isShow = false;
                startActivityForResult(new Intent(MainActivity.MainContext, MapActivity.class), 9090);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        String s = "";
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.set(myYearB, myMonthB-1, myDayB, myHourB, myMinuteB);
                            Calendar cal1 = Calendar.getInstance();
                            cal1.set(myYearE, myMonthE-1, myDayE, myHourE, myMinuteE);

                            long ccc = cal.getTimeInMillis();
                            long ddd = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();

                            JSONObject jsonobj = new JSONObject();
                            jsonobj.put("name", (((EditText) view.findViewById(R.id.Name)).getText().toString()));
                            jsonobj.put("time", ((cal.getTimeInMillis()) / 1000));
                            jsonobj.put("time_end", ((cal1.getTimeInMillis()) / 1000));
                            jsonobj.put("longitude", String.valueOf(MapActivity.Lng));
                            jsonobj.put("latitude", String.valueOf(MapActivity.Lat));
                            jsonobj.put("s_description", (((EditText) view.findViewById(R.id.s_desc)).getText().toString()));
                            jsonobj.put("l_description", (((EditText) view.findViewById(R.id.l_desc3)).getText().toString()));
                            jsonobj.put("cost", (((EditText) view.findViewById(R.id.s_desc2)).getText().toString().equals("") ? 0 : Long.parseLong(((EditText) view.findViewById(R.id.s_desc2)).getText().toString())));
                            jsonobj.put("type", spinner.getSelectedItemPosition());
                            String request = MainActivity.Domain + "/addevent?" +
                                    "main_id=" + MainActivity.MyID
                                    + "&token=" + MainActivity.MyToken
                                    + "&request=" + URLEncoder.encode(jsonobj.toString());

                            s = MainActivity.doGet(request);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return s;
                    }

                    @Override
                    protected void onPostExecute(final String result) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            if (jsonObject.has("error")) {
                                MainActivity.ToastError(result);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        try {
                            EventFragment.CurrID = jsonObject.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MainActivity.isMainFragment = false;

                        Fragment fragment = null;

                        Class fragmentClass = EventFragment.class;
                        Class fragmentTollBar = EventFragmentT.class;
                        FragmentManager fragmentManager = MainActivity.This.getSupportFragmentManager();

                        try {
                            fragment = (Fragment) fragmentTollBar.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(MainActivity.toolbar.getId(), fragment).commit();

                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                    }
                }.execute();
            }
        });

        txtTimeBeginD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBegin = true;
                new DatePickerDialog(MainActivity.MainContext, myCallBackD,
                        myYearB,
                        myMonthB,
                        myDayB)
                        .show();
            }
        });
        txtTimeBeginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBegin = true;
                new TimePickerDialog(MainActivity.MainContext, myCallBackT,
                        myHourB,
                        myMinuteB,
                        true)
                        .show();
            }
        });

        txtTimeEndD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBegin = false;
                new DatePickerDialog(MainActivity.MainContext, myCallBackD,
                        myYearE,
                        myMonthE,
                        myDayE)
                        .show();
            }
        });
        txtTimeEndT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBegin = false;
                new TimePickerDialog(MainActivity.MainContext, myCallBackT,
                        myHourE,
                        myMinuteE,
                        true)
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9090) {
            txtPlace.setText(MapActivity.Adress);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    DatePickerDialog.OnDateSetListener myCallBackD = new DatePickerDialog.OnDateSetListener() {

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (isBegin) {
                myYearB = year;
                myMonthB = monthOfYear + 1;
                myDayB = dayOfMonth;
                txtTimeBeginD.setText((myDayB < 10 ? "0" + myDayB : myDayB) + "." + (myMonthB < 10 ? "0" + myMonthB : myMonthB) + "." + myYearB);
            } else {
                myYearE = year;
                myMonthE = monthOfYear + 1;
                myDayE = dayOfMonth;
                txtTimeEndD.setText((myDayE < 10 ? "0" + myDayE : myDayE) + "." + (myMonthE < 10 ? "0" + myMonthE : myMonthE) + "." + myYearE);
            }
        }
    };
    TimePickerDialog.OnTimeSetListener myCallBackT = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (isBegin) {
                myHourB = hourOfDay;
                myMinuteB = minute;
                txtTimeBeginT.setText((myHourB < 10 ? "0" + myHourB : myHourB) + ":" + (myMinuteB < 10 ? "0" + myMinuteB : myMinuteB) +
                        " (" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + ")");
            } else {
                myHourE = hourOfDay;
                myMinuteE = minute;
                txtTimeEndT.setText((myHourE < 10 ? "0" + myHourE : myHourE) + ":" + (myMinuteE < 10 ? "0" + myMinuteE : myMinuteE) +
                        " (" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + ")");
            }
        }
    };
}