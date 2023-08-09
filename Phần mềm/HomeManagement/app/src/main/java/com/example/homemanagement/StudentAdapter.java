package com.example.homemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.UserViewHolder> {

    private List<Student> mListStudents;

    public StudentAdapter(List<Student> mListStudents) {
        this.mListStudents = mListStudents;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Student student = mListStudents.get(position);
        if(student == null){
            return;
        }
        holder.tvId.setText("ID: " + student.getId());
        holder.tvName.setText("Name: " + student.getName());
        holder.tvTeam.setText("Team: " + student.getTeam());
        holder.tvAbsent.setText("Absent: " + student.getAbsent());
    }

    @Override
    public int getItemCount() {
        if (mListStudents != null){
            return mListStudents.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView tvId;
        private TextView tvName;
        private TextView tvTeam;
        private TextView tvAbsent;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvTeam = itemView.findViewById(R.id.tvTeam);
            tvAbsent = itemView.findViewById(R.id.tvAbsent);
        }
    }

}
