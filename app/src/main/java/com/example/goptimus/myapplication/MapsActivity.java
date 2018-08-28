package com.example.goptimus.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;


    AlertDialog.Builder builder;
    LayoutInflater layoutInflater;

    private static final String TAG = "HomeActivity";

    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    ;

    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;

    private FusedLocationProviderClient mfusedLocationProviderclient;

    private static final float DEFAULT_ZOOM = 17.0f;

    ListView    listView;

    ImageView   img_moto,   img_taxi,   img_bus;

    Location currentLocation    =   null;

    String  token  =    "44f39c384e76a2e02c7a92a98e45c6154efc836c76142225b27c29d121d1";

    String  locality = "";

    Geocoder geocoder    =  null;

    AlertDialog dialog;

    MsgAsyncTask    msgAsyncTask    =   null;

    GetResponseAsyncTask    getResponseAsyncTask    =   null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        builder = new AlertDialog.Builder(this);
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        img_moto    =   (ImageView) findViewById(R.id.imgMoto);
        img_taxi    =   (ImageView) findViewById(R.id.imgTaxi);
        img_bus   =   (ImageView) findViewById(R.id.imgBus);

        geocoder    =   new Geocoder(this);

        getLocationPermission();
        if (isServicesOK()) {
            init();
        }

        initMap();

        img_moto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog0();
            }
        });

        img_taxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog0 ();
            }
        });

        img_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_dialog0 ();
            }
        });


        msgAsyncTask    =   new MsgAsyncTask(this);

        getResponseAsyncTask    =   new GetResponseAsyncTask(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (mLocationPermissionsGranted) {
            getDiviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }else
        {
            builder.setTitle("Permission");
            builder.setMessage("permision no ");
            builder.create().show();
        }
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions  ");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            Log.d(TAG, "getLocationPermission:  location permissions is ok ");

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSIONS_REQUEST_CODE);
            Log.d(TAG, "getLocationPermission:  location permissions is not goot ");
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initialisy map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    private void getDiviceLocation() {
        Log.d(TAG, "getDiviceLocation: getting the divices current Location  ");

        mfusedLocationProviderclient = LocationServices.getFusedLocationProviderClient(this);
        try {
            final Task location = mfusedLocationProviderclient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: Found Location  is successful");
                        currentLocation = (Location) task.getResult();
                        if (currentLocation == null) {
                            Log.d(TAG, "onComplete: Location is null");
                        } else {
                            Log.d(TAG, "onComplete: Location isn't null");

                        }
                        moveCamerato(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                    } else {
                        Log.d(TAG, "onComplete: current location is null ");
                        Toast.makeText(getApplicationContext(), "unable to get current location", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.d(TAG, "getDiviceLocation: SecurityException,  " + e.getMessage());
        }
    }

    private void moveCamerato(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving to camera to lat:" + latLng.latitude + ", long: " + latLng.longitude);
        mMap.addMarker(new
                MarkerOptions().position(latLng).title("Sama position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //builder.setTitle("location");
        //builder.setMessage("moveCamera: moving to camera to lat:" + latLng.latitude + ", long: " + latLng.longitude);
        //builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: called ");

        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permision failled ");

                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permision granted ");

                    mLocationPermissionsGranted = true;
                }
            }

        }

    }

    public void init() {

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: service giigle play is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can fix it ");
            //Dialog dialog   =   GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this,available,ERROR_DIALOG_REQUEST);

            //dialog.show();
        } else {
            Toast.makeText(this, "We can\'t make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void onSearch(View view){
        EditText location_edit   =   (EditText) findViewById(R.id.location_edit);
        String  location    =   location_edit.getText().toString();
        List<Address> addressList = null;

        mMap.clear();

        if(location !=  null    &&  !location.equals("")){
            try {
                addressList =    geocoder.getFromLocationName(location,1);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Address address    =   addressList.get(0);
            locality    =   address.getSubLocality();
            LatLng  latLng  =   new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("my location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }

    public  void changeType (View   view){
        if(mMap.getMapType()    ==   GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void show_dialog0 (){
        View view = layoutInflater.inflate(R.layout.dialog0, null);
        // listView    =  (ListView) view.findViewById(R.id.custom_list);
        Button btnNow  =   (Button) view.findViewById(R.id.now);
        Button btnNext  =   (Button) view.findViewById(R.id.next);
        //listView.setAdapter(new CustomAdapter(this));
        builder.setView(view);

        final AlertDialog dialod = builder.create();

        btnNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialod.cancel();
                open_dialog1();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialod.cancel();
                open_dialog1_after();
            }
        });

        //Toast.makeText(this,""+btn.getId(),Toast.LENGTH_LONG);

        dialod.show();
    }



    public void open_dialog1(){

        View view = layoutInflater.inflate(R.layout.dialog1, null);
        final EditText destination    =   (EditText) view.findViewById(R.id.destination);
        Button btnVilider  =   (Button)    view.findViewById(R.id.validerDestiation);


        final String  nomstr  =   "Client";

        builder.setView(view);

        final AlertDialog dialod = builder.create();

        btnVilider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  end =   destination.getText().toString();
                String  begin   =   locality;
                String  time    =   "time";
                String  date    =   "date";

                msgAsyncTask.execute("http://10.0.2.2/nioudemBackend/index.php",begin,end,time,date, "2",nomstr);

                dialod.cancel();
                show_loader_dialog();
            }
        });

        dialod.show();
    }

    public void open_dialog1_after(){

            View view = layoutInflater.inflate(R.layout.dialog1_after, null);

            Button btnVilider  =   (Button)    view.findViewById(R.id.validerButton);
            final EditText nomEdit    =   (EditText)  view.findViewById(R.id.nomEdit);
            final EditText prenomEdit    =   (EditText)  view.findViewById(R.id.prenomEdit);
            final EditText departEdit    =   (EditText)  view.findViewById(R.id.departEdit);
            final EditText heureEdit    =   (EditText)  view.findViewById(R.id.heureEdit);
            final EditText destinationEdit    =   (EditText)  view.findViewById(R.id.departEdit);
            final EditText jourEdit    =   (EditText)  view.findViewById(R.id.jourEdit);

            builder.setView(view);
            dialog = builder.create();

            btnVilider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("[TAG]================+>","Hey le geek");

                    String  nomstr   =  nomEdit.getText().toString() + " " + prenomEdit.getText().toString();
                    String begin     =  departEdit.getText().toString();
                    String end       =  destinationEdit.getText().toString();
                    String time     =   heureEdit.getText().toString();
                    String date      =  jourEdit.getText().toString();

                    msgAsyncTask.execute("http://10.0.2.2/nioudemBackend/index.php",begin,end,time,date, "2",nomstr);

                    dialog.cancel();

                    show_loader_dialog();

                }
            });


            dialog.show();
    }

    public void open_dialog2(JSONObject jsonObj) throws JSONException {
        View view = layoutInflater.inflate(R.layout.dialog2, null);
        listView    =  (ListView) view.findViewById(R.id.custom_list);

        listView.setAdapter(new CustomAdapter(this,jsonObj));
        builder.setView(view);

        AlertDialog dialod = builder.create();
        dialod.show();
    }

    public void show_loader_dialog(){
        View view = layoutInflater.inflate(R.layout.loaderwaiting, null);
        // listView    =  (ListView) view.findViewById(R.id.custom_list);
        //listView.setAdapter(new CustomAdapter(this));
        builder.setView(view);

        final AlertDialog dialod = builder.create();

        //Toast.makeText(this,""+btn.getId(),Toast.LENGTH_LONG);
        dialod.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialod.show();

        //temporary code  ------------------------------------------------
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("tag", "This'll run 300 milliseconds later");
                        dialod.cancel();

                        getResponseAsyncTask.execute("http://10.0.2.2/nioudemBackend/index.php","Grand yoff","23.4","-23");

                    }
                },
                1000);
        //temporary code  ------------------------------------------------
    }


    /*============================================================================
                                       Clients Http
    =================================================================================*/


    public class MsgAsyncTask extends AsyncTask<String, String, String> {

        Context context;
        AlertDialog alertDialog;
        // AlertDialog alertDialog;

        public MsgAsyncTask (Context c){
            context =   c;
        }

        @Override
        protected String doInBackground(String... voids) {
            String u    =   voids[0];
            String  begin = voids[1];
            String  end   = voids[2];
            String  time  = voids[3];
            String  date  = voids[4];
            String  typeMsg  = voids[5];
            String  myName   =   voids[6];

            String  about  = "about";

            double lat   =   0;
            double lng   =   0;

            if(currentLocation != null){
                lat   =   currentLocation.getLatitude();
                lng   =   currentLocation.getLongitude();
            }

            String result   =   "";

            try {

                String  msg =   "je me traouve : police à proximité de Grand Yoff, Dakar, et  je voudrais le rendre a/au nord foir";
                //String  begin   =   "police à proximité de Grand Yoff, Dakar";
                //String  end     =   "nord foir";
                //double  lat   =   14.717658;
                //double  lng   =   -17.455792;
                //String  time    =    "12:00:00";
                //String  date    =   "2018-08-13";
                //int     typeMsg     =   1;

                String     typeUser    =  "1";


                URL url =   new URL(u);
                HttpURLConnection httpURLConnection =   (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream   =   httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter   =   new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data    =
                        URLEncoder.encode("msg","UTF-8")+"="+URLEncoder.encode(msg,"UTF-8")+"&"+
                        URLEncoder.encode("begin","UTF-8")+"="+URLEncoder.encode(begin,"UTF-8")+"&"+
                        URLEncoder.encode("end","UTF-8")+"="+URLEncoder.encode(end,"UTF-8")+"&"+
                                URLEncoder.encode("latitude","UTF-8")+"="+lat+"&"+
                                URLEncoder.encode("longitude","UTF-8")+"="+lng+"&"+
                                URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"+
                                URLEncoder.encode("date","UTF-8")+"="+URLEncoder.encode(date,"UTF-8")+"&"+
                                URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(myName,"UTF-8")+"&"+
                                URLEncoder.encode("typeUser","UTF-8")+"="+ typeUser +"&"+
                                URLEncoder.encode("typeMsg","UTF-8")+"="+ typeMsg +"&"+
                                URLEncoder.encode("about","UTF-8")+"="+ about +"&"+
                                URLEncoder.encode("token","UTF-8")+"="+ token +"&"+
                                URLEncoder.encode("request","UTF-8")+"="+ 1 +"&"+
                                URLEncoder.encode("operation","UTF-8")+"="+ 2;

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream =   httpURLConnection.getInputStream();
                BufferedReader bufferedReader   =   new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line =   "";
                while ((line  =   bufferedReader.readLine())!= null ){
                    result +=   line;
                }

                bufferedReader.close();
                inputStream.close();
                return  result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Résultat","===================+++++++++>"+result);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public class GetResponseAsyncTask extends AsyncTask<String, String, String> {

        Context context;
        AlertDialog alertDialog;
        // AlertDialog alertDialog;

        public GetResponseAsyncTask (Context c){
            context =   c;
        }

        @Override
        protected String doInBackground(String... voids) {
            String u    =   voids[0];

            String result   =   "";

            try {

                URL url =   new URL(u);
                HttpURLConnection httpURLConnection =   (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream   =   httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter   =   new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data    =
                                URLEncoder.encode("token","UTF-8")+"="+ token +"&"+
                                URLEncoder.encode("request","UTF-8")+"="+ 1 +"&"+
                                URLEncoder.encode("operation","UTF-8")+"="+ 1;

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream =   httpURLConnection.getInputStream();
                BufferedReader bufferedReader   =   new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line =   "";
                while ((line  =   bufferedReader.readLine())!= null ){
                    result +=   line;
                }

                bufferedReader.close();
                inputStream.close();
                return  result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
           //builder.setTitle("Message");
            try {
                JSONObject jsonObj = new JSONObject(result);
                //String[] str = {jsonObj.getString("begin") ,jsonObj.getString("begin"),jsonObj.getString("begin")};
                open_dialog2(jsonObj);
                Log.d("onPostExecute","{}=========================x=>" + jsonObj.getString("begin"));
                //builder.setMessage(result);
                //builder.setMessage(jsonObj.getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
                //builder.setMessage(e.getMessage());
            }

            //builder.create().show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public class DestinationAfterAsyncTask extends AsyncTask<String, String, String> {

        Context context;
        AlertDialog alertDialog;
        // AlertDialog alertDialog;

        public DestinationAfterAsyncTask (Context c){
            context =   c;
        }

        @Override
        protected String doInBackground(String... voids) {
            String u    =   voids[0];
            String  destination =   voids[1];
            String depart       =   voids[2];
            String heure        =   voids[3];
            String  nom         =   voids[3];

            String result   =   "";

            try {
                URL url =   new URL(u);
                HttpURLConnection httpURLConnection =   (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream   =   httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter   =   new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data    = URLEncoder.encode("destination","UTF-8")+"="+URLEncoder.encode(destination,"UTF-8")+"&"+
                        URLEncoder.encode("depart","UTF-8")+"="+URLEncoder.encode(depart,"UTF-8")+"&"+
                        URLEncoder.encode("heure","UTF-8")+"="+URLEncoder.encode(heure,"UTF-8")+"&"+
                        URLEncoder.encode("nom","UTF-8")+"="+URLEncoder.encode(nom,"UTF-8")+"&"+
                        URLEncoder.encode("request","UTF-8")+"=1"+"&"+
                        URLEncoder.encode("operation","UTF-8")+"=1"+"&"+
                        URLEncoder.encode("token","UTF-8")+"="+ token;

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream =   httpURLConnection.getInputStream();
                BufferedReader bufferedReader   =   new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line =   "";
                while ((line  =   bufferedReader.readLine())!= null ){
                    result +=   line;
                }

                bufferedReader.close();
                inputStream.close();
                return  result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            builder.setTitle("Message");
            try {
                JSONObject reader = new JSONObject(result);
                builder.setMessage(reader.getInt("id"));

            } catch (JSONException e) {
                e.printStackTrace();
                builder.setMessage(e.getMessage());
            }

            builder.create().show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }



}
