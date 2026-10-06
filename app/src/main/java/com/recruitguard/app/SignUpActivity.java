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
                if (name.isEmpty())
                {
                    nameInput.setError("Name is required");
                    nameInput.requestFocus();
                    return;
                }

                if (email.isEmpty())
                {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                    return;
                }

                // Validation 2: Check email format
                if (!isValidEmail(email))
                {
                    emailInput.setError("Please enter a valid email address");
                    emailInput.requestFocus();
                    return;
                }

                if (password.isEmpty())
                {
                    passwordInput.setError("Password is required");
                    passwordInput.requestFocus();
                    return;
                }

                // Validation 3: Check password length
                if (password.length() < 6)
                {
                   passwordInput.setError("Password must be at least 6 characters");
                   passwordInput.requestFocus();
                   return;
                }

                if (securityQuestionSpinner.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "Please select a security question", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (securityAnswer.isEmpty())
                {
                    securityAnswerInput.setError("Security answer is required");
                    securityAnswerInput.requestFocus();
                    return;
                }

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
                            Toast.makeText(SignUpActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();

                            //TODO: Save to SharedPreferences
                            saveToSharedPreferences(response.body());

                            // TODO: Go to Sign In or Home based on role
                            navigateBasedOnRole(response.body().getRole());
                        }
                        else
                        {
                            // Error Message
                            Toast.makeText(SignUpActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t)
                    {
                        // Network error
                        Toast.makeText(SignUpActivity.this, "Network error. Check connection.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void saveToSharedPreferences(AuthResponse auth)
    {
         android.content.SharedPreferences prefs = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE);
         android.content.SharedPreferences.Editor editor = prefs.edit();
         editor.putString("token", auth.getToken());
         editor.putString("role", auth.getRole());
         editor.putString("name", auth.getName());
         editor.putInt("userId", auth.getUserId());
         editor.apply();
    }

    private void navigateBasedOnRole(String role)
    {
        if ("admin".equals(role))
        {
            // TODO: Go to Admin Dashboard when Bile builds it
            Toast.makeText(this, "Admin login", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // TODO: Go to Home when Tanaka builds it
            Toast.makeText(this, "Job Seeker", Toast.LENGTH_SHORT).show();
        }
    }
}