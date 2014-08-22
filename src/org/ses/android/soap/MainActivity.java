package org.ses.android.soap;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp.R;
import org.ses.android.soap.database.Local;
import org.ses.android.soap.database.Login;
import org.ses.android.soap.preferences.AdminPreferencesActivity;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.AsyncTaskRunner;
import org.ses.android.soap.tasks.LocalLoadTask;
import org.ses.android.soap.utilities.AppStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private Button btnIngresar;
	private EditText txtUsuario;
	private EditText txtPassword;
	private TextView tvwMensaje;
	private Spinner spnLocal;

	private AsyncTask<String, String, Login> asyncTask;
	private AsyncTask<String, String, Local[]> loadLocal;
//	private AsyncTask<String, String, String> formListTask;
	
	private String response;
	private static Context context;
	private String selLocal,mAlertMsg;
	private Boolean connected;

//   public static final String MisPREFERENCIAS = "mis_pref" ;
   public static final String usuario = "usuarioKey"; 
   public static final String pass = "passKey";
   public static final String url = "urlKey";
   private static final int PROGRESS_DIALOG = 1;
   SharedPreferences mPreferences;
   public ProgressDialog mProgressDialog;
   private AsyncTaskRunner mAsyncTaskRunner;
  /** Called when the activity is first created. */
	 @SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    MainActivity.context = getApplicationContext();
	    setContentView(R.layout.activity_main);
		txtUsuario = (EditText)findViewById(R.id.txtUsuario);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		btnIngresar = (Button)findViewById(R.id.btnIngresar);
		tvwMensaje = (TextView) findViewById(R.id.tvwMensaje);
		spnLocal = (Spinner) findViewById(R.id.spnLocal);
		mAlertMsg = getString(R.string.please_wait);		
		if (AppStatus.getInstance(this).isOnline(this)) {

//		    Toast.makeText(this,"You are online!!!!",8000).show();
		    tvwMensaje.setText(R.string.online);
		    connected = true;
		} else {

//		    Toast.makeText(this,"You are not online!!!!",8000).show();
		    Log.i("Home", "############################You are not online!!!!");
		    tvwMensaje.setText(R.string.no_connection);
		    connected = false;
		}
		
//		loadLocalSpinner();
        if (getLastNonConfigurationInstance() instanceof AsyncTaskRunner) {
            mAsyncTaskRunner = (AsyncTaskRunner) getLastNonConfigurationInstance();
            if (mAsyncTaskRunner.getStatus() == AsyncTask.Status.FINISHED) {
                try {
                    dismissDialog(PROGRESS_DIALOG);
                } catch (IllegalArgumentException e) {
                    Log.i("Login", "Attempting to close a dialog that was not previously opened");
                }
                mAsyncTaskRunner = null;
            }
        } 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.ArrayOfLocal, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnLocal.setAdapter(adapter);
		spnLocal.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
				android.view.View v, int position, long id) {
					selLocal = Integer.toString(position);
//					if (selLocal.equals("0")) loadLocalSpinner();
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					tvwMensaje.setText(getString(R.string.select_local));
				}
		});
		
		btnIngresar.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i("login", "OnClick_connected:" + connected);
			if (connected){
				AsyncTaskRunner runner=new AsyncTaskRunner();
				String userName=txtUsuario.getText().toString();
				String password=txtPassword.getText().toString();
                // Get the server from the settings
                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                        getString(R.string.default_server_url));
			    Editor editor = mPreferences.edit();
				Log.i("login", "OnClick_url:" + url);


				try {
                    asyncTask = runner.execute(userName,password,selLocal,url);

					Login login = asyncTask.get();
                    response = login.Mensaje;
                    if (!asyncTask.isCancelled()) showDialog(PROGRESS_DIALOG);
                    Log.i("login",login.Mensaje);

				    editor.putString(PreferencesActivity.KEY_USERNAME, userName);
				    editor.putString(PreferencesActivity.KEY_PASSWORD, password);
				    editor.putString(PreferencesActivity.KEY_USERID, String.valueOf(login.CodigoUsuario));
				    editor.putString(PreferencesActivity.KEY_LOCAL_ID, selLocal);
//				    editor.commit();

					//response = login.Mensaje;
					Log.i("login", "OnClick_response:" + response);
		//				 //Sale despues
		//					Intent i=new Intent(MainActivity.this,Menu_principal.class); 
		//					startActivity(i);
        //                  finish();
		//				 //Sale despues

					
				 	if(response.equals(getString(R.string.session_init_key)) || response.equals(getString(R.string.password_expired_key))){    

						editor.commit();
						// Remote Server	
				 		Intent intent=new Intent(MainActivity.this,Menu_principal.class); 
						startActivity(intent); 
					
						finish();

				 	}else{
						Log.i("login", "Datos incorrectos" );
						dismissDialog(PROGRESS_DIALOG);
						Toast.makeText(getBaseContext(), response,Toast.LENGTH_SHORT).show();	
					}
				} catch (InterruptedException e1) {
					response = e1.getMessage();
				} catch (ExecutionException e1) {
					response = e1.getMessage();
				} catch (Exception e1) {
					response = e1.getMessage();
				}
				tvwMensaje.setText(response); 
				
			}
	   }
		
		  });
		    
		}
	 
		public static Context getAppContext() {
		    return MainActivity.context;
		}
		   @Override
	    protected void onResume() {
	       mPreferences=getSharedPreferences(AdminPreferencesActivity.ADMIN_PREFERENCES,Context.MODE_PRIVATE);
	       if (mPreferences.contains(usuario))
	       {
		       if(mPreferences.contains(pass)){
					Intent intent=new Intent(MainActivity.this,Menu_principal.class); 
					startActivity(intent); 
		       }
	       }
	       super.onResume();
	   }
	   public void loadLocalSpinner(){
			mPreferences = getSharedPreferences(
			           AdminPreferencesActivity.ADMIN_PREFERENCES, Context.MODE_PRIVATE);	 
			LocalLoadTask tareaLocal = new LocalLoadTask();
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                    getString(R.string.default_server_url));
			loadLocal = tareaLocal.execute(url);
			Local[] objLocal;
			String[] wee;
			try {

				objLocal = loadLocal.get();
				wee = new String[objLocal.length];
				
			     for(int i = 0;i < objLocal.length; i++){
			         wee[i]= objLocal[i].nombre;
			     }
			     ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
			             this, android.R.layout.simple_spinner_item, wee);
			    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
			    spnLocal.setAdapter(spinnerArrayAdapter);	
			     
				Log.i("Visita","Local Array");
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}	
	   protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case PROGRESS_DIALOG:
	            mProgressDialog = new ProgressDialog(this);
        		mProgressDialog = new ProgressDialog(this);
	            mProgressDialog.setTitle(getString(R.string.downloading_data));
	            mProgressDialog.setMessage(mAlertMsg);
	            mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
	            mProgressDialog.setIndeterminate(true);
	            mProgressDialog.setCancelable(false);
	            return mProgressDialog;	        
	        }
	        return null;
       }
}