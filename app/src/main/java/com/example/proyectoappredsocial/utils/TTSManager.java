package com.example.proyectoappredsocial.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSManager {

    private TextToSpeech tts = null;
    private boolean isLoaded = false;

    public void init(Context context) {
        try {
            tts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            Locale spanish = new Locale("es", "ES");
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(spanish);
                isLoaded = true;
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "Lenguaje no permitido");
                }
            } else {
                Log.e("error", "Fallo al inicializar");
            }
        }
    };

    public void shutDown() {
        tts.shutdown();
    }

    public void addQueue(String text) {

        if (isLoaded) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            Log.e("error", "TTS Not initialized");
        }
    }

    public void initQueue(String text) {

        if (isLoaded) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.e("error", "TTS Not initialized");
        }
    }

}
