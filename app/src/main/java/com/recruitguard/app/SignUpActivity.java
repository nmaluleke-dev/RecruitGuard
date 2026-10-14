package com.recruitguard.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity
{
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Spinner securityQuestionSpinner;
    private EditText securityAnswerInput;
    private Button signUpButton;
    private TextView signInLink;
    private String[] securityQuestions =
    {
        "Select a security question",
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
        setContentView(R.layout.activity_sign_up);

        // Hide action bar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        // Find views
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        securityQuestionSpinner = findViewById(R.id.securityQuestionSpinner);
        securityAnswerInput = findViewById(R.id.securityAnswerInput);
        signUpButton = findViewById(R.id.signUpButton);
        signInLink = findViewById(R.id.signInLink);

        // Password visibility toggle
        passwordInput.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    // Check if the click is on the drawableEnd icon
                    int drawableEndWidth = passwordInput.getCompoundPaddingEnd();
                    if (event.getX() >= (passwordInput.getWidth() - drawableEndWidth))
                    {

                        int selectionStart = passwordInput.getSelectionStart();
                        int selectionEnd = passwordInput.getSelectionEnd();

                        if (passwordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod)
                        {
                            // Show Password & Switch to Eye Off Icon
                            passwordInput.setTransformationMethod(null);
                            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                        }
                        else
                        {
                            // Hide Password & Switch to Open Eye Icon
                            passwordInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                        }

                        // Restore cursor position
                        passwordInput.setSelection(selectionStart, selectionEnd);
                        passwordInput.performClick();
                        return true;
                    }
                }
                return false;
            }
        });

        // Setup spinner with white text (editable)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, securityQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityQuestionSpinner.setAdapter(adapter);
        securityQuestionSpinner.setEnabled(true);

        // Sign Up button on click listener
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String securityQuestion = securityQuestionSpinner.getSelectedItem().toString();
                String securityAnswer = securityAnswerInput.getText().toString().trim();

                // Validation 1: Check for empty fields
                if (name.isEmpty()) {
                    nameInput.setError("Name is required");
                    nameInput.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                    return;
                }

                // Validation 2: Check email format
                if (!isValidEmail(email)) {
                    emailInput.setError("Please enter a valid email address");
                    emailInput.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordInput.setError("Password is required");
                    passwordInput.requestFocus();
                    return;
                }

                // Validation 3: Check password length
                if (password.length() < 6) {
                    passwordInput.setError("Password must be at least 6 characters");
                    passwordInput.requestFocus();
                    return;
                }

                if (securityQuestionSpinner.getSelectedItemPosition() == 0) {
                    Utils.showToast(SignUpActivity.this, "Please select a security question");
                    return;
                }

                if (securityAnswer.isEmpty()) {
                    securityAnswerInput.setError("Security answer is required");
                    securityAnswerInput.requestFocus();
                    return;
                }

                //TODO: TEMP

                        Utils.showToast(SignUpActivity.this, "Successfully registered!");
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish();


                //TODO: PUT BACK AFTER DEMO
                /*
                //Call API
                RegisterRequest request = new RegisterRequest(
                        name,
                        email,
                        password,
                        "job_seeker",
                        securityQuestion,
                        securityAnswer
                );

                ApiService apiService = ApiClient.getService();
                Call<AuthResponse> call = apiService.register(request);

                call.enqueue(new retrofit2.Callback<AuthResponse>()
                {
                    @Override
                    public void onResponse(Call<AuthResponse> call, retrofit2.Response<AuthResponse> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            // Success message
                            Utils.showToast(SignUpActivity.this, "Successfully registered!");

                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            finish();
                        }
                        else
                        {
                            // Error Message
                            Utils.showToast(SignUpActivity.this, "Registration failed. Email may already exist.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t)
                    {
                        // Network error
                        Utils.showToast(SignUpActivity.this, "Network error. Check connection.");
                    }
                }); */
            }
        });


        // Sign In link on click listener
        signInLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    // Email validation helper method
    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}