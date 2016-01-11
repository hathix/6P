package org.ses.android.soap;

import android.app.Activity;
import android.os.Bundle;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_home:
                // open up MainMenuActivity (there's probably some code that already does this)
//                Log.v("menu", "home");
                break;
            case R.id.action_logout:
                // TODO get some common code that logs user out (abstract away from MainMenuActivity)
//                Log.v("menu", "logout");
                break;
            case R.id.action_settings:
                // TODO make the settings activity lol
//                Log.v("menu", "settings");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

}
