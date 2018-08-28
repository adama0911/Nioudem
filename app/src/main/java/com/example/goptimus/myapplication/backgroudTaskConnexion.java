package com.example.goptimus.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

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

public class backgroudTaskConnexion extends AsyncTask<String, String, String> {

    Context context;
    AlertDialog alertDialog;
    // AlertDialog alertDialog;

    public backgroudTaskConnexion (Context c){
        context =   c;
    }

    @Override
    protected String doInBackground(String... voids) {
        String u    =   "http://10.0.2.2/nioudemBackend/test.php";
        String result   =   "";

        try {
            URL url =   new URL(u);
            HttpURLConnection httpURLConnection =   (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream   =   httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter   =   new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String post_data    = URLEncoder.encode("nom","UTF-8")+"="+URLEncoder.encode("nom","UTF-8")+"&"+
                    URLEncoder.encode("nom","UTF-8")+"="+URLEncoder.encode("nom","UTF-8");
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

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}