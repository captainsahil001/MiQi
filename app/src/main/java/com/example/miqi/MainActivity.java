package com.example.miqi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);
        TextView resultTextView = findViewById(R.id.textView2);

        button.setOnClickListener(view -> {
            String prompt = editText.getText().toString();

            // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
            GenerativeModel gm = new GenerativeModel(
                    /* modelName */ "gemini-2.0-flash",
                    /* apiKey */ "AIzaSyBLl7IwEWBtAInBAFgx0ucMv5UWkh2_tco" // Ensure BuildConfig.API_KEY is correctly configured
            );

            GenerativeModelFutures model = GenerativeModelFutures.from(gm);

            Content content = new Content.Builder()
                    .addText(prompt)
                    .build();

            ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4));

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    runOnUiThread(() -> {
                        resultTextView.setText(resultText); // Update the UI on the main thread
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    t.printStackTrace();
                }
            }, executor);
        });
    }
}
