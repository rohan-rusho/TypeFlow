package com.thebitx.offlinenote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView textSentence, textSpeed, textAccuracy, textTimer;
    EditText inputTyping;
    Button btnStart, btnRestart, btnShare;

    String[] sentences = {
            "The quick brown fox jumps over the lazy dog.",
            "Typing fast requires practice and patience.",
            "Accuracy is just as important as speed.",
            "Android development is fun and rewarding.",
            "TypeFlow helps improve typing efficiency.",
            "Practice makes perfect when learning to type.",
            "Coding every day builds strong programming skills.",
            "A smooth sea never made a skilled sailor.",
            "Technology is best when it brings people together.",
            "Success is the sum of small efforts repeated daily.",
            "Dream big, work hard, and stay consistent.",
            "Shortcuts rarely lead to long term success.",
            "Never stop learning because life never stops teaching.",
            "Perseverance is the key to mastering any skill.",
            "Simplicity is the soul of efficiency.",
            "Patience and persistence make an unbeatable combination.",
            "Reading improves knowledge and sharpens the mind.",
            "Challenges are what make life interesting.",
            "A journey of a thousand miles begins with one step.",
            "Discipline is the bridge between goals and accomplishment."
    };

    String currentSentence = "";
    long startTime = 0;
    boolean testRunning = false;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSentence = findViewById(R.id.textSentence);
        textSpeed = findViewById(R.id.textSpeed);
        textAccuracy = findViewById(R.id.textAccuracy);
        textTimer = findViewById(R.id.textTimer);   // 👈 Add TextView in XML for timer
        inputTyping = findViewById(R.id.inputTyping);
        btnStart = findViewById(R.id.btnStart);
        btnRestart = findViewById(R.id.btnRestart);
        btnShare = findViewById(R.id.btnShare);     // 👈 Add Button in XML for sharing

        btnStart.setOnClickListener(v -> startTest());
        btnRestart.setOnClickListener(v -> resetTest());
        btnShare.setOnClickListener(v -> shareResult());
    }

    private void startTest() {
        // Pick random sentence
        currentSentence = sentences[new Random().nextInt(sentences.length)];
        textSentence.setText(currentSentence);

        // Reset fields
        inputTyping.setText("");
        textSpeed.setText("Speed: 0 WPM");
        textAccuracy.setText("Accuracy: 0%");
        textTimer.setText("Time: 10s");
        inputTyping.setEnabled(true);
        inputTyping.requestFocus();
        btnShare.setVisibility(View.GONE);

        startTime = System.currentTimeMillis();
        testRunning = true;

        // Countdown timer for 10s
        countDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                textTimer.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                checkTyping(); // stop and calculate result
            }
        }.start();

        btnStart.setVisibility(View.GONE);
        btnRestart.setVisibility(View.VISIBLE);
    }

    private void checkTyping() {
        if (!testRunning) return;

        String typedText = inputTyping.getText().toString();

        // Stop timer if still running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long elapsedTime = System.currentTimeMillis() - startTime; // ms
        double minutes = elapsedTime / 60000.0;

        // Words per minute
        int wordCount = typedText.trim().isEmpty() ? 0 : typedText.trim().split("\\s+").length;
        int wpm = (int) (wordCount / minutes);

        // Accuracy
        int correctChars = 0;
        for (int i = 0; i < Math.min(currentSentence.length(), typedText.length()); i++) {
            if (typedText.charAt(i) == currentSentence.charAt(i)) {
                correctChars++;
            }
        }
        int accuracy = (int) (((double) correctChars / currentSentence.length()) * 100);

        // Update UI
        textSpeed.setText("Speed: " + wpm + " WPM");
        textAccuracy.setText("Accuracy: " + accuracy + "%");
        textTimer.setText("Time: 0s");

        inputTyping.setEnabled(false);
        testRunning = false;
        btnShare.setVisibility(View.VISIBLE);
    }

    private void resetTest() {
        if (countDownTimer != null) countDownTimer.cancel();

        inputTyping.setText("");
        textSentence.setText("Press Start to begin typing test.");
        textSpeed.setText("Speed: 0 WPM");
        textAccuracy.setText("Accuracy: 0%");
        textTimer.setText("Time: 10s");
        btnStart.setVisibility(View.VISIBLE);
        btnRestart.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
        testRunning = false;
    }

    private void shareResult() {
        String result = textSpeed.getText().toString() + "\n" + textAccuracy.getText().toString();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Typing Test Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is my result on TypeFlow:\n" + result);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
