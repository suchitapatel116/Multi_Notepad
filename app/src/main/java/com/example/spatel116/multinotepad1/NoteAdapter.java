package com.example.spatel116.multinotepad1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by spatel116 on 2/7/2018.
 */

public class NoteAdapter extends RecyclerView.Adapter<NotesViewHolder>
{
    private static final String TAG = "NoteAdapter";
    //This two objects are needed, so that the adapter knows that it is working with main activity and notes view holder
    private ArrayList<Note> list_of_notes;  //can use any collection like hashSet
    private MainActivity mainActivity;

    public NoteAdapter(MainActivity mnActi, ArrayList<Note> list)
    {
        mainActivity = mnActi;
        list_of_notes = list;
    }

    //It creates and inflates brand new element to stuff into the recycler view
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Create new");

        //The template layout of the list is passed here and not the main activity where the view is to displayed
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list, parent, false);

        //can set reference to on click listeners here, and when someone clicks you can say that main activity will take care of it
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                //when user clicks on the note it should open to edit

                int pos = mainActivity.getRecyclerView().getChildAdapterPosition(view);
                mainActivity.doEditNote(view, pos);
            }
        });

        //Gives prompt for deleting the note
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: ");

                int pos = mainActivity.getRecyclerView().getChildAdapterPosition(view);
                mainActivity.deleteNote(view, pos);

                return true;
            }
        });

        //return viewHolder object and passing the view object created here
        return new NotesViewHolder(itemView);
    }

    //It just takes the data from Note class and gives to view/layout object
    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {

        //It will fetch the note with index = position
        Note noteItem = list_of_notes.get(position);
        holder.tvTitle.setText(noteItem.getTitle());
        holder.tvDescription.setText(noteItem.getDescription());
        holder.tvDate.setText(noteItem.getDateTime());
    }

    @Override
    public int getItemCount() {
        return list_of_notes.size();
    }

    /*
    //It would elements in reverse order so that the latest are printed first
    @Override
    public long getItemId(int position) {
        return super.getItemId(getItemCount() - position);
    }
    */

    public void refreshList(ArrayList<Note> list)
    {
        this.list_of_notes = list;
        notifyDataSetChanged();
    }
}
