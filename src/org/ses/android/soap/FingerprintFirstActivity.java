package org.ses.android.soap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.BuscarHuellaTask;
import org.ses.android.soap.utils.PreferencesManager;

import java.util.concurrent.ExecutionException;

/**
 * Created by fanneyzhu on 1/21/16.
 */
public class FingerprintFirstActivity extends FingerprintBaseActivity {

    private Participant participant;

    private TextView headerText;
    private ImageView imgFingerprint;
    private Button btnScan;
    private Button btnConfirm;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_confirm_layout);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        imgFingerprint = (ImageView) findViewById(R.id.imageViewScan1);
        headerText = (TextView) findViewById(R.id.textScan1);
        btnScan = (Button) findViewById(R.id.btnScan1);
        btnConfirm = (Button) findViewById(R.id.btnConfirm1);

        // change text
        headerText.setText(R.string.scan_first_time);

        setupScanner();
        imgFingerprint.setImageBitmap(grayBitmap);

        setListeners();

        // load up the participant
        participant = (Participant) getIntent().getParcelableExtra("Participant");

    }

    @Override
    public void onPause() {
        imgFingerprint.setImageBitmap(grayBitmap);
        super.onPause();
    }

    public void ScanFingerPrint() {
        byte[] buffer = ScanFingerPrintBase();
        if (buffer != null) {
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));
            PreferencesManager.setFingerprint(getBaseContext(), mTemplate);
        }
    }

    public void setListeners() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanFingerPrint();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Compare just-scanned fingerprint to stored fingerprint
                if (mTemplate == null) {
                    askForRescan();
                }

                else {
                    // check if fingerprint already belongs to another patient
                    BuscarHuellaTask tarea = new BuscarHuellaTask();
                    AsyncTask<String, String, String> asyncTaskString = tarea.execute(mPreferences.getString("Fingerprint",""));
                    String result;
                    try {
                        result = asyncTaskString.get();

                        if (result.equals("fingerprintNotFound") || result.equals("someMatchDidntWork")) {
                            Intent intent = new Intent(FingerprintFirstActivity.this, FingerprintConfirmActivity.class);
                            intent.putExtra("Participant", participant);
                            intent.putExtra("codigoPaciente", participant.CodigoPaciente);
                            startActivity(intent);
                            startActivity(intent);
                        } else {
                            Toast.makeText(FingerprintFirstActivity.this,
                                    getString(R.string.fingerprint_dni_not_matched),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void askForRescan() {
        headerText.setText(this.getString(R.string.scan_again));
        imgFingerprint.setImageBitmap(grayBitmap);
    }

}
