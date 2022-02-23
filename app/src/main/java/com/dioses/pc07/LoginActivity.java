package com.dioses.pc07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout tilUser, tilPassword;
    TextInputEditText tiedtUser, tiedtPassword;
    ProgressBar progressLoading;
    private CheckBox checkBoxRememberMe;

    public final static String EXTRA_USER = "EXTRA_USERNAME";
    public final static String EXTRA_PASSWORD = "EXTRA_PASSWORD";

    public static final String KEY_REMEMBER_ME_USER = "KEY_REMEMBER_ME_USER";
    public static final String KEY_REMEMBER_ME_PASSWORD = "KEY_REMEMBER_ME_PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tilUser = findViewById(R.id.til_user);
        tilPassword = findViewById(R.id.til_password);

        tiedtUser = findViewById(R.id.tiedt_user);
        tiedtPassword = findViewById(R.id.tiedt_password);

        progressLoading = findViewById(R.id.progress_loading);

        checkBoxRememberMe = findViewById(R.id.check_box_remember_me);

        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        tiedtUser.setText("");
        tiedtPassword.setText("");
        checkBoxRememberMe.setChecked(false);

        if (!getSharedPreferencesString(KEY_REMEMBER_ME_USER).isEmpty()) {
            tiedtUser.setText(getSharedPreferencesString(KEY_REMEMBER_ME_USER));
            checkBoxRememberMe.setChecked(true);
        }

        if (!getSharedPreferencesString(KEY_REMEMBER_ME_PASSWORD).isEmpty()) {
            tiedtPassword.setText(getSharedPreferencesString(KEY_REMEMBER_ME_PASSWORD));
        }

        btnLogin.setOnClickListener(view -> {
            if (checkBoxRememberMe.isChecked()) {
                setSharedPreferencesString(KEY_REMEMBER_ME_USER, getInputUser());
                setSharedPreferencesString(KEY_REMEMBER_ME_PASSWORD, getInputPassword());
            } else {
                setSharedPreferencesString(KEY_REMEMBER_ME_USER, "");
                setSharedPreferencesString(KEY_REMEMBER_ME_PASSWORD, "");
            }
            new TaskValidate().execute();
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    private String getInputUser() {
        return Objects.requireNonNull(tiedtUser.getText()).toString();
    }

    private String getInputPassword() {
        return Objects.requireNonNull(tiedtPassword.getText()).toString();
    }


    class TaskValidate extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return validateInDatabase();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideLoading();
            isValidUser(aBoolean);

        }
    }

    private void isValidUser(boolean isValid) {
        if (isValid) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(EXTRA_USER, getInputUser());
            intent.putExtra(EXTRA_PASSWORD, getInputPassword());
            startActivity(intent);
        } else {
            Toast.makeText(this, "No existe usuario con esas credenciales", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInDatabase() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "upn", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor fila;
        fila = bd.rawQuery("select * from usuario where username =  '" + getInputUser() + "' AND contrasenia= '" + getInputPassword() + "'", null);

        return fila.moveToFirst();
    }


    public void setSharedPreferencesString(String key, String data) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.dioses.pc07", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public String getSharedPreferencesString(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.dioses.pc07", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void showLoading() {
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressLoading.setVisibility(View.GONE);
    }
}