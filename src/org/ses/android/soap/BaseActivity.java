package org.ses.android.soap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import org.ses.android.seispapp120.R;

/**
 * Created by neel on 1/11/16.
 *
 * An activity that auto-includes an options menu (aka action bar.)
 * Have all your activities inherit from this, not from Activity.
 */
public class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_menu,menu);
        return true;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

}
