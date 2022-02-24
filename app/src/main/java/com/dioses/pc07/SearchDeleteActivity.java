package com.dioses.pc07;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class SearchDeleteActivity extends AppCompatActivity {
    private ProgressBar progressLoading;
    CustomAdapter mAdapter;
    private RecyclerView recyclerUser;
    String myDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_delete);
        progressLoading = findViewById(R.id.progress_loading);
        recyclerUser = findViewById(R.id.recycler_users);
        myDni = getIntent().getStringExtra(HomeActivity.EXTRA_DNI);

        mAdapter = new CustomAdapter(user -> {
            if (user.getDni().equals(myDni)) {
                Toast.makeText(this, "No puede eliminar su cuenta", Toast.LENGTH_SHORT).show();
            } else {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(SearchDeleteActivity.this, "upn", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.delete("usuario", "dni='" + user.getDni() + "'", null);
                bd.close();
                Toast.makeText(SearchDeleteActivity.this, "Se eliminó el usuario " + user.getNombres() + " " + user.getApellidos(), Toast.LENGTH_SHORT).show();
                new TaskGetListData().execute();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerUser.setLayoutManager(layoutManager);
        recyclerUser.setAdapter(mAdapter);
        recyclerUser.setHasFixedSize(true);
        new TaskGetListData().execute();
    }

    class TaskGetListData extends AsyncTask<Void, Void, List<User>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            return getDataAllUser();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            hideLoading();
            renderRecycler(users);


        }
    }

    private void renderRecycler(List<User> users) {
        mAdapter.updateUsers(users);
    }

    private List<User> getDataAllUser() {
        List<User> listUsers = new LinkedList<>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "upn", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor fila = bd.rawQuery("select nombres,apellidos,telefono,dni,sexo,username,contrasenia,idrol from usuario ", null);
        if (fila.moveToFirst()) {
            do {
                listUsers.add(new User(fila.getString(0), fila.getString(1), fila.getString(2),
                        fila.getString(3), fila.getInt(4), fila.getString(5),
                        fila.getString(6), fila.getInt(7)));
            } while (fila.moveToNext());

        }
        fila.close();
        return listUsers;
    }

    private void showLoading() {
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressLoading.setVisibility(View.GONE);
    }
}