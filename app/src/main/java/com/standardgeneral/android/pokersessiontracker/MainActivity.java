package com.standardgeneral.android.pokersessiontracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText editId;
    EditText editSessionHours;
    EditText editSessionProfit;
    Button btnAdd;
    Button btnDelete;
    Button btnModify;
    Button btnView;
    Button btnViewAll;
    Button btnShowInfo;
    Button buttonGraph;
    DatePicker datePicker;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String ID = "ID";
        final String SESSIONHOURS = "SESSIONHOURS";
        final String SESSIONPROFIT = "SESSIONPROFIT";
        final String SESSIONHOURLY = "SESSIONHOURLY";

        editId =(EditText)findViewById(R.id.editId);
        editSessionHours =(EditText)findViewById(R.id.editSessionHours);
        editSessionProfit =(EditText)findViewById(R.id.editSessionProfit);
        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnDelete=(Button)findViewById(R.id.btnDelete);
        btnModify=(Button)findViewById(R.id.btnModify);
        btnView=(Button)findViewById(R.id.btnView);
        btnViewAll=(Button)findViewById(R.id.btnViewAll);
        buttonGraph=(Button)findViewById(R.id.buttonGraph);
        datePicker = (DatePicker)findViewById(R.id.datePicker1);
        //btnShowInfo=(Button)findViewById(R.id.btnShowInfo);



        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = openOrCreateDatabase("PokerDB", Context.MODE_PRIVATE, null);
                //db.execSQL("DROP TABLE POKERSESSIONS;");

                db.execSQL("CREATE TABLE IF NOT EXISTS POKERSESSIONS " +
                        "(  ID              INT PRIMARY KEY NOT NULL, " +
                        "   SESSIONDATE     LONG NOT NULL, " +
                        "   SESSIONHOURS    INT NOT NULL, " +
                        "   SESSIONPROFIT   INT NOT NULL, " +
                        "   SESSIONHOURLY   REAL);");
                Calendar cal = Calendar.getInstance();
                cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                long sessionDate = cal.getTimeInMillis();

                // Adding a record
                if(view==btnAdd)
                {
                    // Checking empty fields
                    if(editId.getText().toString().trim().length()==0||
                            editSessionHours.getText().toString().trim().length()==0||
                            editSessionProfit.getText().toString().trim().length()==0)
                    {
                        showMessage("Error", "Please enter all values");
                        return;
                    }
                    Cursor c=db.rawQuery("SELECT * FROM POKERSESSIONS WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'", null);
                    if(c.moveToFirst())
                    {
                        showMessage("Error", "ID already exists.");
                        return;
                    }
                    int sessionProfit = Integer.parseInt(editSessionProfit.getText().toString());
                    int sessionHours = Integer.parseInt(editSessionHours.getText().toString());
                    float sessionHourly = sessionProfit / sessionHours;

                    // Inserting record
                    db.execSQL("INSERT INTO POKERSESSIONS VALUES('"+ editId.getText()+ "','" + sessionDate + "','"+ sessionHours +
                            "','"+ sessionProfit+"','" + sessionHourly + "');");
                    showMessage("Success", "Record added");
                    clearText();
                }
// Deleting a record
                if(view==btnDelete)
                {
                    // Checking empty roll number
                    if(editId.getText().toString().trim().length()==0)
                    {
                        showMessage("Error", "Please enter Id");
                        return;
                    }
                    // Searching roll number
                    Cursor c=db.rawQuery("SELECT * FROM POKERSESSIONS WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'", null);
                    if(c.moveToFirst())
                    {
                        // Deleting record if found
                        db.execSQL("DELETE FROM POKERSESSIONS WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'");
                        showMessage("Success", "Record Deleted");
                    }
                    else
                    {
                        showMessage("Error", "Invalid Id");
                    }
                    clearText();
                }
// Modifying a record
                if(view==btnModify)
                {
                    // Checking empty roll number
                    if(editId.getText().toString().trim().length()==0||
                            editSessionHours.getText().toString().trim().length()==0||
                            editSessionProfit.getText().toString().trim().length()==0)
                    {
                        showMessage("Error", "Please enter all values.");
                        return;
                    }
                    // Searching roll number
                    Cursor c=db.rawQuery("SELECT * FROM POKERSESSIONS WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'", null);
                    if(c.moveToFirst())
                    {
                        // Modifying record if found
                        db.execSQL("UPDATE POKERSESSIONS SET SESSIONHOURS ='"+ Integer.parseInt(editSessionHours.getText().toString())+
                                "',SESSIONDATE='" + sessionDate +
                                "',SESSIONPROFIT='"+ Integer.parseInt(editSessionProfit.getText().toString())+
                                "' WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'");
                        showMessage("Success", "Record Modified");
                    }
                    else
                    {
                        showMessage("Error", "Invalid ID");
                    }
                    clearText();
                }
// Viewing a record
                if(view==btnView)
                {
                    // Checking empty ID
                    if(editId.getText().toString().trim().length()==0)
                    {
                        showMessage("Error", "Please enter ID");
                        return;
                    }
                    // Searching roll number
                    Cursor c=db.rawQuery("SELECT * FROM POKERSESSIONS WHERE ID ='"+ Integer.parseInt(editId.getText().toString())+"'", null);
                    if(c.moveToFirst())
                    {
                        // Displaying record if found
                        long sessionDateFromDb = c.getLong(1);
                        Calendar calFromDb = Calendar.getInstance();
                        calFromDb.setTimeInMillis(sessionDateFromDb);
                        datePicker.updateDate(calFromDb.get(Calendar.YEAR), calFromDb.get(Calendar.MONTH),calFromDb.get(Calendar.DAY_OF_MONTH));
                        editSessionHours.setText(c.getString(2));
                        editSessionProfit.setText(c.getString(3));

                    }
                    else
                    {
                        showMessage("Error", "Invalid ID");
                        clearText();
                    }
                }
// Viewing all records
                if(view==btnViewAll)
                {
                    // Retrieving all records
                    Cursor c=db.rawQuery("SELECT * FROM POKERSESSIONS", null);
                    // Checking if no records found
                    if(c.getCount()==0)
                    {
                        showMessage("Error", "No records found");
                        return;
                    }
                    // Appending records to a string buffer
                    StringBuffer buffer=new StringBuffer();
                    int allTimeProfit = 0;
                    int allTimeHours = 0;

                    while(c.moveToNext())
                    {
                        Calendar calTmp = Calendar.getInstance();
                        calTmp.setTimeInMillis(c.getLong(1));
                        String date = "" + calTmp.get(Calendar.YEAR)+"/"+(calTmp.get(Calendar.MONTH)+1)+"/"+calTmp.get(Calendar.DAY_OF_MONTH);

                        buffer.append("__________________\n");
                        buffer.append("ID: "+c.getString(0)+"\n");
                        buffer.append("Date: "+date+"\n");
                        buffer.append("Hours: "+c.getString(2)+"\n");
                        buffer.append("Profit: $"+c.getString(3)+"\n");
                        buffer.append("Hourly Profit: $"+c.getString(4)+"\n");


                        allTimeProfit += Integer.parseInt(c.getString(3));
                        allTimeHours += Integer.parseInt(c.getString(2));

                    }
                    buffer.append("__________________\n");
                    buffer.append("All Time Profit: $"+allTimeProfit+"\n");
                    buffer.append("Total Hours Played: "+allTimeHours+"\n");
                    buffer.append("Average Profit per Hour: $"+(allTimeProfit/allTimeHours)+"\n");



                    // Displaying all records
                    showMessage("Poker Sessions Details", buffer.toString());
                }
// Displaying info
                if(view==btnShowInfo)
                {
                    showMessage("Poker Session Tracker", "Developed By Brian");
                }
                if(view==buttonGraph)
                {
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    MainActivity.this.startActivity(intent);
                }

            }
        };



        btnAdd.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        btnModify.setOnClickListener(onClickListener);
        btnView.setOnClickListener(onClickListener);
        btnViewAll.setOnClickListener(onClickListener);
        buttonGraph.setOnClickListener(onClickListener);

    }

    public void showMessage(String title,String message)
    {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Sweet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                /*.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })*/
                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();
    }

    public void clearText()
    {
        editId.setText("");
        editSessionHours.setText("");
        editSessionProfit.setText("");
        editId.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_data) {

            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("Please Confirm")
                    .setMessage("Delete all data?")
                    .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.execSQL("DROP TABLE POKERSESSIONS;");
                                dialog.cancel();
                            }
                        })
                    .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
