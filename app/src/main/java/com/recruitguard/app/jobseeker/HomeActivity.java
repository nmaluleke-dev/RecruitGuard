package com.recruitguard.app.jobseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.recruitguard.app.jobseeker.HomeActivity;
import com.recruitguard.app.R;
import com.recruitguard.app.ResultActivity;

public class HomeActivity extends AppCompatActivity {
    private Spinner typeSpinner;
    private EditText input;
    private TextView scanBtn;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.js_home);
        JsUi.window(this);
        ScanScore.init(this);
        JsUi.setupDrawer(this, R.id.js_menu_home);

        String name = ScanScore.userName();
        ((TextView) findViewById(R.id.js_welcome))
                .setText(name.isEmpty() ? "Welcome back" : "Welcome back, " + name);

        typeSpinner = findViewById(R.id.js_type);
        input = findViewById(R.id.js_input);
        scanBtn = findViewById(R.id.js_scan_btn);
        progress = findViewById(R.id.js_progress);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.js_spinner_item,
                new String[]{"Text", "URL"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                input.setText("");
                input.setError(null);
                input.setHint(pos == 0 ? "e.g. We are hiring urgently. Pay R500 fee..."
                        : "e.g. https://www.linkedin.com/jobs...");
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

            ScanScore.get().scan(value, type, new ScanScore.ScanCallback() {
                @Override public void onSuccess(ScanScore.Result r) {
                    progress.setVisibility(View.GONE);
                    scanBtn.setEnabled(true);
                    startActivity(new Intent(HomeActivity.this, ResultActivity.class));
                }
                @Override public void onError(String message) {
                    progress.setVisibility(View.GONE);
                    scanBtn.setEnabled(true);
                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JsUi.refreshBadge(this);
        ScanScore.get().loadNotifications(error -> JsUi.refreshBadge(this));
    }
}