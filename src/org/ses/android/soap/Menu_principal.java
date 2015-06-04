package org.ses.android.soap;


import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp.R;
import org.ses.android.soap.preferences.AdminPreferencesActivity;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.FormList1Task;
import org.ses.android.soap.tasks.FormListTask;
import org.ses.android.soap.utilities.AppStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;

public class Menu_principal extends Activity {
	private Button btnRegistrarParticipante;
	private Button btnGenerarVisita;
	private Button btnCerrarSesion;
	private Button btnRunODK;
	private AsyncTask<String, String, String> formListTask;
    private AsyncTask<String, String, String> formList1Task;
	SharedPreferences mPreferences;
//	private static final int PASSWORD_DIALOG = 1;
    // menu options
    private static final int MENU_PREFERENCES = Menu.FIRST;
//    private static final int MENU_ADMIN = Menu.FIRST + 1;
    @SuppressWarnings("unused")
	private SharedPreferences mAdminPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);
        // dynamically construct the "ODK Collect vA.B" string
        TextView mainMenuMessageLabel = (TextView) findViewById(R.id.main_menu_header);
        mainMenuMessageLabel.setText(getVersionedAppName());

        if (AppStatus.getInstance(this).isOnline(this)) {
		    Toast.makeText(this,R.string.online,Toast.LENGTH_SHORT).show();

		} else {

		    Toast.makeText(this,R.string.no_connection,Toast.LENGTH_SHORT).show();
		}
		btnRegistrarParticipante = (Button)findViewById(R.id.btnRegistrarParticipante);
		btnGenerarVisita = (Button)findViewById(R.id.btnGenerarVisita);
		btnCerrarSesion = (Button)findViewById(R.id.btnCerrarSesion);
		btnRunODK = (Button)findViewById(R.id.btnRunODK);
        mAdminPreferences = this.getSharedPreferences(AdminPreferencesActivity.ADMIN_PREFERENCES, 0);
        
        btnRegistrarParticipante.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {		
				Intent intent=new Intent(Menu_principal.this,ParticipanteDNIActivity.class); 
                startActivity(intent);
			}
		});
        
        btnGenerarVisita.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {		
//				Toast.makeText(getBaseContext(), "Opcion deshabilitada!!",Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(Menu_principal.this,ParticipanteBusquedaActivity.class); 
                startActivity(intent);
			}
		});
        
        btnRunODK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {		
//				Toast.makeText(getBaseContext(), "Opcion deshabilitada!!",Toast.LENGTH_SHORT).show();
//				// Remote Server
//                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                String filterForms = mPreferences.getString(PreferencesActivity.KEY_FILTERFORMS, "");
//                String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
//                        getString(R.string.default_server_url));		        
//                String codigo = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
//		        Editor editor = mPreferences.edit();
//		        
//				FormListTask formList=new FormListTask();
//				formListTask=formList.execute(codigo,url);
////				String filterForms;
//				try {
//					String filterForms = formList.get();
//					Log.i("menu", ".filterForms:"+filterForms );
//					editor.putString(PreferencesActivity.KEY_FILTERFORMS, filterForms);
//					editor.commit();				
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (ExecutionException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//
//				// Remote Server
                // JT:04/06/2015
                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String filterForms = "";
                String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                        getString(R.string.default_server_url));
                String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                String codigolocal = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
                String codigoproyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
                Log.i("menu", ".codigousuario:"+codigousuario );
                Log.i("menu", ".codigolocal:"+codigolocal );
                Log.i("menu", ".codigoproyecto:"+codigoproyecto );
		        //Editor editor = mPreferences.edit();

				FormList1Task formList1=new FormList1Task();
				formList1Task=formList1.execute(codigousuario,codigolocal,codigoproyecto,url);

				try {
					filterForms = formList1.get();
					Log.i("menu", ".filterForms:"+filterForms );
					//editor.putString(PreferencesActivity.KEY_FILTERFORMS, filterForms);
					//editor.commit();
                    String Codigo = "002009-1234-2";
                    Intent intents = new Intent(Intent.ACTION_MAIN);
                    intents.setComponent(new ComponentName("org.odk.collect.android", "org.odk.collect.android.activities.MainMenuActivity"));
                    intents.putExtra("idParticipante", Codigo);
                    intents.putExtra("idProyecto", filterForms);
                    startActivity(intents);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

                // JT:04/06/2015
                // JT:03/06/2015

                // Codigo = "";
                // Proyecto = "";

//                String Codigo = "002009-1234-2";
//                String Proyecto = "DETE_A0_V1/DETE_C2_V3/DETE_C1_V5/DETE_A3_V2";
//
//                Intent intents = new Intent(Intent.ACTION_MAIN);
//                intents.setComponent(new ComponentName("org.odk.collect.android", "org.odk.collect.android.activities.MainMenuActivity"));
//                intents.putExtra("idParticipante", Codigo);
//                intents.putExtra("idProyecto", Proyecto);
//                startActivity(intents);

                 //JT:03/06/2015

//				Intent i;
//				PackageManager manager = getPackageManager();
//				try {
//				    i = manager.getLaunchIntentForPackage("org.odk.collect.android");
//				    if (i == null)
//				        throw new PackageManager.NameNotFoundException();
//				    i.addCategory(Intent.CATEGORY_LAUNCHER);
//				    startActivity(i);
//				} catch (PackageManager.NameNotFoundException e) {
//
//				}
			}
		});        
        btnCerrarSesion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

	        	AlertDialog.Builder builder = new AlertDialog.Builder(Menu_principal.this);
	        	builder.setMessage(getString(R.string.exit_yes_no))
	        	        .setTitle(getString(R.string.warning))
	        	        .setCancelable(false)
	        	        .setPositiveButton(getString(R.string.answer_yes),
	        	                new DialogInterface.OnClickListener() {
	        	                    @Override
									public void onClick(DialogInterface dialog, int id) {
	        	                    	SharedPreferences sharedpreferences = getSharedPreferences
	        	                      	      (AdminPreferencesActivity.ADMIN_PREFERENCES, Context.MODE_PRIVATE);
	        	                    	Editor editor = sharedpreferences.edit();
	        	                    	editor.clear();
	        	                    	editor.commit();
//	        	                    	moveTaskToBack(true); 
//	        	                    	Menu_principal.this.finish();
	        	                        // metodo que se debe implementar
	        	                    	//envia al otro activity login
	        	                    	Intent intent = new Intent(Menu_principal.this, MainActivity.class);
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
		});
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, 0, getString(R.string.general_preferences)).setIcon(
                android.R.drawable.ic_menu_preferences);
//        menu.add(0, MENU_ADMIN, 0, getString(R.string.admin_preferences)).setIcon(
//                R.drawable.ic_menu_login);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PREFERENCES:

                Intent ig = new Intent(this, PreferencesActivity.class);
                startActivity(ig);
                return true;
//            case MENU_ADMIN:
//
////                String pw = mAdminPreferences.getString(AdminPreferencesActivity.KEY_ADMIN_PW, "");
////                if ("".equalsIgnoreCase(pw)) {
//                    Intent i = new Intent(this, AdminPreferencesActivity.class);
//                    startActivity(i);
////                } else {
////                    showDialog(PASSWORD_DIALOG);
////
////                }
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getVersionedAppName() {
        String versionDetail = "";
        try {
            PackageInfo pinfo;
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            versionDetail = " " + versionName + " (" + versionNumber + ")";
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getString(R.string.app_name) + versionDetail;
    }
}