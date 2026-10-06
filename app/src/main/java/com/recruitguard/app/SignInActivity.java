package com.recruitguard.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity
{
    private EditText emailInput;
    private EditText passwordInput;
    private Button signInButton;
    private TextView signUpLink;
    private TextView forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Hide action bar
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }

        // Find views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        signUpLink = findViewById(R.id.signUpLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        // Sign In button click
        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Validation
                if (email.isEmpty())
                {
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                    return;
                }

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

                LoginRequest request = new LoginRequest(email, password);

                ApiService apiService = ApiClient.getService();
                Call<AuthResponse> call = apiService.login(request);

                call.enqueue(new Callback<AuthResponse>()
                {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            AuthResponse auth = response.body();
                            Toast.makeText(SignInActivity.this, "Welcome " + auth.getName() + "!", Toast.LENGTH_SHORT).show();

                            // Save to SharedPreferences
                            android.content.SharedPreferences prefs = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE);
                            android.content.SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", auth.getToken());
                            editor.putString("role", auth.getRole());
                            editor.putString("name", auth.getName());
                            editor.putInt("userId", auth.getUserId());
                            editor.apply();

                            // TODO: Role-based routing


                        }
                        else
                        {
                            Toast.makeText(SignInActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t)
                    {
                        Toast.makeText(SignInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Sign Up link on click listener
        signUpLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        // Forgot Password click listener
        forgotPasswordLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}