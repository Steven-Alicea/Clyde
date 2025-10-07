package com.example.clyde.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clyde.Database.ClydeDataSource;
import com.example.clyde.Dialogs.RegistrationConfirmationDialog;
import com.example.clyde.Dialogs.RegistrationErrorDialog;
import com.example.clyde.R;
import com.example.clyde.User.User;

import static com.example.clyde.Global.GlobalFunctions.verifyName;
import static com.example.clyde.Global.GlobalFunctions.verifyPassword;
import static com.example.clyde.Global.GlobalFunctions.verifyPhoneNumber;
import static com.example.clyde.Global.GlobalFunctions.verifyEmail;

public class RegisterActivity extends AppCompatActivity {

    private Button submit, cancel;
    private ClydeDataSource dataSource;
    private EditText firstName, middleName, lastName, phoneNumber, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //this.deleteDatabase("clyde.db");  // USED TO DELETE THE DATABASE (SOLVED PROBLEM ON MY PHONE WHEN TESTING)
        bindControls();
        registerHandlers();
        this.dataSource = new ClydeDataSource(this);
        openDatabase();
    }

    @Override
    protected void onDestroy() {
        closeDatabase();
        super.onDestroy();
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
        firstName       = findViewById(R.id.firstName);
        middleName      = findViewById(R.id.middleName);
        lastName        = findViewById(R.id.lastName);
        phoneNumber     = findViewById(R.id.phoneNumber);
        email           = findViewById(R.id.email);
        password        = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        submit          = findViewById(R.id.submit);
        cancel          = findViewById(R.id.cancel);
    }

    private void registerHandlers () {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegistrationInformationValid()){
                    if (!doesUserAccountExist()) {
                        RegisterUser();
                        openRegistrationConfirmationDialog();
                    }
                    else if (doesUserAccountExist()) {
                        openRegistrationErrorDialog();
                    }
                }
            }
        });
    }

    private boolean doesUserAccountExist() {
        return this.dataSource.getUserEmail(email.getText().toString());
    }

    private void RegisterUser() {
        User user = new User (0,
                lastName.getText().toString(),
                firstName.getText().toString(),
                middleName.getText().toString(),
                phoneNumber.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                (long) 0,
                (long) 0);
        this.dataSource.insertUser(user);
    }

    public boolean isRegistrationInformationValid() {
        if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "FIRST NAME MANDATORY", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!verifyName(firstName.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "INVALID FIRST NAME", Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "LAST NAME MANDATORY", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!verifyName(lastName.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "INVALID LAST NAME", Toast.LENGTH_SHORT).show();
            return false;
        } else if (middleName.getText().toString().isEmpty() && phoneNumber.getText().toString().isEmpty()) {
            if (!verifyEmail(email.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID EMAIL ADDRESS", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyPassword(password.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PASSWORD", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.getText().toString().matches(confirmPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "PASSWORDS DO NOT MATCH", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else if (middleName.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()) {
            if (!verifyPhoneNumber(phoneNumber.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PHONE NUMBER", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyEmail(email.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID EMAIL ADDRESS", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyPassword(password.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PASSWORD", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.getText().toString().matches(confirmPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "PASSWORDS DO NOT MATCH", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else if (!middleName.getText().toString().isEmpty() && phoneNumber.getText().toString().isEmpty()) {
            if (!verifyName(middleName.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID MIDDLE NAME", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyEmail(email.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID EMAIL ADDRESS", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyPassword(password.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PASSWORD", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.getText().toString().matches(confirmPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "PASSWORDS DO NOT MATCH", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else if (!middleName.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()) {
            if (!verifyName(middleName.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID MIDDLE NAME", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyPhoneNumber(phoneNumber.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PHONE NUMBER", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyEmail(email.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID EMAIL ADDRESS", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!verifyPassword(password.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "INVALID PASSWORD", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.getText().toString().matches(confirmPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "PASSWORDS DO NOT MATCH", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            Toast.makeText(RegisterActivity.this, "SOMETHING ELSE IS WRONG", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void openRegistrationConfirmationDialog() {
        RegistrationConfirmationDialog registrationConfirmationDialog = new RegistrationConfirmationDialog();
        registrationConfirmationDialog.show(getSupportFragmentManager(),"registration confirmation dialog");
    }

    public void openRegistrationErrorDialog() {
        RegistrationErrorDialog registrationErrorDialog = new RegistrationErrorDialog();
        registrationErrorDialog.show(getSupportFragmentManager(),"registration error dialog");
    }
}
