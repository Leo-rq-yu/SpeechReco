package com.example.speechreco;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private TextView tvText;
    private TextToSpeech t1;
    String url = "http://34.231.40.162:8000/api/chatterbot/";
    OkHttpClient client = new OkHttpClient();
//    Response response = null;
    MediaType MEDIA_TYPE = MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tvText);
        btnSpeak = findViewById(R.id.btnSpeak);


        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    int result = t1.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext() , "Language missing or not support", Toast.LENGTH_LONG).show();
                    }
                    tvText.setText("Hello. Welcome to the chatbot of cryptogevity, which is the chatbot mainly for cryptocurrency and related topics.");
                    t1.speak("Hello. Welcome to the chatbot of cryptogevity, which is the chatbot mainly for cryptocurrency and related topics.", TextToSpeech.QUEUE_ADD, null, null);
                    tvText.setText("How can I help you?");
                    t1.speak("How can I help you?", TextToSpeech.QUEUE_FLUSH, null,null);
                } else {
                    Toast.makeText(getApplicationContext() , "TTS initialization failed" + i, Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvText.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> texts = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(texts.get(0));
                    speak();
                    JSONObject postdata = new JSONObject();
                    try{
                        postdata.put("text", tvText.getText().toString());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
//                    tvText.setText(texts.get(0));
                    RequestBody body = RequestBody.create(postdata.toString(), MEDIA_TYPE);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .header("Content-type", "application/json")
                            .header("Accept", "text/plain")
                            .build();
                    tvText.setText("Please Wait");
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            tvText.setText(e.toString());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String jsonData = response.body().string();
                            try {
                                JSONObject JSONobj = new JSONObject(jsonData);
                                String answer = JSONobj.getString("text").toString();
                                t1.speak(answer,TextToSpeech.QUEUE_FLUSH, null,null);
                                Log.i("answer",answer);
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });

//                    tvText.setText(answer);
                }
                break;
            }
        }


    }

    private void speak() {
        String text = tvText.getText().toString();
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }
}