package com.example.android.walkmyandroid;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    private OnTaskCompleted mListener;


    FetchAddressTask(Context applicationContext, OnTaskCompleted listener){
        mContext = applicationContext;
        mListener = listener;
    }



    @Override
    protected String doInBackground(Location... locations) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Location location = locations[0];
        List<Address> addresses = null;
        String resultMessage = "";
        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1
            );
            if(addresses == null || addresses.size() == 0){
                if(resultMessage.isEmpty()){
                    resultMessage = "No address found";
                    Log.e(TAG, resultMessage);
                }
            }
            else{
                Address address = addresses.get(0);
                ArrayList<String> addressParts = new ArrayList<>();

                for(int i=0; i <= address.getMaxAddressLineIndex(); i++){
                    addressParts.add(address.getAddressLine(i));
                }

                resultMessage = TextUtils.join("\n", addressParts);
            }
        }catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = "Service not available";
            Log.e(TAG, resultMessage, ioException);
        }catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage ="Invalid coordinates were supplied to the Geocoder" + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude();

            Log.e(TAG, resultMessage, illegalArgumentException);
        }
        return resultMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onTaskCompleted(s);
        super.onPostExecute(s);

    }

    interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }
}
