package org.ses.android.soap;

import android.os.Bundle;

import org.ses.android.seispapp120.R;

/**
 * Created by fanneyzhu on 1/11/16.
 *
 * NOTE(neel): this is all temporary, feel free to overwrite when there's a merge conflict!
 * Just make sure this still extends BaseActivity.
 */
public class FingerprintFindActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_search_layout);
    }
}
