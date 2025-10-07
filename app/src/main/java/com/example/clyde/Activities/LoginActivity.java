package com.example.clyde.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clyde.Database.ClydeDataSource;
import com.example.clyde.Dialogs.LoginErrorDialog;
import com.example.clyde.R;
import com.example.clyde.Utils.PermissionUtils;
import com.example.clyde.Utils.PreferenceUtils;

import static com.example.clyde.Global.GlobalFunctions.verifyPassword;
import static com.example.clyde.Global.GlobalFunctions.verifyEmail;

public class LoginActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private Button login;
    private ClydeDataSource dataSource;
    private EditText email, password;
    private TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!checkPermission())
            askPermission();
        bindControls();
        checkSharedPreferences();
        registerHandlers();
        this.dataSource = new ClydeDataSource(this);
        openDatabase();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermission() {
        PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
        } else {
            mPermissionDenied = true;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }

    private void checkSharedPreferences() {
        if ((PreferenceUtils.getEmail(this) != null) && (PreferenceUtils.getPassword(this) != null)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean openDatabase() {
        try {
            this.dataSource.open();
            return true;
        }
        catch (SQLiteException ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void closeDatabase() {
        try {
            this.dataSource.close();
        }
        catch (SQLiteException ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void bindControls() {
        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login    = findViewById(R.id.btnLogin);
        register = findViewById(R.id.register);
    }

    private void registerHandlers() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoginInformationValid()) {
                    String e =  email.getText().toString().trim();
                    String p = password.getText().toString().trim();
                    if (!isUserInRegistered(e, p))
                        openLoginErrorDialog();
                    else {
                        PreferenceUtils.saveEmail(e, getApplicationContext());
                        PreferenceUtils.savePassword(p, getApplicationContext());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("EMAIL", email.getText().toString().trim());
                        email.setText(null);
                        password.setText(null);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    public boolean isUserInRegistered(String email, String password) {
        return this.dataSource.getUserEmailAndPassword(email, password);
    }

    public boolean isLoginInformationValid() {
        if (!verifyEmail(email.getText().toString())) {
            Toast.makeText(LoginActivity.this, "INVALID EMAIL ADDRESS" , Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!verifyPassword(password.getText().toString())) {
            Toast.makeText(LoginActivity.this, "INVALID PASSWORD", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void openLoginErrorDialog() {
        LoginErrorDialog loginErrorDialog = new LoginErrorDialog();
        loginErrorDialog.show(getSupportFragmentManager(),"login error dialog");
    }
}
