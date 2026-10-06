package com.recruitguard.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;

public class ForgotPasswordActivity extends AppCompatActivity
{
    private EditText emailInput;
    private Button verifyEmailButton;
    private LinearLayout securitySection;
    private Spinner securityQuestionSpinner;
    private EditText securityAnswerInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button resetPasswordButton;
    private ArrayAdapter<String> adapter;

    // Demo questions
    private String[] securityQuestions =
    {
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What city were you born in?",
        "What is your favorite food?",
        "What was the name of your first school?",
        "What is your childhood best friend's name?",
        "What was the make of your first car?",
        "What is your favorite movie?",
        "What street did you grow up on?",
        "What is your favorite book?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        securitySection = findViewById(R.id.securitySection);
        securityQuestionSpinner = findViewById(R.id.securityQuestionSpinner);
        securityAnswerInput = findViewById(R.id.securityAnswerInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Spinner with questions (it is disabled by default)
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, securityQuestions)
        {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                text.setTextColor(Color.parseColor("#FFFFFF"));
                text.setTextSize(14);
                text.setTypeface(null, Typeface.BOLD);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#1C1C1E"));
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);
        securityQuestionSpinner.setEnabled(false);

        // Verify Email button
        verifyEmailButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty())
                {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                    return;
                }

                if (!isValidEmail(email))
                {
                    emailInput.setError("Please enter a valid email");
                    emailInput.requestFocus();
                    return;
                }

                // TODO: Call API to check if email exists and get security question
                emailInput.setEnabled(false);
                verifyEmailButton.setEnabled(false);
                verifyEmailButton.setText("Verified");
                securitySection.setVisibility(View.VISIBLE);
                securityQuestionSpinner.setSelection(0);

                // TODO: Remove this (demo)
                Toast.makeText(ForgotPasswordActivity.this, "Email verified", Toast.LENGTH_SHORT).show();
            }
        });

        // Reset Password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String answer = securityAnswerInput.getText().toString().trim();
                String newPass = newPasswordInput.getText().toString().trim();
                String confirmPass = confirmPasswordInput.getText().toString().trim();

                if (answer.isEmpty())
                {
                    securityAnswerInput.setError("Answer is required");
                    securityAnswerInput.requestFocus();
                    return;
                }

                if (newPass.isEmpty())
                {
                    newPasswordInput.setError("New password is required");
                    newPasswordInput.requestFocus();
                    return;
                }

                if (newPass.length() < 6)
                {
                    newPasswordInput.setError("Password must be at least 6 characters");
                    newPasswordInput.requestFocus();
                    return;
                }

                if (!newPass.equals(confirmPass))
                {
                    confirmPasswordInput.setError("Passwords do not match");
                    confirmPasswordInput.requestFocus();
                    return;
                }

                // Show success message and go to Sign In screen
                Toast.makeText(ForgotPasswordActivity.this,"Password reset successfully!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                //If the user clicks back from sign in they will exit the app rather than return to the forgot password screen.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}