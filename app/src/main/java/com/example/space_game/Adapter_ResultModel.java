package com.example.space_game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_ResultModel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Result> results;
    private OnItemClickListener mItemClickListener;

    public Adapter_ResultModel(Context context, ArrayList<Result> results){
        this.context = context;
        this.results = results;
    }

    public void updateList(ArrayList<Result> results){
        this.results = results;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if(holder instanceof ViewHolder){
            final Result result = getItem(position);
            final ViewHolder genericViewHolder = (ViewHolder)holder;
            genericViewHolder.result_LBL_title.setText(result.toString());
        }
    }

    @Override
    public int getItemCount() {
        if(results.size() > 10){
            return 10;
        }else {
            return results.size();
        }
    }

    private Result getItem(int position){
        return results.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView result_LBL_title;

        public ViewHolder(final View itemView){
            super(itemView);
            this.result_LBL_title = itemView.findViewById(R.id.result_LBL_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), getItem(getAdapterPosition()));
                }
            });
        }
    }

    public void removeAt(int position){
        results.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, results.size());
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, Result result);
    }


}
