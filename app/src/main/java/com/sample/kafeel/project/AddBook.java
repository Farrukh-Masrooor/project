package com.sample.kafeel.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import static android.app.Activity.RESULT_OK;


public class AddBook extends Fragment implements View.OnClickListener{

    EditText editText1, editText2;
    Button button;
    ImageView imageView;
    SharedPreferences settings;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.sample.kafeel.project.R.layout.add_book_layout, container, false);
        editText1 = view.findViewById(com.sample.kafeel.project.R.id.book_name);
        editText2 = view.findViewById(com.sample.kafeel.project.R.id.book_des);
        imageView=view.findViewById(com.sample.kafeel.project.R.id.image_book);
        settings = getActivity().getSharedPreferences("MyPrefsFile", 0);
        button = view.findViewById(com.sample.kafeel.project.R.id.addBookButtoon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        button.setOnClickListener(this);
        int s = settings.getInt("user_id", 0);
        Log.d("My_log", "id" + s);
        return view;
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onClick(View view) {
        String s1=editText1.getText().toString();
        String s2=editText2.getText().toString();
        if(!s1.equals("")&&!s2.equals("")){
        LoadData data=new LoadData();
        data.execute(s1,s2);

        editText1.setText("");
        editText2.setText("");}
        else
        Toast.makeText(getActivity(),"fields are empty",Toast.LENGTH_SHORT).show();

    }

    class LoadData extends AsyncTask {

        String url = "http://192.168.56.1/android_connect/insertBookDetails.php";

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d("My_log", "in async");

            String name = objects[0].toString();
            String des = objects[1].toString();
            int s = settings.getInt("user_id", 0);
            String result = getResult(name, des,s);
            Log.d("My_log", "in backgrund" + result);
            return result;
        }

        public String getResult(String name, String des,int i) {

            // Log.v(TAG, "Final Requsting URL is : :"+url);

            String line = "";
            String responseData = null;
            String s= String.valueOf(i);
            try {
                StringBuilder sb = new StringBuilder();
                String x = "";
                URL httpurl = new URL(url);
                //URLConnection tc= httpurl.openConnection();
                HttpURLConnection connection = (HttpURLConnection) httpurl.openConnection();
                connection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("book_name", name)
                        .appendQueryParameter("book_description", des)
                       .appendQueryParameter("user_id", s);
//                        .appendQueryParameter("user_password", pasword);


                String query = builder.build().getEncodedQuery();
//
//
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());


                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                out.close();

                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_CONFLICT) {
                    Log.d("My_log", "after connect");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    while ((line = in.readLine()) != null) {
                        sb.append(line + "\n");
                        x = sb.toString();
                        //Log.d("My_log", "" + x);
                    }
                    responseData = new String(x);
                } else {
                    Log.d("My_log", "unable to connect");
                    Toast.makeText(getContext(), "unable to connect", Toast.LENGTH_SHORT);
                    return null;
                }
            } catch (UnknownHostException uh) {
                Log.d("My_log", "Unknown host :");
                uh.printStackTrace();
            } catch (FileNotFoundException e) {
                Log.v("My_log", "FileNotFoundException :");
                e.printStackTrace();
            } catch (IOException e) {
                Log.v("My_log", "IOException :");
                e.printStackTrace();
            } catch (Exception e) {
                Log.v("My_log", "Exception :");
                e.printStackTrace();
            }
            return responseData;
        }



    }
}
