package com.dioses.pc07;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<User> mUser;
    UserClickListener mUserListener;

    public CustomAdapter(UserClickListener mUserListener) {
        this.mUserListener = mUserListener;
    }

    public void updateUsers(List<User> mUser) {
        this.mUser = mUser;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view, this.mUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(mUser.get(holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return mUser == null ? 0 : mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private UserClickListener mItemListener;
        private TextView textRole, textNames, textLastNames, textUsername, textPassword, textDni, textCellPhone, textSex;
        User mUser;

        public ViewHolder(@NonNull View itemView, UserClickListener userClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            textRole = itemView.findViewById(R.id.text_role);
            textNames = itemView.findViewById(R.id.text_name);
            textLastNames = itemView.findViewById(R.id.text_last_name);
            textUsername = itemView.findViewById(R.id.text_username);
            textPassword = itemView.findViewById(R.id.text_password);
            textDni = itemView.findViewById(R.id.text_dni);
            textCellPhone = itemView.findViewById(R.id.text_cell_phone);
            textSex = itemView.findViewById(R.id.text_sexo);
            this.mItemListener = userClickListener;
        }

        private void bindView(User data) {
            mUser = data;
            textRole.setText(data.getIdrol() == 0 ? "Administrador" : "Usuario");
            textNames.setText(data.getNombres());
            textLastNames.setText(data.getApellidos());
            textDni.setText(data.getDni());
            textCellPhone.setText(data.getTelefono());
            textUsername.setText(data.getUsername());
            textPassword.setText(data.getConstrasenia());
            textSex.setText(data.getSexo() == 0 ? "Hombre" : "Mujer");
        }

        @Override
        public void onClick(View view) {
            mItemListener.onClick(mUser);
        }
    }
}
