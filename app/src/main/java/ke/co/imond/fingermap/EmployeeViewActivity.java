package ke.co.imond.fingermap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.imond.fingermap.Data.Employee;

public class EmployeeViewActivity extends AppCompatActivity {

    ListView listViewEmployees;

    DatabaseReference mDatabase;
    List employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employeeview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDatabase = FirebaseDatabase.getInstance().getReference("RevolutionBios");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewEmployees = (ListView)findViewById(R.id.listViewemployees);
        employeeList = new ArrayList();
        mDatabase.child("employees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employeeList.clear();

                for (DataSnapshot empSnapshop : dataSnapshot.getChildren()){
                    Employee employee = empSnapshop.getValue(Employee.class);

                    employeeList.add(employee);
                }

                EmployeeList adapter = new EmployeeList(EmployeeViewActivity.this, employeeList);
                listViewEmployees.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        Toast.makeText(this, "studentList"+studentList, Toast.LENGTH_SHORT).show();

    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//
//    }

}
