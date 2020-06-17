package com.example.todo_app.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todo_app.R;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button login, signup;
    private TextView title;
    private int a = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean Registered;
    Boolean log, value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupView();
        Registered = sharedPreferences.getBoolean("Registered", false);
        log = sharedPreferences.getBoolean("Log", false);

        if (!Registered) {
            login.setVisibility(View.GONE);
            signup.setVisibility(View.VISIBLE);
            title.setText("Sign Up");
        }
        Intent intent = getIntent();
        value = intent.getBooleanExtra("log", true);
        if (value == false) {
            editor.putBoolean("Log", false);
            editor.commit();
            log = sharedPreferences.getBoolean("Log", false);
        }


        // If the user is registered already.
        if (Registered) {
            login.setVisibility(View.VISIBLE);
            signup.setVisibility(View.GONE);
            title.setText("Log In");
            if (log) {
                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        }

        //Login method called when user clicks login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck();
            }
        });

        //Signup method called when user clicks sign up button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCheck();
            }
        });
    }

    private void setupView() {
        title = findViewById(R.id.textView);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.Login);
        signup = findViewById(R.id.Signup);

        username.setText("");
        password.setText("");
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = sharedPreferences.edit();
    }

    private void registerCheck() {
        String r_User = username.getText().toString();
        String r_Pass = password.getText().toString();

        if (r_User.isEmpty()) {
            alertDialogue("Username is empty");
        } else if (r_Pass.isEmpty()) {
            alertDialogue("Password is empty");
        } else {
            editor.putBoolean("Registered", true);
            editor.putString("username", r_User);
            editor.putString("password", r_Pass);
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    //Login check method
    private void loginCheck() {
        String userName = username.getText().toString();
        String pass = password.getText().toString();

        if (userName.isEmpty()) {
            alertDialogue("Username is empty");
        } else if (pass.isEmpty()) {
            alertDialogue("Password is empty");
        } else {
            String uName = sharedPreferences.getString("username", null);
            String uPass = sharedPreferences.getString("password", null);
            if (userName.equals(uName)) {
                if (pass.equals(uPass)) {
                    editor.putBoolean("Log", true);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Username is incorrect", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Alert message dialogue method
    private void alertDialogue(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.show();
    }
}
