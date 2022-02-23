package com.dioses.pc07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private TextView textNames, textLastNames, textUsername, textPassword, textDni, textCellPhone, textSex;
    private FloatingActionButton btnRegisterUser, btnDeleteUser, btnUpdatePassword;
    private Button btnSaveNewPassword;
    private TextInputLayout tilNewPassword;
    private TextInputEditText tiedtNewPassword;
    private ProgressBar progressLoading;
    private Group groupSavePassword;
    private String usernameInput;
    private String passwordInput;
    public static final String EXTRA_FROM_ADMIN = "EXTRA_FROM_ADMIN";

    public static final String KEY_IS_FIRST_TIME = "EXTRA_IS_FIRST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textNames = findViewById(R.id.text_name);
        textLastNames = findViewById(R.id.text_last_name);
        textUsername = findViewById(R.id.text_username);
        textPassword = findViewById(R.id.text_password);
        textDni = findViewById(R.id.text_dni);
        textCellPhone = findViewById(R.id.text_cell_phone);
        textSex = findViewById(R.id.text_sexo);

        tilNewPassword = findViewById(R.id.til_new_password);
        tiedtNewPassword = findViewById(R.id.tiedt_new_password);

        progressLoading = findViewById(R.id.progress_loading);

        btnRegisterUser = findViewById(R.id.btn_register_new_user);
        btnDeleteUser = findViewById(R.id.btn_delete_user);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        btnSaveNewPassword = findViewById(R.id.btn_save_new_password);

        groupSavePassword = findViewById(R.id.group_save_password);

        setSharedPreferencesBool(KEY_IS_FIRST_TIME, false);

        if (getIntent().getExtras() != null) {
            usernameInput = getIntent().getStringExtra(LoginActivity.EXTRA_USER);
            passwordInput = getIntent().getStringExtra(LoginActivity.EXTRA_PASSWORD);
            new TaskGetdata().execute();
        }

        listeners();
    }

    private void listeners() {
        btnRegisterUser.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            intent.putExtra(EXTRA_FROM_ADMIN, true);
            startActivity(intent);
        });
        btnDeleteUser.setOnClickListener(view -> {

        });
        btnUpdatePassword.setOnClickListener(view -> {
            btnUpdatePassword.setVisibility(View.GONE);
            groupSavePassword.setVisibility(View.VISIBLE);
        });

        btnSaveNewPassword.setOnClickListener(view -> {
            if (getInputNewPassword().length() == 6) {
                new TaskUpdatePassword().execute();
            } else {
                tilNewPassword.setErrorEnabled(false);
                tilNewPassword.setError("Formato incorrecto");
            }
        });
    }

    class TaskGetdata extends AsyncTask<Void, Void, User> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected User doInBackground(Void... voids) {
            return getDataUser();
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            fillData(user);
            hideLoading();
        }
    }

    private User getDataUser() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "upn", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor fila = bd.rawQuery("select nombres,apellidos,telefono,dni,sexo,username,contrasenia,idrol from usuario where username =  '" + usernameInput + "' AND contrasenia= '" + passwordInput + "'", null);
        if (fila.moveToFirst()) {
            return new User(fila.getString(0), fila.getString(1), fila.getString(2),
                    fila.getString(3), fila.getInt(4), fila.getString(5),
                    fila.getString(6), fila.getInt(7));
        }
        return null;
    }

    class TaskUpdatePassword extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return modifyPassword();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideLoading();
            if (aBoolean) {
                Toast.makeText(HomeActivity.this, "Se modificó la nueva contraseña", Toast.LENGTH_SHORT).show();
                btnUpdatePassword.setVisibility(View.VISIBLE);
                groupSavePassword.setVisibility(View.GONE);
                textPassword.setText(getInputNewPassword());
            } else {
                Toast.makeText(HomeActivity.this, "No existe articulo con el código ingresado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean modifyPassword() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "upn", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues register = new ContentValues();
        register.put("contrasenia", getInputNewPassword());
        int cant = bd.update("usuario", register, "dni='" + getInputDNI() + "'", null);
        bd.close();
        return cant > 0;
    }

    private void fillData(User data) {
        textNames.setText(data.getNombres());
        textLastNames.setText(data.getApellidos());
        textDni.setText(data.getDni());
        textCellPhone.setText(data.getTelefono());
        textUsername.setText(data.getUsername());
        textPassword.setText(data.getConstrasenia());

        textSex.setText(data.getSexo() == 0 ? "Hombre" : "Mujer");
        displayRole(data.getIdrol() == 0);
    }

    private void displayRole(boolean isAdmin) {
        btnRegisterUser.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnDeleteUser.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnUpdatePassword.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
    }

    private String getInputNewPassword() {
        return Objects.requireNonNull(tiedtNewPassword.getText()).toString();
    }

    private String getInputDNI() {
        return textDni.getText().toString();
    }

    public void setSharedPreferencesBool(String key, boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.dioses.pc07", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, state);
        editor.apply();
    }

    private void showLoading() {
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressLoading.setVisibility(View.GONE);
    }
}