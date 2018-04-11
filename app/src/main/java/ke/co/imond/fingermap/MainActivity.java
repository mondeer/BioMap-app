package ke.co.imond.fingermap;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    EditText userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText)findViewById(R.id.edit_text_email);
        String username = userName.getText().toString();

        loginButton = (Button)findViewById(R.id.login_with_password);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Hello"+username, Toast.LENGTH_SHORT).show();
//            }
//        });



        if ("admin".equals(userName.getText().toString()))
            authLog();
        if("".equals(userName.getText().toString()))
            adminLog();



    }

    public void adminLog(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, userName.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent admin = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(admin);
            }
        });

    }

    public void authLog() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, userName.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0B40000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

}
