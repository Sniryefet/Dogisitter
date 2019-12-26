package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTripActivity extends AppCompatActivity {

    private Button mBackButton;
    private EditText mTripName;
    private EditText mMeetingPlace;
    private EditText mDate;
    private EditText mTime;
    private EditText mDuration;
    private EditText mCapacity;
    private EditText mDescription;

    private FirebaseDatabase mPersonalTripRef;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mTripName= findViewById(R.id.trip_name);
        mMeetingPlace=findViewById(R.id.Meeting_Place);
        mDate=findViewById(R.id.Date);
        mTime=findViewById(R.id.Time);
        mDuration=findViewById(R.id.Duration);
        mCapacity=findViewById(R.id.Capacity);
        mDescription=findViewById(R.id.Description);


        mDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddTripActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String a = "" + selectedMinute;
                        String b = "" + selectedHour;
                        if(selectedMinute<10){
                            a = "0"+selectedMinute;
                        }
                        if(selectedHour<10){
                            b = "0"+selectedHour;
                        }
                        mTime.setText( b + ":" + a);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }

    //onClick
    public void createTrip(View v){
        attemptTripAddition();
    }
    //check if all the necessary data was given
    private void attemptTripAddition(){
        String tripName=mTripName.getText().toString();
        String meetingPlace=mMeetingPlace.getText().toString();
        String date= mDate.getText().toString();
        String time = mTime.getText().toString();
        String duration= mDuration.getText().toString();
        String capacity = mCapacity.getText().toString();
        String description = mDescription.getText().toString();
        Log.d("Snir",tripName + ","+meetingPlace+","+date+","+time);
        //The necessary fields are  meeting place, date, time
        //other fields will have a default value
        if(tripName==null || tripName=="") tripName = "";
        if(capacity==null || capacity=="") capacity = "5";

        boolean valid = true;
        View focusView= null;

        mMeetingPlace.setError(null);
        mDate.setError(null);
        mTime.setError(null);
        mDuration.setError(null);


        if(meetingPlace==null || meetingPlace.equals("")){
            Log.d("myBalls","meeting place is null or empty string");
            mMeetingPlace.setError("Must enter meeting place");
            focusView=mMeetingPlace;
            valid = false;
        }
        if (!dateValidation(date)){
            Log.d("myBalls","date is null or empty string");
            mDate.setError("Date isn't valid . should be in format dd/mm/yyyy");
            focusView=mDate;
            valid= false;
        }
        if(!timeValidation(time)){
            Log.d("myBalls","time is null or empty string");

            mTime.setError("Time isn't valid . should be in format  xx:xx");
            focusView=mTime;
            valid=false;
        }

        if(duration.equals("")||!durationValidation(Integer.parseInt(duration))){
            Log.d("myBalls","duration is null or empty string");
            mDuration.setError("Duration isn't valid . should be between 30 minutes to 300 minutes (5hours)  ");
            focusView=mDate;
            valid= false;
        }

        if (!valid) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        }else{
            String userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            Trip newTrip = new Trip(tripName,meetingPlace,date,time,duration,capacity,description,userID);
            addTrip(newTrip);

        }

    }



    //onClick
    //return to the admin home page
    public void back(View v){
        Intent intent = new Intent(AddTripActivity.this,AdminMainActivity.class);
        finish();
        startActivity(intent);
    }

    private void addTrip(Trip newTrip){
        String userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        //LoginActivity.uInfo.addTrip(newTrip);
        String tripId = FirebaseDatabase.getInstance().getReference("Trips").push().getKey();
        Log.d("TESTTEST", "KeyId: "+ tripId);
        FirebaseDatabase.getInstance().getReference("Trips").child(tripId)
                .setValue(newTrip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){

                }else{

                }
            }
        });
        FirebaseDatabase.getInstance().getReference("AdminTrips").child(userID).child(tripId)
                .setValue(newTrip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){

                }else{

                }
            }
        });
    }


    //checks if the date entered is valid date
    private boolean dateValidation(String userDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(userDate);
            System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    //checks if the time entered is valid time between 00:00 - 23:59
    private boolean timeValidation(String time){
        if(! time.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")){
            return false;
        }
        return true;
    }

    //checks if the duration is valid number and doe't not
    private boolean durationValidation(int duration){
        return duration < 300 && duration >10;
    }

    //generic error message box;
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        mDate.setText(sdf.format(myCalendar.getTime()));
    }
}