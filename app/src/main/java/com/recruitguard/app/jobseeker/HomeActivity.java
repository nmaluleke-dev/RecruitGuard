package com.recruitguard.app.jobseeker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.recruitguard.app.R;

public class HomeActivity extends AppCompatActivity {
    private Spinner typeSpinner;
    private EditText input;
    private Button scanBtn;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_home);

        typeSpinner = findViewById(R.id.js_type);
        input = findViewById(R.id.js_input);
        scanBtn = findViewById(R.id.js_scan_btn);
        progress = findViewById(R.id.js_progress);

        typeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"URL", "Text"}));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                input.setText("");
                input.setError(null);
                input.setHint(pos == 0 ? "Paste job posting URL" : "Paste job posting text");
            }
            @Override public void onNothingSelected(AdapterView<?> p) { }
        });

        scanBtn.setOnClickListener(v -> {
            final String value = input.getText().toString().trim();
            final String type = (String) typeSpinner.getSelectedItem();
            if (value.isEmpty()) { input.setError("This field is required."); return; }
            if (type.equals("URL") && !value.contains(".")) { input.setError("Enter a valid URL."); return; }

            scanBtn.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            // TODO: replace this delay with the backend call
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ScanScore.get().scan(value, type);
                progress.setVisibility(View.GONE);
                scanBtn.setEnabled(true);
                startActivity(new Intent(this, ResultActivity.class));
            }, 1200);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JsUi.setupNav(this, R.id.js_nav_scan);
    }
}