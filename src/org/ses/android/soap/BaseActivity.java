package org.ses.android.soap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.ses.android.seispapp120.R;

/**
 * Created by neel on 1/11/16.
 *
 * An activity that auto-includes an options menu (aka action bar) that includes global
 * navigation buttons.
 * Have all your activities that want an options menu inherit from this, not from Activity.
 */
public class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_menu,menu);
        return true;
    }

    /*
     * pops up a menu asking user to confirm logout, and if yes, wipes all session memory
     */
    public void logOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage(getString(R.string.exit_yes_no))
                .setTitle(getString(R.string.warning))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.answer_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                editor.commit();
                                Intent intent = new Intent(BaseActivity.this, PromoterLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton(getString(R.string.answer_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_home:
                Intent intent = new Intent(getBaseContext(), MainMenuActivity.class);
                startActivity(intent);
                Log.v("menu", "homepage");
                break;
            case R.id.action_logout:
                logOut();
                Log.v("menu", "logout");
                break;
            case R.id.action_settings:
                // TODO make the settings activity lol
//                Log.v("menu", "settings");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
