package org.ses.android.soap;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.tasks.StringConexion;

import java.nio.ByteBuffer;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;

/**
 * Created by anyway on 1/11/16.
 */
public class FingerprintBaseActivity extends BaseActivity {

    private static final String TAG = "SecuGen USB";

    private int imgWidth;
    private int imgHeight;
    private int[] mMaxTemplateSize;
    private int[] grayBuffer;
    protected Bitmap grayBitmap;
    private IntentFilter filter;
    private PendingIntent mPermissionIntent;

    protected JSGFPLib jsgfpLib;
    protected byte[] mTemplate;

    String url = StringConexion.conexion;

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

    protected void setupScanner() {

        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i = 0; i < grayBuffer.length; ++i)
            grayBuffer[i] = android.graphics.Color.GRAY;
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);
        mMaxTemplateSize = new int[1];

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        jsgfpLib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        jsgfpLib.CloseDevice();
        unregisterReceiver(mUsbReceiver);
        mTemplate = null;
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
                SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                error = jsgfpLib.GetDeviceInfo(deviceInfo);
                imgWidth = deviceInfo.imageWidth;
                imgHeight = deviceInfo.imageHeight;
                jsgfpLib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                jsgfpLib.GetMaxTemplateSize(mMaxTemplateSize);
                mTemplate = new byte[mMaxTemplateSize[0]];
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

    public byte[] ScanFingerPrintBase() {
        byte[] buffer = new byte[imgWidth * imgHeight];
        if (jsgfpLib.GetImage(buffer) == SGFDxErrorCode.SGFDX_ERROR_NONE) {
            jsgfpLib.GetImage(buffer);
            Log.d("Success!", "");

            SGFingerInfo fpInfo = new SGFingerInfo();
            long result = jsgfpLib.CreateTemplate(fpInfo, buffer, mTemplate);
            return buffer;
        } else {
            return null;
        }
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
