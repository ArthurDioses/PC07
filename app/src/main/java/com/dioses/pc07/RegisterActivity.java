package com.dioses.pc07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilNames, tilLastName, tilDni, tilCellPhone, tilUser, tilPassword;
    private TextInputEditText tiedtNames, tiedtLastName, tiedtDni, tiedtCellPhone, tiedtUser, tiedtPassword;
    private RadioButton radioButtonMan, radioButtonWoman;
    private CheckBox checkBoxAdmin, checkBoxUserNormal;
    private Button btnRegister, btnLogin;

    private ProgressBar progressLoading;
    private RadioGroup radioGroupSex;

    private Group groupRole;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getIntent().getExtras() != null) {
            isAdmin = getIntent().getExtras().getBoolean(HomeActivity.EXTRA_FROM_ADMIN);
        }
        //TextInputLayout match
        tilNames = findViewById(R.id.til_names);
        tilLastName = findViewById(R.id.til_last_name);
        tilDni = findViewById(R.id.til_dni);
        tilCellPhone = findViewById(R.id.til_cell_phone);
        tilUser = findViewById(R.id.til_user);
        tilPassword = findViewById(R.id.til_password);

        //TextInputEditText match
        tiedtNames = findViewById(R.id.tiedt_names);
        tiedtLastName = findViewById(R.id.tiedt_last_name);
        tiedtDni = findViewById(R.id.tiedt_dni);
        tiedtCellPhone = findViewById(R.id.tiedt_cell_phone);
        tiedtUser = findViewById(R.id.tiedt_user);
        tiedtPassword = findViewById(R.id.tiedt_password);

        //RadioButton match
        radioButtonMan = findViewById(R.id.radio_button_man);
        radioButtonWoman = findViewById(R.id.radio_button_woman);

        //Checkbox match
        checkBoxAdmin = findViewById(R.id.check_box_admin);
        checkBoxUserNormal = findViewById(R.id.check_box_user);

        //Button match
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);

        //ProgressBar match
        progressLoading = findViewById(R.id.progress_loading);

        //RadioGroup match
        radioGroupSex = findViewById(R.id.radio_group_sex);

        //Group match
        groupRole = findViewById(R.id.group_role);
        listeners();
        if (isAdmin || getSharedPreferencesBool(HomeActivity.KEY_IS_FIRST_TIME)) {
            showSectionRole();
            isAdmin = true;
        }
    }

    private void listeners() {
        btnRegister.setOnClickListener(view -> register());
        btnLogin.setOnClickListener(view -> finish());
    }

    public void register() {
        if (isValidaFields()) {
            new TaskRegister().execute();
        }
    }

    class TaskRegister extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ContentValues register = new ContentValues();
            register.put("nombres", getInputNames());
            register.put("apellidos", getInputLastNames());
            register.put("dni", getInputDNI());
            register.put("telefono", getInputCellPhone());
            register.put("sexo", getSelectSex());
            register.put("username", getInputUser());
            register.put("contrasenia", getInputPassword());
            register.put("idrol", getRole());
            return registerDatabase(register);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideLoading();
            if (aBoolean) {
                cleanView();
                Toast.makeText(RegisterActivity.this, "Se cargaron los datos del usuario", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean registerDatabase(ContentValues contentValues) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "upn", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.insert("usuario", null, contentValues);
        bd.close();
        return true;
    }

    private void showSectionRole() {
        groupRole.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
    }

    public boolean getSharedPreferencesBool(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.dioses.pc07", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    private void cleanView() {
        tiedtNames.setText("");
        tiedtLastName.setFocusable(true);
        tiedtDni.setText("");
        tiedtCellPhone.setText("");
        tiedtUser.setText("");
        tiedtPassword.setText("");

        radioButtonMan.setChecked(false);
        radioButtonWoman.setChecked(false);

        checkBoxAdmin.setChecked(false);
        checkBoxUserNormal.setChecked(false);
    }

    private boolean isValidaFields() {
        boolean isCorrect = true;
        if (TextUtils.isEmpty(getInputNames().trim())) {
            setErrorField(tilNames, "Campo requerido");
            isCorrect = false;
        } else {
            cleanErrorField(tilNames);
        }
        if (TextUtils.isEmpty(getInputLastNames().trim())) {
            setErrorField(tilLastName, "Campo requerido");
            isCorrect = false;
        } else {
            cleanErrorField(tilLastName);
        }
        if (TextUtils.isEmpty(getInputDNI().trim())) {
            setErrorField(tilDni, "Campo requerido");
            isCorrect = false;
        } else if (getInputDNI().length() < 8) {
            setErrorField(tilDni, "Ingrese correctamente su DNI");
            isCorrect = false;
        } else {
            cleanErrorField(tilDni);
        }
        if (TextUtils.isEmpty(getInputCellPhone().trim())) {
            setErrorField(tilCellPhone, "Campo requerido");
            isCorrect = false;
        } else if (tiedtCellPhone.length() < 9) {
            setErrorField(tilCellPhone, "Ingrese correctamente su Número");
            isCorrect = false;
        } else {
            cleanErrorField(tilCellPhone);
        }
        if (TextUtils.isEmpty(getInputUser().trim())) {
            setErrorField(tilUser, "Campo requerido");
            isCorrect = false;
        } else {
            cleanErrorField(tilUser);
        }
        if (TextUtils.isEmpty(getInputPassword().trim())) {
            setErrorField(tilPassword, "Campo requerido");
            isCorrect = false;
        } else {
            cleanErrorField(tilPassword);
        }
        if (!radioButtonMan.isChecked() && !radioButtonWoman.isChecked()) {
            Toast.makeText(this, "Debes selecciona un género", Toast.LENGTH_SHORT).show();
            isCorrect = false;
        }

        if (isAdmin && !checkBoxAdmin.isChecked() && !checkBoxUserNormal.isChecked()) {
            Toast.makeText(this, "Debes selecciona un rol", Toast.LENGTH_SHORT).show();
            isCorrect = false;
        }
        return isCorrect;
    }

    private int getRole() {
        return checkBoxAdmin.isChecked() ? 0 : 1;
    }

    private int getSelectSex() {
        return radioGroupSex.getCheckedRadioButtonId() == R.id.radio_button_man ? 0 : 1;
    }

    private String getInputNames() {
        return Objects.requireNonNull(tiedtNames.getText()).toString();
    }

    private String getInputLastNames() {
        return Objects.requireNonNull(tiedtLastName.getText()).toString();
    }

    private String getInputDNI() {
        return Objects.requireNonNull(tiedtDni.getText()).toString();
    }

    private String getInputCellPhone() {
        return Objects.requireNonNull(tiedtCellPhone.getText()).toString();
    }

    private String getInputUser() {
        return Objects.requireNonNull(tiedtUser.getText()).toString();
    }

    private String getInputPassword() {
        return Objects.requireNonNull(tiedtPassword.getText()).toString();
    }

    private void setErrorField(TextInputLayout textInputLayout, String messaerror) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(messaerror);
    }

    private void cleanErrorField(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(null);
    }

    private void showLoading() {
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressLoading.setVisibility(View.GONE);
    }
}