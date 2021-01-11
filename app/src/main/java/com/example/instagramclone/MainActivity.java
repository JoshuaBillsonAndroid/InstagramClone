package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView nameEditText;
    TextView passwordEditText;
    ConstraintLayout loginLayout;
    ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Grab Views */
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginLayout = findViewById(R.id.loginLayout);
        logoImageView = findViewById(R.id.logoImageView);

        /* Set OnClickListener */
        loginLayout.setOnClickListener(this);
        logoImageView.setOnClickListener(this);
    }

    public void onRegister(View view) {
        hideKeyboard();
        if (nameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Need To Enter Username & Password!", Toast.LENGTH_SHORT).show();
        } else {
            ParseUser user = new ParseUser();
            user.setPassword(passwordEditText.getText().toString());
            user.setUsername(nameEditText.getText().toString());

            user.signUpInBackground((ParseException e) -> {
                if (e == null) {
                    Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Username Already Exists!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onLogin(View view) {
        hideKeyboard();
        ParseUser.logInInBackground(nameEditText.getText().toString(), passwordEditText.getText().toString(), (ParseUser user, ParseException e) -> {
            if (user != null) {
                Intent intent = new Intent(getApplicationContext(), UserList.class);
                startActivity(intent);
            } else if (e != null) {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginLayout || view.getId() == R.id.logoImageView) {
            hideKeyboard();
        }
    }
}