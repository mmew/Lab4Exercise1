package th.ac.tu.siit.its333.lab4exercise1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    CourseDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new CourseDBHelper(this);
    }
    @Override
    protected void onResume() {
        super.onResume();


        SQLiteDatabase d = helper.getReadableDatabase();
        Cursor cursor = d.rawQuery("SELECT SUM(Credit) as TotalCredit,SUM(Credit*value) as TotalGP  FROM course ;", null);
        cursor.moveToFirst(); // get the first row
        double TotalCredit = cursor.getDouble(0);
        double TotalGP = cursor.getDouble(1);
        // This method is called when this activity is put foreground.

        TextView tv = (TextView)findViewById(R.id.tvGP);
        TextView tv1 = (TextView)findViewById(R.id.tvCR);
        TextView tv2 = (TextView)findViewById(R.id.tvGPA);


        double gpa = 0;
        if (TotalCredit > 0)
            gpa = TotalGP/TotalCredit;

        tv.setText(Double.toString(TotalGP));
        tv1.setText(Double.toString(TotalCredit));
        tv2.setText(String.format("%.2f",gpa));
    }

    public void buttonClicked(View v) {
        int id = v.getId();
        Intent i;

        switch(id) {
            case R.id.btAdd:
                i = new Intent(this, AddCourseActivity.class);
                startActivityForResult(i, 88);
                break;

            case R.id.btShow:
                i = new Intent(this, ListCourseActivity.class);
                startActivity(i);
                break;

            case R.id.btReset:
                SQLiteDatabase db = helper.getWritableDatabase();
                db.delete("course","",null);
                onResume();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                int credit = data.getIntExtra("credit", 0);
                String grade = data.getStringExtra("grade");

                SQLiteDatabase d = helper.getWritableDatabase();
                ContentValues r = new ContentValues();
                r.put("code",code);
                r.put("credit",credit);
                r.put("grade",grade);
                r.put("value",gradeToValue(grade) );
                d.insert("course",null,r);

            }
        }

        Log.d("course", "onActivityResult");
    }

    double gradeToValue(String g) {
        if (g.equals("A"))
            return 4.0;
        else if (g.equals("B+"))
            return 3.5;
        else if (g.equals("B"))
            return 3.0;
        else if (g.equals("C+"))
            return 2.5;
        else if (g.equals("C"))
            return 2.0;
        else if (g.equals("D+"))
            return 1.5;
        else if (g.equals("D"))
            return 1.0;
        else
            return 0.0;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
