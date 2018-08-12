package com.example.spatel116.multinotepad1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_NEW_NOTE = 111;
    private static final int REQUEST_CODE_EDIT_NOTE = 222;
    private ArrayList<Note> list_of_notes = new ArrayList<>();

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private Note note;

    private static int editNoteIndex = -1;
    private Intent intent_newNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        recyclerView = (RecyclerView)findViewById(R.id.rv_recyclerView);
        //create new adapter with reference to 'this' activity
        noteAdapter = new NoteAdapter(this, list_of_notes);

        recyclerView.setAdapter(noteAdapter);
        LinearLayoutManager mLayout = new LinearLayoutManager(this);

        //To display the list in reverse order.
        mLayout.setReverseLayout(true);
        mLayout.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayout);

        //note = loadFromFile();
        JsonAsyncTask asynctask = new JsonAsyncTask(this);
        //The exceute method call the doInBackground method, execute() will take comma separated input
        asynctask.execute(getString(R.string.filename));

        if(noteAdapter != null)
            noteAdapter.refreshList(list_of_notes);
    }

    //To get a reference in the NoteAdapter class when onclick method is used
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume: ");

        if(intent_newNote != null) {
            Log.d(TAG, "onResume: saved!!!!");
            note = (Note) intent_newNote.getSerializableExtra("updated_obj");
            saveNote(note, false);
            noteAdapter.notifyDataSetChanged();
        }
        if(noteAdapter != null)
            noteAdapter.refreshList(list_of_notes);
        super.onResume();
    }

    public void getDataFromAsync(ArrayList<Note> notes_list) {
        //Give the list obj to the main class list
        list_of_notes = notes_list;

        if(noteAdapter != null)
            noteAdapter.refreshList(list_of_notes);
    }

    //To create the menu based on the layout defined in action_menu_1.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater() method from activity
        //Layout is a compressed version and inflater expands it to get a real object
        getMenuInflater().inflate(R.menu.action_menu_1, menu);

        return true;
    }

    //Everytime any menu item gets selected following function is called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item1_info:
                Log.d(TAG, "onOptionsItemSelected: Info menu");

                Intent it = new Intent(this, AboutActivity.class);
                startActivity(it);
                break;
            case R.id.menu_item2_add:
                Log.d(TAG, "onOptionsItemSelected: Add menu");

                //Create new note
                note = new Note();

                //Create new activity for new note. New note so pass the file name.
                intent_newNote = new Intent(this, NewNoteActivity.class);
                //pass the note object and get the back the updated object
                intent_newNote.putExtra("note_obj", note);
                startActivityForResult(intent_newNote, REQUEST_CODE_NEW_NOTE);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_NEW_NOTE) {
            if(resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: REQUEST_CODE_NEW_NOTE");
                note = (Note) data.getSerializableExtra("updated_obj");

                if(note.getTitle().toString().isEmpty())
                    Toast.makeText(this,getString(R.string.not_saved),Toast.LENGTH_SHORT).show();
                else
                    //save the item
                    saveNote(note, false);

                Log.d(TAG, "onActivityResult: size== "+list_of_notes.size());
            }
            else
                Log.d(TAG, "onActivityResult: " + resultCode);
        }
        else if(requestCode == REQUEST_CODE_EDIT_NOTE) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: REQUEST_CODE_EDIT_NOTE");

                note = (Note) data.getSerializableExtra("updated_obj");
                Log.d(TAG, "doEditNote: "+ note.getTitle());

                if(note.getTitle().toString().isEmpty())
                    Toast.makeText(this,getString(R.string.not_saved),Toast.LENGTH_SHORT).show();
                else
                    saveNote(note, true);
                noteAdapter.notifyDataSetChanged();
            }
            else if(requestCode == RESULT_CANCELED)
                Log.d(TAG, "onActivityResult: not updated");
                
        }
        else
            Log.d(TAG, "onActivityResult: " + requestCode);

    }

    public boolean saveNote(Note noteObj, boolean isUpdate)
    {
        Log.d(TAG, "saveNote: ");
        boolean isSaved = true;
        try
        {
            Log.d(TAG, "saveNote: "+ list_of_notes.size());

            //Null noteObj indicates it is called from delete
            if(noteObj != null) {
                String formattedDate = new SimpleDateFormat("EEE MMM d, HH:mm a").format(Calendar.getInstance().getTime());

                //Add to the list when object is saved
                if(isUpdate) {
                    Log.d(TAG, "saveNote: is updated note "+editNoteIndex);

                    list_of_notes.get(editNoteIndex).setDateTime(formattedDate);
                    list_of_notes.get(editNoteIndex).setTitle(noteObj.getTitle());
                    list_of_notes.get(editNoteIndex).setDescription(noteObj.getDescription());
                    //Saved so now clear the defaults, now no need
                    editNoteIndex = -1;
                }
                else {
                    noteObj.setDateTime(formattedDate);
                    list_of_notes.add(noteObj);
                    Log.d(TAG, "saveNote: not updated");
                }
            }

            FileOutputStream outStrm = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);
            JsonWriter jWriter = new JsonWriter(new OutputStreamWriter(outStrm, getResources().getString(R.string.encoding)));

            jWriter.setIndent("     ");

            jWriter.beginArray();
            for(int i=0; i<list_of_notes.size(); i++) {

                jWriter.beginObject();

                jWriter.name("title").value(list_of_notes.get(i).getTitle());
                jWriter.name("description").value(list_of_notes.get(i).getDescription());
                jWriter.name("date_time").value(list_of_notes.get(i).getDateTime());

                jWriter.endObject();
            }
            jWriter.endArray();
            jWriter.close();


            //--Print the object written
            /// You do not need to do the below - it's just
            /// a way to see the JSON that is created.
            ///
            StringWriter sw = new StringWriter();
            jWriter = new JsonWriter(sw);
            jWriter.setIndent("  ");
            jWriter.beginArray();
            for(int i=0; i<list_of_notes.size(); i++) {

                jWriter.beginObject();
                jWriter.name("title").value(list_of_notes.get(i).getTitle());
                jWriter.name("description").value(list_of_notes.get(i).getDescription());
                jWriter.name("date_time").value(list_of_notes.get(i).getDateTime());
                jWriter.endObject();
                //Log.d(TAG, "saveNote: --"+list_of_notes.get(i).getTitle());
            }
            jWriter.endArray();
            jWriter.close();
            Log.d(TAG, "saveProduct: JSON:\n" + sw.toString());
            ///
            ///
        }
        catch(Exception e)
        {
            Log.d(TAG, "save Note: Exception while saving the file");
            e.printStackTrace();
            isSaved = false;
        }
        return isSaved;
    }

    public void deleteNote(final View v, final int pos)
    {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setTitle("Delete Note");
        dialog_builder.setMessage("Do you want to delete '"+ list_of_notes.get(pos).getTitle() +"'?");

        dialog_builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doDelete(v, pos);
            }
        });

        dialog_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });

        AlertDialog dialog = dialog_builder.create();
        dialog.show();
    }

    public void doDelete(View v, int pos)
    {
        Log.d(TAG, "doDelete: ");
        list_of_notes.remove(pos);
        noteAdapter.notifyDataSetChanged();

        //now remove from product.json file, call save item
        saveNote(null, false);
    }

    public void doEditNote(View v, int pos)
    {
        Log.d(TAG, "doEditNote: pos = "+ pos);

        editNoteIndex = pos;
        //Get the note data and pass the object
        note = list_of_notes.get(pos);

        Intent intent = new Intent(this, NewNoteActivity.class);
        intent.putExtra("note_obj", note);
        startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
    }

}
