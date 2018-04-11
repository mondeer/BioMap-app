package ke.co.imond.fingermap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.fpi.MtGpio;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.AsyncFingerprint;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortManager;
import ke.co.imond.fingermap.Data.Employee;
import ke.co.imond.fingermap.utils.ExtApi;

public class EnrollActivity extends AppCompatActivity {

    EditText pf_no, mfirst_name, mlast_name, department_name, section_name, section_id, designation;
    TextView mthumb_fp, mindex_fp;
    Button buttonEnroll;
    ImageButton thumbFP, indexFP;
    Button emp_nfc;
    private ListView mListView;
    private byte[] jpgbytes=null;

    private byte[] model1=new byte[512];
    private byte[] model2=new byte[512];
    private boolean isenrol1=false;
    private boolean isenrol2=false;
    private int savecount=0;

    private ImageView fpImage;
    private TextView  tvFpStatus;
    private AsyncFingerprint vFingerprint;
    private Dialog fpDialog;
    private int	iFinger=0;
    private boolean	bIsUpImage=true;
    private int count;
    private boolean bcheck=false;

    //����
    private SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private byte[] databuf=new byte[1024];
    private int datasize=0;
    private int soundIda;
    private SoundPool soundPool;

    private Timer TimerBarcode=null;
    private TimerTask TaskBarcode=null;
    private Handler HandlerBarcode;

    //NFC
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    private DatabaseReference mDatabase;

    public String CardSN="";

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pf_no = (EditText)findViewById(R.id.pf_no);
        mfirst_name = (EditText)findViewById(R.id.first_name);
        mlast_name = (EditText)findViewById(R.id.last_name);
        designation = (EditText)findViewById(R.id.designation);
        mthumb_fp = (TextView) findViewById(R.id.thumb_fp);
        mindex_fp = (TextView) findViewById(R.id.index_fp);
        emp_nfc = (Button)findViewById(R.id.cardButton);
        department_name = (EditText)findViewById(R.id.department_name);
        section_name = (EditText)findViewById(R.id.section_name);
        section_id = (EditText)findViewById(R.id.section_id);


        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference("RevolutionBios");

//        cardButton = (CheckedTextView) findViewById(R.id.cardButton);
        mindex_fp = (TextView) findViewById(R.id.index_fp);
        mthumb_fp = (TextView) findViewById(R.id.thumb_fp);
        thumbFP=(ImageButton) findViewById(R.id.thumbFP);
        thumbFP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FPDialog(1);
            }
        });

        indexFP=(ImageButton) findViewById(R.id.indexFP);
        indexFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FPDialog(2);
            }
        });

        openSerialPort();
        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
        FPInit();

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

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};

        buttonEnroll = (Button)findViewById(R.id.save);
        buttonEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckInputData(1)){
                    writeNewUser();
                    Toast.makeText(EnrollActivity.this, "Succesfully saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EnrollActivity.this, EnrollActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(EnrollActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeNewUser() {
        String EmpID = mDatabase.child("employees").push().getKey();
        String pf_nos = pf_no.getText().toString();
        String first_name = mfirst_name.getText().toString();
        String last_name = mlast_name.getText().toString();
        String designate = designation.getText().toString();
        String dept_name = department_name.getText().toString();
        String sect_name = section_name.getText().toString();
        String sect_id = section_id.getText().toString();
        String fingerThumb = ExtApi.BytesToBase64(model1, model1.length);
        String fingerIndex = ExtApi.BytesToBase64(model2, model2.length);

        Employee employee = new Employee(EmpID, pf_nos, first_name, last_name, designate, CardSN, fingerThumb, fingerIndex, dept_name, sect_name, sect_id);

        mDatabase.child("employees").child(EmpID).setValue(employee);

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,null);
    }

    private void FPDialog(int i){
        iFinger=i;
        if(iFinger==1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thumb fingerprint");
            final LayoutInflater inflater = LayoutInflater.from(this);
            View vl = inflater.inflate(R.layout.dialog_enrolfinger, null);
            fpImage = (ImageView) vl.findViewById(R.id.imageView1);
            tvFpStatus= (TextView) vl.findViewById(R.id.textview1);
            builder.setView(vl);
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //SerialPortManager.getInstance().closeSerialPort();
                    dialog.dismiss();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //SerialPortManager.getInstance().closeSerialPort();
                    dialog.dismiss();
                }
            });

            fpDialog = builder.create();
            fpDialog.setCanceledOnTouchOutside(false);
            fpDialog.show();

            FPProcess();
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Index fingerprint");
            final LayoutInflater inflater = LayoutInflater.from(this);
            View vl = inflater.inflate(R.layout.dialog_enrolfinger, null);
            fpImage = (ImageView) vl.findViewById(R.id.imageView1);
            tvFpStatus= (TextView) vl.findViewById(R.id.textview1);
            builder.setView(vl);
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //SerialPortManager.getInstance().closeSerialPort();
                    dialog.dismiss();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //SerialPortManager.getInstance().closeSerialPort();
                    dialog.dismiss();
                }
            });

            fpDialog = builder.create();
            fpDialog.setCanceledOnTouchOutside(false);
            fpDialog.show();

            FPProcess();
        }

    }

    private void FPInit(){
        //ָ�ƴ���
        vFingerprint.setOnGetImageExListener(new AsyncFingerprint.OnGetImageExListener() {
            @Override
            public void onGetImageExSuccess() {
                if(bcheck){
                    vFingerprint.FP_GetImageEx();
                }else{
                    if(bIsUpImage){
                        vFingerprint.FP_UpImageEx();
                        tvFpStatus.setText("Fingerprint image being displayed...");
                    }else{
                        tvFpStatus.setText("Being processed...");
                        vFingerprint.FP_GenCharEx(count);
                    }
                }
            }

            @Override
            public void onGetImageExFail() {
                if(bcheck){
                    bcheck=false;
                    tvFpStatus.setText("Please press fingerprint��");
                    vFingerprint.FP_GetImageEx();
                    count++;
                }else{
                    vFingerprint.FP_GetImageEx();
                }
            }
        });

        vFingerprint.setOnUpImageExListener(new AsyncFingerprint.OnUpImageExListener() {
            @Override
            public void onUpImageExSuccess(byte[] data) {
                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                fpImage.setImageBitmap(image);
                //fpImage.setBackgroundDrawable(new BitmapDrawable(image));
                vFingerprint.FP_GenCharEx(count);
                tvFpStatus.setText("Being processed...");
            }

            @Override
            public void onUpImageExFail() {
            }
        });

        vFingerprint.setOnGenCharExListener(new AsyncFingerprint.OnGenCharExListener() {
            @Override
            public void onGenCharExSuccess(int bufferId) {
                //vFingerprint.FP_Search(1, 1, 256);
                //tvFpStatus.setText("Being identified...");
                if (bufferId == 1) {
                    bcheck=true;
                    tvFpStatus.setText("Please lift your finger��");
                    vFingerprint.FP_GetImageEx();

                    //tvFpStatus.setText("Please press fingerprint��");
                    //vFingerprint.FP_GetImageEx();
                    //count++;
                } else if (bufferId == 2) {
                    vFingerprint.FP_RegModel();
                }
            }

            @Override
            public void onGenCharExFail() {
                tvFpStatus.setText("Failed to generate eigenvalues��");
            }
        });

        vFingerprint.setOnRegModelListener(new AsyncFingerprint.OnRegModelListener() {

            @Override
            public void onRegModelSuccess() {
                vFingerprint.FP_UpChar();
                tvFpStatus.setText("Synthetic template success��");
            }

            @Override
            public void onRegModelFail() {
                tvFpStatus.setText("Synthetic template failure��");
            }
        });

        vFingerprint.setOnUpCharListener(new AsyncFingerprint.OnUpCharListener() {

            @Override
            public void onUpCharSuccess(byte[] model) {
                //AdminEditActivity.this.model = model;
                if(iFinger==1){
                    mthumb_fp.setText("Registered");
                    System.arraycopy(model, 0, EnrollActivity.this.model1,0,512);
                    isenrol1=true;
                    //EnrollActivity.this.model1 = model;
                }else{
                    mindex_fp.setText("Registered");
                    System.arraycopy(model, 0, EnrollActivity.this.model2,0,512);
                    isenrol2=true;
                    //EnrollActivity.this.model2 = model;
                }
                tvFpStatus.setText("Successful registration��");

                //SerialPortManager.getInstance().closeSerialPort();
                fpDialog.cancel();
            }

            @Override
            public void onUpCharFail() {
                tvFpStatus.setText("Registration Failed��");
            }
        });

    }

    private void FPProcess(){

        count = 1;
        //model = null;
        tvFpStatus.setText("Press fingerprint��");
        try {
            Thread.currentThread();
            Thread.sleep(200);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        vFingerprint.FP_GetImageEx();
    }
    //һά����

    //����
    public void BarcodeOpen(){
        MtGpio.getInstance().BCPowerSwitch(true);
        MtGpio.getInstance().BCReadSwitch(true);
        try {
            Thread.currentThread();
            Thread.sleep(200);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        datasize=0;
        MtGpio.getInstance().BCReadSwitch(false);
        //DialogFactory.showProgressDialog(RoomCheckActivity.this,"Read barcodes...");
    }

    public void BarcodeClose(){
        if (mReadThread != null)
            mReadThread.interrupt();
        closeSerialPort();
        mSerialPort = null;
        MtGpio.getInstance().BCReadSwitch(true);
        MtGpio.getInstance().BCPowerSwitch(false);
        //DialogFactory.cancleProgressDialog();
    }

    public void openSerialPort(){
        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
        } catch (IOException e) {
        } catch (InvalidParameterException e) {
        }
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path = "/dev/ttyMT1";
            int baudrate = 9600;
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    private boolean CheckInputData(int type){
        int len=pf_no.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "Please enter the numbers", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=mfirst_name.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "First name Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=mlast_name.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "Last Name Required Also", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=designation.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "Class Level is Mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=department_name.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "Name of Guardian needed", Toast.LENGTH_SHORT).show();
            return false;
        }
        len=section_name.getText().toString().length();
        if(len<=0){
            Toast.makeText(EnrollActivity.this, "Guardian Contacts Required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()/*true*/) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EnrollActivity.this, "Read barcodes fail", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                System.arraycopy(buffer, 0, databuf,datasize,size);
                datasize=datasize+size;
                if(TimerBarcode==null){
                    TimerBarcodeStart();
                }
            }
        });
    }

    public void TimerBarcodeStart() {
        TimerBarcode = new Timer();
        HandlerBarcode = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TimerBarcodeStop();
                if(datasize>0){
                    byte tp[]=new byte[datasize];
                    System.arraycopy(databuf, 0, tp,0,datasize);
                    mindex_fp.setText(new String(tp));
                    soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
                    datasize=0;
                }
                super.handleMessage(msg);
            }
        };
        TaskBarcode = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                HandlerBarcode.sendMessage(message);
            }
        };
        TimerBarcode.schedule(TaskBarcode, 1000, 1000);
    }

    public void TimerBarcodeStop() {
        if (TimerBarcode!=null) {
            TimerBarcode.cancel();
            TimerBarcode = null;
            TaskBarcode.cancel();
            TaskBarcode=null;
        }
    }

    //NFC

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent){
        byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String cardstr=/*Integer.toString(count)+":"+*/
                Integer.toHexString(sn[0]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[1]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[2]&0xFF).toUpperCase()+
                        Integer.toHexString(sn[3]&0xFF).toUpperCase();
        emp_nfc.setText(cardstr);
        CardSN=cardstr;
        //soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
    }

}
