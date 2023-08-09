package com.example.homemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.StudentViewHolder>{

    private List<Student> mListStudents;
    private IClickListener mIClickListener;

    public interface IClickListener{
        void onClickUpdateItem(Student student);
    }

    public AttendanceAdapter(List<Student> mListStudents, IClickListener listener) {
        this.mListStudents = mListStudents;
        this.mIClickListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_attendance, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = mListStudents.get(position);
        if(student == null){
            return;
        }
        holder.tvId.setText("ID: " + student.getId());
        holder.tvName.setText("Name: " + student.getName());
        holder.tvAbsent.setText("Absent: " + student.getAbsent());
        holder.btnMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener.onClickUpdateItem(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListStudents != null){
            return mListStudents.size();
        }
        return 0;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{

        private TextView tvId;
        private TextView tvName;
        private Button btnMiss;
        private TextView tvAbsent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            btnMiss = itemView.findViewById(R.id.btnMiss);
            tvAbsent = itemView.findViewById(R.id.tvAbsent);
        }
    }
}
