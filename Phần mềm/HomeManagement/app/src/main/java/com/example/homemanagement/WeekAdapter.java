package com.example.homemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    private List<Week> mListWeeks;
    private IClickListener2 mIClickListener2;

    public interface IClickListener2{
        void onClickUpdateItem(Week week);
    }
    public WeekAdapter(List<Week> mListWeeks, WeekAdapter.IClickListener2 listener) {
        this.mListWeeks = mListWeeks;
        this.mIClickListener2 = listener;
    }

    @NonNull
    @Override
    public WeekAdapter.WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekAdapter.WeekViewHolder holder, int position) {
        Week week = mListWeeks.get(position);
        if(week == null){
            return;
        }
        holder.tvWeek.setText("Day: " + week.getNumberWeek());
        holder.tvDate.setText("Date: " + week.getDateCreate());
        holder.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListener2.onClickUpdateItem(week);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListWeeks != null){
            return mListWeeks.size();
        }
        return 0;
    }

    public class WeekViewHolder extends RecyclerView.ViewHolder{

        private TextView tvWeek;
        private TextView tvDate;
        private Button btnOpen;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tvWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnOpen = itemView.findViewById(R.id.btnOpen);
        }
    }
}
