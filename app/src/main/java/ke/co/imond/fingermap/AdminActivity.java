package ke.co.imond.fingermap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fgtit.fpcore.FPMatch;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button addEmployee, viewEmployee, addAddmin;
    ImageView FingerThumb, FingerIndex;
    private SoundPool soundPool;
    private int soundIda,soundIdb;
    private boolean soundflag=false;

    private static final int RE_WORK0 = 0;
    private static final int RE_WORK1 = 1;
    private static final int RE_WORK2 = 2;
    private PowerManager.WakeLock wakeLock;

    private Timer startTimer;
    private TimerTask startTask;
    Handler startHandler;

    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        addEmployee = (Button)findViewById(R.id.addEmployee);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, EnrollActivity.class);
                startActivity(intent);
            }
        });

        viewEmployee = (Button)findViewById(R.id.viewEmployee);
        viewEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, EmployeeViewActivity.class);
                startActivity(intent);
            }
        });

        addAddmin = (Button)findViewById(R.id.viewRecords);
        addAddmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(AdminActivity.this, ClockinActivity.class);
//                startActivityForResult(intent, RE_WORK1);
                Toast.makeText(AdminActivity.this, "Implementation On the way", Toast.LENGTH_SHORT).show();
            }
        });

        FingerThumb = (ImageView)findViewById(R.id.FineThumb);
        FingerThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ClockinActivity.class);
                startActivityForResult(intent, RE_WORK1);
            }
        });

        FingerIndex = (ImageView)findViewById(R.id.FineIndex);
        FingerIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ClockoutActivity.class);
                startActivityForResult(intent, RE_WORK2);
            }
        });

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "sc");
        wakeLock.acquire();

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.start, 1);
        soundIdb = soundPool.load(this, R.raw.stop, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundflag = true;    //  ��ʾ�������
            }
        });

        if(FPMatch.getInstance().InitMatch()==0){
            Toast.makeText(getApplicationContext(), "Init Matcher Fail!", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getApplicationContext(), "Init Matcher OK!", Toast.LENGTH_SHORT).show();
        }

//        UpdateApp.getInstance().setAppContext(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RE_WORK0:
                break;
            case RE_WORK1:
                break;
            case RE_WORK2:
                break;
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        TimerStart();
    }

    public void TimerStart()
    {
        startTimer = new Timer();
        startHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        startTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                startHandler.sendMessage(message);
            }
        };
        startTimer.schedule(startTask, 5000, 5000);
    }

    public void TimeStop()
    {
        if (startTimer!=null)
        {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeStop();

        wakeLock.release(); //������ֻ���
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        wakeLock.release();//������ֻ���
    }

    @Override
    protected void onResume() {
        super.onResume();

        wakeLock.acquire(); //���ñ��ֻ���
    }
}
