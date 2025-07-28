package com.example.potato_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private TextView resultView;
    private Bitmap selectedBitmap;

    private final String CLOUD_FUNCTION_URL = "https://us-central1-metal-scholar-466712-m1.cloudfunctions.net/predict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnSend = findViewById(R.id.btnSend);
        resultView = findViewById(R.id.resultView);

        btnSelect.setOnClickListener(v -> selectImage());
        btnSend.setOnClickListener(v -> {
            if (selectedBitmap != null) {
                sendImageToServer(selectedBitmap);
            } else {
                Toast.makeText(this, "Select an image first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImageToServer(Bitmap bitmap) {
        new Thread(() -> {
            try {
                // Convert bitmap to JPEG byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                String LINE_FEED = "\r\n";

                URL url = new URL(CLOUD_FUNCTION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("Connection", "Keep-Alive");

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Start form-data
                dos.writeBytes("--" + boundary + LINE_FEED);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"" + LINE_FEED);
                dos.writeBytes("Content-Type: image/jpeg" + LINE_FEED);
                dos.writeBytes(LINE_FEED);
                dos.write(imageBytes);
                dos.writeBytes(LINE_FEED);
                dos.writeBytes("--" + boundary + "--" + LINE_FEED);  // END boundary
                dos.flush();
                dos.close();

                // Read response
                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                Log.d("HTTP_RESPONSE", "Code: " + responseCode + ", Body: " + response);

                if (responseCode == 200) {
                    JSONObject json = new JSONObject(response.toString());
                    String predictedClass = json.getString("class");
                    double confidence = json.getDouble("confidence");
                    runOnUiThread(() -> resultView.setText("Class: " + predictedClass + "\nConfidence: " + confidence + "%"));
                } else {
                    runOnUiThread(() -> resultView.setText("Server error (" + responseCode + "):\n" + response.toString()));
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> resultView.setText("Exception: " + e.getMessage()));
            }
        }).start();
    }



}
