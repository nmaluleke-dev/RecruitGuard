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

    // security questions
    private String[] securityQuestions =
            {
                    "Select your security question",
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

        // Start with both password fields hidden (dots) and the closed eye icon
        newPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        newPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
        confirmPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);

        // Spinner with questions
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, securityQuestions)
        {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#000000"));
                text.setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#000000"));
                text.setPadding(32, 24, 32, 24);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);
        securityQuestionSpinner.setEnabled(true);


        // Password visibility toggle for New Password
        newPasswordInput.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    int drawableEndWidth = newPasswordInput.getCompoundPaddingEnd();
                    if (event.getX() >= (newPasswordInput.getWidth() - drawableEndWidth))
                    {
                        int selectionStart = newPasswordInput.getSelectionStart();
                        int selectionEnd = newPasswordInput.getSelectionEnd();

                        if (newPasswordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod)
                        {
                            // Show password and switch to the open eye icon
                            newPasswordInput.setTransformationMethod(null);
                            newPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                        }
                        else
                        {
                            // Hide password and switch to the closed eye icon
                            newPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            newPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        }

                        newPasswordInput.setSelection(selectionStart, selectionEnd);
                        newPasswordInput.performClick();
                        return true;
                    }
                }
                return false;
            }
        });

        // Password visibility toggle for Confirm Password
        confirmPasswordInput.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    int drawableEndWidth = confirmPasswordInput.getCompoundPaddingEnd();
                    if (event.getX() >= (confirmPasswordInput.getWidth() - drawableEndWidth))
                    {
                        int selectionStart = confirmPasswordInput.getSelectionStart();
                        int selectionEnd = confirmPasswordInput.getSelectionEnd();

                        if (confirmPasswordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod)
                        {
                            // Show password and switch to the open eye icon
                            confirmPasswordInput.setTransformationMethod(null);
                            confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                        }
                        else
                        {
                            // Hide password and switch to the closed eye icon
                            confirmPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        }

                        confirmPasswordInput.setSelection(selectionStart, selectionEnd);
                        confirmPasswordInput.performClick();
                        return true;
                    }
                }
                return false;
            }
        });


        verifyEmailButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                    return;
                }

                if (!isValidEmail(email)) {
                    emailInput.setError("Please enter a valid email");
                    emailInput.requestFocus();
                    return;
                }

                // Call API to verify email exists
                VerifyEmailRequest request = new VerifyEmailRequest(email);
                ApiService apiService = ApiClient.getService();

                apiService.verifyEmail(request).enqueue(new Callback<Void>()
                {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response)
                    {
                        if (response.isSuccessful())
                        {
                            emailInput.setEnabled(false);
                            verifyEmailButton.setEnabled(false);
                            verifyEmailButton.setText("Verified");
                            securitySection.setVisibility(View.VISIBLE);
                            securityQuestionSpinner.setEnabled(true);
                            Utils.showToast(ForgotPasswordActivity.this, "Email verified! Please select your security question.");
                        }
                        else
                        {
                            Utils.showToast(ForgotPasswordActivity.this, "Email not registered in our system.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t)
                    {
                        Utils.showToast(ForgotPasswordActivity.this, "Network error: " + t.getMessage());
                    }
                });
            }
        });


        // Reset Password Button
        resetPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailInput.getText().toString().trim();
                String answer = securityAnswerInput.getText().toString().trim();
                String newPass = newPasswordInput.getText().toString().trim();
                String confirmPass = confirmPasswordInput.getText().toString().trim();

                if (securityQuestionSpinner.getSelectedItemPosition() == 0)
                {
                    Utils.showToast(ForgotPasswordActivity.this, "Please select your security question");
                    return;
                }

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

                int questionIndex = securityQuestionSpinner.getSelectedItemPosition() - 1;

                // Call API to reset password
                ResetPasswordRequest request = new ResetPasswordRequest(
                        email,
                        questionIndex,
                        answer,
                        newPass);

                ApiService apiService = ApiClient.getService();

                apiService.resetPassword(request).enqueue(new Callback<Void>()
                {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response)
                    {
                        if (response.isSuccessful())
                        {
                            Utils.showToast(ForgotPasswordActivity.this, "Password reset successfully!");

                            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            //Check which error the backend sent
                            String errorMessage = "Incorrect security answer. Please try again";
                            try {
                                if(response.errorBody() != null)
                                {
                                    String errorBody = response.errorBody().string();

                                    if(errorBody.contains("question"))
                                    {
                                        errorMessage = "Incorrect security question selected. Please try again";
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                //Keep default message
                            }
                            Utils.showToast(ForgotPasswordActivity.this, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t)
                    {
                        Utils.showToast(ForgotPasswordActivity.this, "Network error: " + t.getMessage());
                    }
                });
            }
        });
    }

    // Email validation helper method
    private boolean isValidEmail(String email)
    {
        // Check basic format first
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return false;
        }

        // Check for valid domain extensions
        String[] validDomains = {".com", ".co.za", ".org", ".net", ".ac.za", ".gov.za", ".edu"};
        boolean hasValidDomain = false;

        for (String domain : validDomains)
        {
            if (email.toLowerCase().endsWith(domain))
            {
                hasValidDomain = true;
                break;
            }
        }
        return hasValidDomain;
    }
}