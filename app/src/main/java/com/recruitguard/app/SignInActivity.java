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
                            Utils.showToast(SignInActivity.this, "Welcome " + auth.getName() + "!");
                            // Save to SharedPreferences
                            android.content.SharedPreferences prefs = getSharedPreferences("RecruitGuardPrefs", MODE_PRIVATE);
                            android.content.SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", auth.getToken());
                            editor.putString("role", auth.getRole());
                            editor.putString("name", auth.getName());
                            editor.putInt("userId", auth.getUserId());
                            editor.apply();

                            /*
                            // TODO: Role-based routing
                            if ("admin".equals(auth.getRole()))
                            {
                                // Go to Admin Dashboard
                                startActivity(new Intent(SignInActivity.this, AdminDashboardActivity.class));
                            }
                            else
                            {
                                //Go to Job Seeker Home
                                startActivity(new Intent(SignInActivity.this, com.recruitguard.app.jobseeker.HomeActivity.class));
                            }
                            finish();
                            */

                        }
                        else
                        {
                            Utils.showToast(SignInActivity.this, "Invalid email or password");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t)
                    {
                        Utils.showToast(SignInActivity.this, "Network error: " + t.getMessage());
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