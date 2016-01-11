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
import SecuGen.FDxSDKPro.SGDeviceInfoParam;
import SecuGen.FDxSDKPro.SGFDxErrorCode;

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

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        jsgfpLib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));


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
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            //debugMessage("getImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n");
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));
        }
    }

    public void onClick(View v) {
        if (v == btnScan) {
            ScanFingerPrint();
        }
        if (v == btnSearch) {

        }
    }

}

