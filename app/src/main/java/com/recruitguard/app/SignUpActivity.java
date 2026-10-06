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

                if (password.isEmpty()) {
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

                if(securityQuestionSpinner.getSelectedItemPosition() == 0)
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

                // TODO: Call API
                Toast.makeText(SignUpActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();

                // Go to Sign In after successful registration
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
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