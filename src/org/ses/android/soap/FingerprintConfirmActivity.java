package org.ses.android.soap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
// TODO import org.ses.android.soap.tasks.PutFingerprintTask;
import org.ses.android.soap.tasks.RegistrarParticipanteTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.PreferencesManager;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGANSITemplateInfo;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGISOTemplateInfo;
import SecuGen.FDxSDKPro.SGImpressionType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.concurrent.ExecutionException;

/**
 * Created by anyway on 1/11/16.
 */
public class FingerprintConfirmActivity extends BaseActivity {

    private static final String TAG = "SecuGen USB";

    private Participant currParticipant;

    private TextView headerText;
    private ImageView imgFingerprint;
    private int imgWidth;
    private int imgHeight;
    private int[] mMaxTemplateSize;
    private int[] grayBuffer;
    private Bitmap grayBitmap;
    private IntentFilter filter;
    private PendingIntent mPermissionIntent;

    private JSGFPLib jsgfpLib;
    private byte[] mTemplate;
    private Button btnScan;
    private Button btnConfirm;

    private byte[] storedTemplate;

    private SharedPreferences mPreferences;

    RegistrarParticipanteTask registerPatientDetails;
    private AsyncTask<String, String, String> registrarParticipante;

    // TODO PutFingerprintTask registerPatientFingerprint;
    private AsyncTask<String, String, String> putFingerprint;

    String url;

    //RILEY
    //This broadcast receiver is necessary to get user permissions to access the attached USB device
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                        } else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null");
                    } else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device " + device);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_confirm_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imageViewScan1);
        headerText = (TextView) findViewById(R.id.textScan1);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnConfirm = (Button) findViewById(R.id.btnConfirm1);

        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i = 0; i < grayBuffer.length; ++i)
            grayBuffer[i] = android.graphics.Color.GRAY;
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);
        imgFingerprint.setImageBitmap(grayBitmap);
        mMaxTemplateSize = new int[1];

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        jsgfpLib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));
        setListeners();

        //retrieve presently stored fingerprint
        storedTemplate = PreferencesManager.getFingerprint(getBaseContext());
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        jsgfpLib.CloseDevice();
        unregisterReceiver(mUsbReceiver);
        mTemplate = null;
        imgFingerprint.setImageBitmap(grayBitmap);
        super.onPause();
    }
    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        registerReceiver(mUsbReceiver, filter);
        long error = jsgfpLib.Init(SGFDxDeviceName.SG_DEV_AUTO);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                dlgAlert.setMessage(R.string.fingerprint_scanner_not_found);
            else
                dlgAlert.setMessage(R.string.fingerprint_init_failed);
            dlgAlert.setTitle("SecuGen Fingerprint SDK");
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //finish();
                            return;
                        }
                    }
            );
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        } else {
            UsbDevice usbDevice = jsgfpLib.GetUsbDevice();
            if (usbDevice == null) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("SDU04P or SDU03P fingerprint sensor not found!");
                dlgAlert.setTitle("SecuGen Fingerprint SDK");
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                return;
                            }
                        }
                );
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
            } else {
                jsgfpLib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                error = jsgfpLib.OpenDevice(0);
                // debugMessage("OpenDevice() ret: " + error + "\n");
                SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                error = jsgfpLib.GetDeviceInfo(deviceInfo);
                // debugMessage("GetDeviceInfo() ret: " + error + "\n");
                imgWidth = deviceInfo.imageWidth;
                imgHeight = deviceInfo.imageHeight;
                jsgfpLib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                jsgfpLib.GetMaxTemplateSize(mMaxTemplateSize);
                mTemplate = new byte[mMaxTemplateSize[0]];
                // debugMessage("TEMPLATE_FORMAT_SG400 SIZE: " + mMaxTemplateSize[0] + "\n");
                //Thread thread = new Thread(this);
                //thread.start();
            }
        }
    }
    public Bitmap toGrayscale(byte[] mImageBuffer) {
        byte[] Bits = new byte[mImageBuffer.length * 4];
        for (int i = 0; i < mImageBuffer.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = mImageBuffer[i]; // Invert the source bits
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
        }

        Bitmap bmpGrayscale = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bmpGrayscale;
    }

    public void ScanFingerPrint() {
        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
        byte[] buffer = new byte[imgWidth * imgHeight];
        dwTimeStart = System.currentTimeMillis();
        if (jsgfpLib.GetImage(buffer) == SGFDxErrorCode.SGFDX_ERROR_NONE) {
            jsgfpLib.GetImage(buffer);
            Log.d("Success!", "");
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));

            SGFingerInfo fpInfo = new SGFingerInfo();
            long result = jsgfpLib.CreateTemplate(fpInfo, buffer, mTemplate);

        }
    }

    public void setListeners() {
        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ScanFingerPrint();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Compare just-scanned fingerprint to stored fingerprint
                if (mTemplate == null) {
                    askForRescan();
                }

                long sl = SGFDxSecurityLevel.SL_NORMAL; // Set security level as NORMAL
                boolean[] matched = new boolean[1];
                if (jsgfpLib.MatchTemplate(mTemplate, storedTemplate, sl, matched)
                        == SGFDxErrorCode.SGFDX_ERROR_NONE && matched[0]) {

                    // Unpack patient info
                    Bundle patientInfo = getIntent().getExtras();
                    if (patientInfo != null) { // it shouldn't be null
                        currParticipant = patientInfo.getParcelable("patient");
                    }

                    // Save everything
                    url = StringConexion.conexion;
                    try {


                        registerPatientDetails = new RegistrarParticipanteTask();
                        // TODO registerPatientFingerprint = new PutFingerprintTask();

                        registrarParticipante = registerPatientDetails.execute(
                                (String) currParticipant.getProperty(5),
                                (String) currParticipant.getProperty(4),
                                (String) currParticipant.getProperty(1),
                                (String) currParticipant.getProperty(2),
                                (String) currParticipant.getProperty(3),
                                (String) currParticipant.getProperty(6),
                                (String) currParticipant.getProperty(7), url);

                        putFingerprint = null; //TODO

                        Toast.makeText(getBaseContext(), "Datos guardados!!",Toast.LENGTH_SHORT).show();

                        // Continue to patient dashboard activity
                        Intent intent = new Intent(FingerprintConfirmActivity.this, ParticipantDashboardActivity.class);
                        intent.putExtra("patient", currParticipant);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        askForRescan();
                    }
                } else {
                    askForRescan();
                }
            }
        });
    }

    public void askForRescan() {
        headerText.setText(this.getString(R.string.scan_again));
        imgFingerprint.setImageBitmap(grayBitmap);
        mTemplate = null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        jsgfpLib.CloseDevice();
        mTemplate = null;
        jsgfpLib.Close();
        super.onDestroy();
    }
}
