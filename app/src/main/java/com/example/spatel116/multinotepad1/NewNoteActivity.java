package com.example.spatel116.multinotepad1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NewNoteActivity extends AppCompatActivity {

    private static final String TAG = "NewNoteActivity";
    private EditText ed_tit;
    private EditText ed_desc;
    private Note noteObj;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        ed_tit = (EditText)findViewById(R.id.ed_ntitle);
        ed_desc = (EditText)findViewById(R.id.ed_ndesc);

        //Get the note object data from the main activity
        intent = getIntent();
        if(intent.hasExtra("note_obj")) {
            noteObj = (Note) intent.getSerializableExtra("note_obj");
            ed_tit.setText(noteObj.getTitle());
            ed_desc.setText(noteObj.getDescription());
        }
    }

    //To create the menu based on the layout defined in action_menu_2.xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item3_save:

                updateData();
                finish();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if(isNoteEdited()) {
            AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);

            dialog_builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateData();
                    finish();
                }
            });
            dialog_builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            dialog_builder.setTitle("Save Note");
            dialog_builder.setMessage("Your note is not saved. Do you want to save ' " + ed_tit.getText().toString() + " ' now?");

            AlertDialog dialog = dialog_builder.create();
            dialog.show();
        }
        else
            super.onBackPressed();
    }

    private boolean isNoteEdited()
    {
        Log.d(TAG, "isNoteEdited: ");
        boolean retVal = true;

        if(ed_tit.getText().toString().equals("") && ed_desc.getText().toString().equals("")) {
            retVal = false;
            Log.d(TAG, "isNoteEdited: false");
        }
            
        else if(noteObj.getTitle() != null && noteObj.getDescription() != null
                && noteObj.getTitle().toString().equals(ed_tit.getText().toString())
                && noteObj.getDescription().toString().equals(ed_desc.getText().toString()))
            retVal = false;

        return retVal;
    }

    private void updateData()
    {
        Log.d(TAG, "updateData: ");

        //Condition for checking if the data is same as previous then dont save
        if(!isNoteEdited()) {
            setResult(RESULT_CANCELED);
        }
        else {
            Log.d(TAG, "updateData: here======");
            //Save the note here in json file
            noteObj.setTitle(ed_tit.getText().toString());
            noteObj.setDescription(ed_desc.getText().toString());

            intent.putExtra("updated_obj", noteObj);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    protected void onPause() {
        updateData();
        super.onPause();
    }
}
