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

        // Spinner with questions (disabled)
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, securityQuestions)
        {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#333333"));
                text.setTextSize(16);
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
                            // Show Password & Switch to Eye Off Icon
                            newPasswordInput.setTransformationMethod(null);
                            newPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        }
                        else
                        {
                            // Hide Password & Switch to Open Eye Icon
                            newPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            newPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
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
                            // Show Password & Switch to Eye Off Icon
                            confirmPasswordInput.setTransformationMethod(null);
                            confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        }
                        else
                        {
                            // Hide Password & Switch to Open Eye Icon
                            confirmPasswordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            confirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                        }

                        confirmPasswordInput.setSelection(selectionStart, selectionEnd);
                        confirmPasswordInput.performClick();
                        return true;
                    }
                }
                return false;
            }
        });


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
                            //get index from API
                            int questionIndex = Integer.parseInt(response.body().getQuestion());

                            //Disable email editing
                            emailInput.setEnabled(false);
                            verifyEmailButton.setEnabled(false);
                            verifyEmailButton.setText("Verified");

                            //show security section
                            securitySection.setVisibility(View.VISIBLE);

                            // Set security question. Just displays the text and disables the spinner.
                            securityQuestionSpinner.setSelection(questionIndex);

                            Utils.showToast(ForgotPasswordActivity.this, "Email verified! Please answer your security question.");
                        }
                        else
                        {
                            Utils.showToast(ForgotPasswordActivity.this, "Email not registered in our system.");
                        }
                    }

                    @Override
                    public void onFailure(Call<SecurityQuestionResponse> call, Throwable t)
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
                            Utils.showToast(ForgotPasswordActivity.this, "Password reset successfully!");

                            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Utils.showToast(ForgotPasswordActivity.this, "Incorrect security answer. Please try again.");
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