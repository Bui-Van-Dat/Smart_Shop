package com.example.homemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.UserViewHolder> {

    private List<Leave> mListLeaves;

    public LeaveAdapter(List<Leave> mListLeaves) {
        this.mListLeaves = mListLeaves;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leave, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Leave leave = mListLeaves.get(position);
        if(leave == null){
            return;
        }
        holder.tvName.setText("Name: " + leave.getName());
        holder.tvId.setText("ID: " + leave.getId());
        holder.tvDay.setText("Date: " + leave.getDate());
        holder.tvReason.setText("Reason: " + leave.getReason());
    }

    @Override
    public int getItemCount() {
        if (mListLeaves != null){
            return mListLeaves.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView tvId;
        private TextView tvName;
        private TextView tvDay;
        private TextView tvReason;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvReason = itemView.findViewById(R.id.tvReason);
        }
    }

}
