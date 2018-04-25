package ke.co.imond.fingermap;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.fpcore.FPMatch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.AsyncFingerprint;
import android_serialport_api.SerialPortManager;
import ke.co.imond.fingermap.Data.Clockins;
import ke.co.imond.fingermap.Data.ImageSimpleAdapter;
import ke.co.imond.fingermap.Data.Employee;
import ke.co.imond.fingermap.utils.ExtApi;

public class ClockinActivity extends AppCompatActivity {

    private TextView tvFpStatus;
    private ImageView fpImage;

    private ListView listView1;
    private ArrayList<HashMap<String, Object>> mData1;
    private SimpleAdapter adapter1;

    private ListView listView2;
    private ArrayAdapter<String> mListArrayAdapter2;

    private AsyncFingerprint vFingerprint;
    private boolean			 bIsUpImage=true;
    private boolean			 bIsCancel=false;
    private boolean			 bfpWork=false;

    private Timer startTimer;
    private TimerTask startTask;
    private Handler startHandler;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    DatabaseReference mDatabase;
    List userList;
    List fp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clockin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference("RevolutionBios");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userList = new ArrayList();

        listView1=(ListView) findViewById(R.id.listView1);
        mData1 = new ArrayList<HashMap<String, Object>>();
        adapter1 = new ImageSimpleAdapter(this,mData1,R.layout.listview_signitem,
                new String[]{"title","info","dts","img"},
                new int[]{R.id.title,R.id.info,R.id.dts,R.id.img});
        listView1.setAdapter(adapter1);

        mListArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.list_item);
        listView2 = (ListView) findViewById(R.id.listView2);
        listView2.setAdapter(mListArrayAdapter2);

        tvFpStatus = (TextView)findViewById(R.id.textView1);
        tvFpStatus.setText("");
        fpImage = (ImageView)findViewById(R.id.imageView1);
        fpImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Enable the NFC function in the system settings!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};

        AddStatus(getString(R.string.txt_fpbegin));
        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
        FPInit();
        FPProcess();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,null);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent){
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        final String cardsn=
                Integer.toHexString(sn[0]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[1]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[2]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[3]&0xFF).toUpperCase();
        SetStatus(getString(R.string.txt_cardsn)+cardsn);
        Query query = mDatabase.child("employees");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot empSnapshot : dataSnapshot.getChildren()) {
                        Employee employee = empSnapshot.getValue(Employee.class);

//                        for(int i=0;i<studentList.size();i++){
                            assert employee != null;
                            if(employee.getEmpNFC().contains(cardsn)){
                                AddPersonItem(employee);
                                SetStatus(getString(R.string.txt_cardmatch));
                            }
//
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ScrollListViewToBottom() {
        listView1.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView1.setSelection(adapter1.getCount() - 1);
            }
        });
    }

    private void AddStatus(String text){
        mListArrayAdapter2.add(text);
    }

    private void SetStatus(String text){
        mListArrayAdapter2.clear();
        mListArrayAdapter2.add(text);
    }

    private void AddPersonItem(final Employee employee){
        //        get the clockin ID
        Query query = mDatabase.child("clockins");
        query.orderByChild("userID").equalTo(employee.getEmpID()).addListenerForSingleValueEvent(new ValueEventListener() {
            String ClockID = mDatabase.child("clockins").push().getKey();
            String UserID = employee.getEmpID();
            String NFC=employee.getDesignation();
            String name=employee.getLast_name()+" "+employee.getFirst_name();
            String ClockinTime=ExtApi.getStringDate();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot clockinSnapshot : dataSnapshot.getChildren()) {
                        Clockins clockin = clockinSnapshot.getValue(Clockins.class);

                        try {
                            assert clockin != null;
                            if (getDate(clockin.getClocktime()).equals(getDate(ClockinTime))){
                                Toast.makeText(ClockinActivity.this, "Cant Clockin twice in a day", Toast.LENGTH_SHORT).show();
                            }
                            else
                                mDatabase.child("clockins").child(ClockID).setValue((new Clockins(UserID, ClockID, ClockinTime)));
                                Toast.makeText(ClockinActivity.this, "awesome", Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }else
                    mDatabase.child("clockins").child(ClockID).setValue((new Clockins(UserID, ClockID, ClockinTime)));

                //                Toast.makeText(ClockinActivity.this, "awesome", Toast.LENGTH_SHORT).show();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", name);
                map.put("info", NFC);
                map.put("dts", ClockinTime);
                map.put("img", ExtApi.LoadBitmap(getResources(),R.mipmap.admin));
                mData1.add(map);
                adapter1.notifyDataSetChanged();
                ScrollListViewToBottom();

            }

            public void onCancelled(DatabaseError databaseError){
                Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public static String getDate(String clocktime) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date theDate = format.parse(clocktime);

        Calendar myCal = new GregorianCalendar();
        myCal.setTime(theDate);

        int month = myCal.get(Calendar.DAY_OF_MONTH);
        int day = myCal.get(Calendar.MONTH) + 1;
        int year = myCal.get(Calendar.YEAR);

        return String.format("%d%02d%02d",day,month,year);
    }

    private void FPProcess(){
        if(!bfpWork){
            try {
                Thread.currentThread();
                Thread.sleep(500);
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            tvFpStatus.setText(getString(R.string.txt_fpplace));
            vFingerprint.FP_GetImageEx();
            bfpWork=true;
        }
    }

    private void FPInit(){
        //ָ\CEƴ\A6\C0\ED
        vFingerprint.setOnGetImageExListener(new AsyncFingerprint.OnGetImageExListener() {
            @Override
            public void onGetImageExSuccess() {
                if(bIsUpImage){
                    vFingerprint.FP_UpImageEx();
                    tvFpStatus.setText(getString(R.string.txt_fpdisplay));
                }else{
                    tvFpStatus.setText(getString(R.string.txt_fpprocess));
                    vFingerprint.FP_GenCharEx(1);
                }
            }

            @Override
            public void onGetImageExFail() {
                if(!bIsCancel){
                    vFingerprint.FP_GetImageEx();
                    //SignLocalActivity.this.AddStatus("Error");
                }
            }
        });

        vFingerprint.setOnUpImageExListener(new AsyncFingerprint.OnUpImageExListener() {
            @Override
            public void onUpImageExSuccess(byte[] data) {
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                fpImage.setImageBitmap(image);
//                fpImage.setBackgroundDrawable(new BitmapDrawable(image));
                tvFpStatus.setText(getString(R.string.txt_fpprocess));
                vFingerprint.FP_GenCharEx(1);
            }

            @Override
            public void onUpImageExFail() {
                bfpWork=false;
                TimerStart();
            }
        });

        vFingerprint.setOnGenCharExListener(new AsyncFingerprint.OnGenCharExListener() {
            @Override
            public void onGenCharExSuccess(int bufferId) {
                tvFpStatus.setText(getString(R.string.txt_fpidentify));
                vFingerprint.FP_UpChar();
            }

            @Override
            public void onGenCharExFail() {
                tvFpStatus.setText(getString(R.string.txt_fpfail));
            }
        });

        vFingerprint.setOnUpCharListener(new AsyncFingerprint.OnUpCharListener() {

            @Override
            public void onUpCharSuccess(final byte[] model) {
                Query query = mDatabase.child("employees");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot empSnapshot : dataSnapshot.getChildren()) {
                                Employee employee = empSnapshot.getValue(Employee.class);

                                    if(employee.getFingerThumb().length()>=512){
                                        byte[] ref=ExtApi.Base64ToBytes(employee.getFingerThumb());
                                        if(FPMatch.getInstance().MatchTemplate(model, ref)>60){
                                            AddPersonItem(employee);
                                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                            break;
                                        }

                                    }
                                    if(employee.getFingerIndex().length()>=512){
                                        byte[] ref=ExtApi.Base64ToBytes(employee.getFingerIndex());
                                        if(FPMatch.getInstance().MatchTemplate(model, ref)>60){
                                            AddPersonItem(employee);
                                            tvFpStatus.setText(getString(R.string.txt_fpmatchok));
                                            break;
                                        }
                                    }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                bfpWork=false;
                TimerStart();
            }

            @Override
            public void onUpCharFail() {
                tvFpStatus.setText(getString(R.string.txt_fpmatchfail)+":-1");
                bfpWork=false;
                TimerStart();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            bIsCancel=true;
            SerialPortManager.getInstance().closeSerialPort();
            this.finish();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void TimerStart(){
        if(startTimer==null){
            startTimer = new Timer();
            startHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    TimeStop();
                    FPProcess();
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
            startTimer.schedule(startTask, 1000, 1000);
        }
    }

    public void TimeStop(){
        if (startTimer!=null)
        {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask=null;
        }
    }

}
