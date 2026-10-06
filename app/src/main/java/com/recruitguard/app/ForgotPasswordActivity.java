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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Spinner setup (keep this)
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

        // TODO: VERIFY EMAIL BUTTON
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

                // Call API to verify email exists and get security question
                VerifyEmailRequest request = new VerifyEmailRequest(email);
                ApiService apiService = ApiClient.getService();

                apiService.getSecurityQuestion(request).enqueue(new Callback<SecurityQuestionResponse>()
                {
                    @Override
                    public void onResponse(Call<SecurityQuestionResponse> call, Response<SecurityQuestionResponse> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            String question = response.body().getQuestion();

                            emailInput.setEnabled(false);
                            verifyEmailButton.setEnabled(false);
                            verifyEmailButton.setText("Verified");

                            securitySection.setVisibility(View.VISIBLE);

                            for (int i = 0; i < securityQuestions.length; i++)
                            {
                                if (securityQuestions[i].equals(question))
                                {
                                    securityQuestionSpinner.setSelection(i);
                                    break;
                                }
                            }
                            Toast.makeText(ForgotPasswordActivity.this, "Email verified! Please answer your security question.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, "Email not registered in our system.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SecurityQuestionResponse> call, Throwable t)
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // RESET PASSWORD BUTTON - ONLY THIS ONE (API version)
        resetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailInput.getText().toString().trim();
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

                // Call API to reset password
                ResetPasswordRequest request = new ResetPasswordRequest(email, answer, newPass);
                ApiService apiService = ApiClient.getService();

                apiService.resetPassword(request).enqueue(new Callback<Void>()
                {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response)
                    {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, "Incorrect security answer. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t)
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}