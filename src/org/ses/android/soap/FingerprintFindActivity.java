package org.ses.android.soap;

import android.app.Activity;
import android.view.View;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.PatId;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.IdsListTask;
import org.ses.android.soap.tasks.ParticipantLoadTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.widgets.GrupoBotones;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

/**
 * Created by fanneyzhu on 1/11/16.
 */
public class FingerprintFindActivity extends Activity
        implements View.OnClickListener {

    private static final String TAG = "SecuGen USB";

    private ImageView imgFingerprint;
    private byte[] rawFingerprint;
    private int imgWidth;
    private int imgHeight;
    private int[] mMaxTemplateSize;
    private int[] grayBuffer;
    private Bitmap grayBitmap;
    private IntentFilter filter;
    private PendingIntent mPermissionIntent;

    private JSGFPLib jsgfpLib;

    private Button btnScan;
    private EditText edt_first_name;
    private EditText edt_maternal_name;
    private EditText edt_paternal_name;
    private EditText edt_dni_document;
    private TextView edt_dob;
    private Button btnSearch;

    private AsyncTask<String, String, Participant> asyncTask;
    private SharedPreferences mPreferences;

    private Participant participant;
    String dni, names, paternalLast, maternalLast;

    //RILEY
    //This broadcast receiver is necessary to get user permissions to access the attached USB device
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //DEBUG Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
                            //debugMessage("Vendor ID : " + device.getVendorId() + "\n");
                            //debugMessage("Product ID: " + device.getProductId() + "\n");
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
        setContentView(R.layout.fingerprint_search_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imgFingerprint);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        edt_first_name = (EditText) findViewById(R.id.edt_first_name);
        edt_maternal_name = (EditText) findViewById(R.id.edt_maternal_name);
        edt_paternal_name = (EditText) findViewById(R.id.edt_paternal_name);
        edt_dob = (TextView) findViewById(R.id.tvwfecha_nacimiento);
        edt_dni_document = (EditText) findViewById(R.id.edt_dni_document);
        btnSearch = (Button) findViewById(R.id.btnSearch);
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
                dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
            else
                dlgAlert.setMessage("Fingerprint device initialization failed!");
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
        // if (jsgfpLib.GetImage(buffer) == SGFDxErrorCode.SGFDX_ERROR_NONE) {
            jsgfpLib.GetImage(buffer);
            Log.i("Success!", "");
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            //debugMessage("getImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));
        // }
    }

    public void onClick(View v) {
        if (v == btnScan) {
            ScanFingerPrint();
        }
        if (v == btnSearch) {
            dni = edt_dni_document.getText().toString();
            names = edt_first_name.getText().toString();
            maternalLast = edt_maternal_name.getText().toString();
            paternalLast = edt_paternal_name.getText().toString();

            // read in other stuff as well

            mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID,"");
            Log.i("codigousuario",codigousuario);

            // Don't really need this, but whatever: tasks have not been properly updated
            String url = StringConexion.conexion;

            // valid-length DNI has been entered, search just off that
            if (dni != null && dni.length() == 8)
            {
                ParticipantLoadTask tarea = new ParticipantLoadTask();
                asyncTask=tarea.execute(dni,url);

                try{
                    participant = asyncTask.get();
                    Log.i("DNI:", dni);

                    if (participant == null){
                        Intent intent = new Intent(getBaseContext(), NoMatchActivity.class);
                        startActivity(intent);
                    }else{
                        Log.i("CodigoPaciente:",participant.CodigoPaciente );
                        Editor editor = mPreferences.edit();
                        editor.putString("CodigoPaciente",participant.CodigoPaciente);
                        editor.putString("patient_name",participant.Nombres);
                        editor.commit();

                        Intent intent = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
                        startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            // We have full name + DOB information
            if (names != null && maternalLast != null && paternalLast != null &&
                    names.length() > 0 && maternalLast.length() > 0 && paternalLast.length() > 0)
            {
                // search based off name
            }


        }
    }

}

