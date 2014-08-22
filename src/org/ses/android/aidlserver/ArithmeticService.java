package org.ses.android.aidlserver;

import org.ses.android.soap.preferences.AdminPreferencesActivity;
import org.ses.android.soap.preferences.PreferencesActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
 
public class ArithmeticService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }
    /**
     * IRemote defnition is available here
     */
    private final IRemote.Stub mBinder = new IRemote.Stub() {
    	SharedPreferences mPreferences ;
        @Override
        public int add(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return (a + b);
        }
        @Override
        public String getFilterForms() throws RemoteException {
            // TODO Auto-generated method stub
			mPreferences = getSharedPreferences(
			           AdminPreferencesActivity.ADMIN_PREFERENCES, Context.MODE_PRIVATE);	
			String filterForms = mPreferences.getString(PreferencesActivity.KEY_FILTERFORMS, "");
			Log.i("filterForms:",filterForms);
//            return ("DOTS_A1_V1/DOTS_B1_V1");
            return filterForms;
        }
    };
}
