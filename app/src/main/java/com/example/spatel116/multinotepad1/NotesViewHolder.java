package com.example.spatel116.multinotepad1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by spatel116 on 2/7/2018.
 */

public class NotesViewHolder extends RecyclerView.ViewHolder
{
    //Android framework needs ViewHolder data members to be public
    public TextView tvTitle;
    public TextView tvDescription;
    public TextView tvDate;

    public NotesViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvDescription = (TextView) itemView.findViewById(R.id.tv_desc);
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
    }
}
