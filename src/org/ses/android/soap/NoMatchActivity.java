package org.ses.android.soap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by svijayakumar2 on 1/12/16.
 */
public class NoMatchActivity extends BaseActivity {

    private String dni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_match_layout);

        try{
            dni = getIntent().getStringExtra("dni");
        }
        catch(Exception e)
        {
            dni = null;
        }


        // "Register Patient" button should open RegisterParticipantActivity
<<<<<<< HEAD
        // Should carry fingerprint data on
        Button buttonRegisterNewParticipant = (Button) findViewById(R.id.btnRegisterNewUser);
=======
        Button buttonRegisterNewParticipant = (Button) findViewById(R.id.btnRegisterNewParticipant);
>>>>>>> 837a859fe9fc95e4514ec0ef38e98331d57ac4f5
        buttonRegisterNewParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoMatchActivity.this, RegisterParticipantActivity.class);
                if (dni != null)
                    intent.putExtra("dni", dni);
                startActivity(intent);
            }
        });

        // "Add Fingerprint to Existing Patient" button should open AddFingerprintExistingActivity
        // Should carry fingerprint data on
        Button buttonAddFingerprintExisting = (Button) findViewById(R.id.btnAddFingerprintExisting);
        buttonAddFingerprintExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoMatchActivity.this, AddFingerprintExistingActivity.class);
                startActivity(intent);
            }
        });
        // "Try Again" button should return to FingerprintFindActivity (Fingerprint search layout)
        Button buttonTryAgain = (Button) findViewById(R.id.btnTryAgain);
        buttonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoMatchActivity.this, FingerprintFindActivity.class);
                startActivity(intent);
            }
        });
    }
}
