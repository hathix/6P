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
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;

import java.util.concurrent.ExecutionException;

public class ParticipanteAsignarIdActivity extends Activity {
	//private static final int ACTIVITY_VISITLIST=1;
	//private static final int VISITLIST_ID = Menu.FIRST;

	private Button btn_accept1;
    private Button btn_cancel1;
    private TextView txt_id_type;
    private TextView txt_participant_name;
    private EditText edt_participant_name;
    private TextView txt_participant_id;
    private EditText edt_participant_id;
    private TextView txt_assign_msg;
    private AsyncTask<String, String, String> generarIdTAM;
    private AsyncTask<String, String, String> generarIdENR;
    private AsyncTask<String,String,Idreg[]> mostrarTipoID;
    MostrarTipoIDTask mostrarID;
    AsyncTask<String, String, String> genIdTAM;
    AsyncTask<String, String, String> genIdENR;
    String selLocal;
    String selProyecto;
    String codigopaciente;
    String selGrupo;
    String selVisita;
    String codigousuario;
    String url;
    Idreg[] tipoID;
    String idAsigTAM = "";
    String idAsigENR = "";
    String estadoTAM ="";
    String estadoENR ="";
    int  get_ENR;
    boolean  validar_emr = false;
    String mTipoAsig="";;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participante_asignar_id);
        DataLoad();
        ValidarActBotones();



        btn_accept1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            /*String mIdAsig = edt_participant_id.getText().toString();
            Log.i("btn_accept1","mIdAsig:" + mIdAsig);

           String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);

            txt_assign_msg.setText(resul);
            //btn_accept1.setVisibility(View.INVISIBLE);
            btn_accept1.setEnabled(false);*/
         //   Log.i("btn_accept1","resul:" + resul);
         //   Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();
                if (validar_emr == true && get_ENR == 0){

                    String mIdAsig = edt_participant_id.getText().toString();
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);

                    txt_assign_msg.setText(resul);
                    //btn_accept1.setVisibility(View.INVISIBLE);
                    btn_accept1.setEnabled(false);
                    Log.i("btn_accept1","resul:" + resul);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();

                    Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("selLocal", selLocal);
                    extras.putString("selProyecto", selProyecto);
                    extras.putString("codigopaciente",codigopaciente);
                    extras.putString("selGrupo", selGrupo);
                    extras.putString("selVisita",selVisita);
                    extras.putString("codigousuario",codigousuario);
                    extras.putString("url",url);
                    extras.putString("estadoTAM",estadoTAM);
                    extras.putString("estadoENR",estadoENR);
                    extras.putInt("validar_emr", 1);

                    pass.putExtras(extras);
                    startActivity(pass);
                    finish();
                }
                else if (get_ENR > 0 &&  get_ENR<10){


                    String mIdAsig = edt_participant_id.getText().toString();
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);

                    txt_assign_msg.setText(resul);
                    //btn_accept1.setVisibility(View.INVISIBLE);
                    btn_accept1.setEnabled(false);
                    Log.i("btn_accept1", "resul:" + resul);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();

                    btn_accept1.setEnabled(false);


                }

                else if (get_ENR == 20){



                }

                else  if (get_ENR==22) {
                    get_ENR = 0;
                    String mIdAsig = edt_participant_id.getText().toString();
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();

                    btn_accept1.setEnabled(false);


                }

                else  if (get_ENR==23) {

                    String mIdAsig = edt_participant_id.getText().toString();
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();

                    btn_accept1.setEnabled(false);


                }
            }
        });
        btn_cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

    private void DataLoad(){
        Bundle data = getIntent().getExtras();

        selLocal = data.getString("selLocal");
        selProyecto = data.getString("selProyecto");
        codigopaciente = data.getString("codigopaciente");
        selGrupo = data.getString("selGrupo");
        selVisita = data.getString("selVisita");
        codigousuario = data.getString("codigousuario");
        url = data.getString("url");
        estadoTAM =data.getString("estadoTAM");
        estadoENR =data.getString("estadoENR");
        get_ENR = data.getInt("validar_emr");


        MostrarTipoIDTask mostrarID =  new MostrarTipoIDTask();
        mostrarTipoID = mostrarID.execute(selLocal,selProyecto,codigopaciente,url);
        String mTipoVisita="";
        mTipoAsig="";
        edt_participant_name = (EditText)findViewById(R.id.edt_participant_name);
        edt_participant_id= (EditText)findViewById(R.id.edt_participant_id);
        btn_accept1 = (Button)findViewById(R.id.btn_accept1);
        btn_cancel1 = (Button)findViewById(R.id.btn_cancel1);
        txt_id_type = (TextView)findViewById(R.id.txt_id_type);
        txt_assign_msg = (TextView)findViewById(R.id.txt_assign_msg);
        edt_participant_name.setFocusable(false);
        try {
            tipoID = mostrarTipoID.get();
            Log.i("AsigID","tipoID[0].NombreCompleto: "+ tipoID[0].NombreCompleto);
            Log.i("AsigID","tipoID[0].IdTAM: "+ tipoID[0].IdTAM);
            Log.i("AsigID","tipoID[0].TipoTAM: "+ tipoID[0].TipoTAM);
            Log.i("AsigID","tipoID[0].IdENR: "+ tipoID[0].IdENR);
            Log.i("AsigID", "tipoID[0].TipoENR: " + tipoID[0].TipoENR);
            edt_participant_name.setText(tipoID[0].NombreCompleto);



         //  if (get_ENR==1){

               //AsigarIdENR(mTipoVisita,mTipoAsig,tipoID);
//
         //  }

            if (selGrupo.equals("1")  && selVisita.equals("1") )
            {
                mTipoVisita = getString(R.string.screening) ;
                txt_id_type.setText(mTipoVisita);
                mTipoAsig = String.valueOf(tipoID[0].TipoTAM);
                get_ENR=20;
                Log.i("AsigID", "mTipoAsig:" + mTipoAsig);
                if (tipoID[0].IdTAM == null || tipoID[0].IdTAM.equals("anyType{}")){
                    edt_participant_id.setText("");
                    edt_participant_id.setFocusable(true);
                    edt_participant_id.setEnabled(false);
                }


                else{
                    edt_participant_id.setText(tipoID[0].IdTAM);
                    edt_participant_id.setFocusable(false);
                }
                if (mTipoAsig.equals("2"))  //   Asignar automáticamente
                {
                    //

                    String mIdAsig = "";
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    edt_participant_id.setText(resul);
                    edt_participant_id.setEnabled(false);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();



                    btn_accept1.setEnabled(false);
                }
                if (mTipoAsig.equals("0") || mTipoAsig.equals("1"))  //     Asignar manualmente
                {
                    edt_participant_id.setFocusable(true);
                    btn_accept1.setEnabled(true);
                    edt_participant_id.setEnabled(true);
                    get_ENR = 23;
                }


            }

            if (selGrupo.equals("2")  && selVisita.equals("1")   && estadoTAM.equals("1") && estadoENR.equals("1")   ){

                 if (get_ENR==0) {
                     mTipoVisita = getString(R.string.screening);
                     txt_id_type.setText(getString(R.string.screening));
                     if ((tipoID[0].IdENR == null || tipoID[0].IdENR.equals("anyType{}")) && (tipoID[0].IdTAM.toString()=="anyType{}"||tipoID[0].IdTAM.toString()==null  )){
                         edt_participant_id.setText("");
                         edt_participant_id.setFocusable(true);
                         txt_id_type.setText(getString(R.string.screening));



                         if (String.valueOf(tipoID[0].TipoENR).equals("2") )  //   Asignar automáticamente
                         {
                             //
                             String mIdAsig = "";
                             String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                             edt_participant_id.setText(resul);
                             Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();


                             btn_accept1.setEnabled(false);
                             validar_emr = true;
                             //    AsigarIdENR(mTipoVisita,mTipoAsig);

                         }
                         if (String.valueOf(tipoID[0].TipoENR).equals("0") || String.valueOf(tipoID[0].TipoENR).equals("1"))  //     Asignar manualmente
                         {
                             ActivarBotenes();
                            // edt_participant_id.setFocusable(true);
                            // btn_accept1.setEnabled(true);
                             //edt_participant_id.setEnabled(true);
                             validar_emr = true;
                             // AsigarIdENR(mTipoVisita,mTipoAsig);
                         }

                     }

                     else   if ((tipoID[0].IdENR == null || tipoID[0].IdENR.equals("anyType{}")) && (tipoID[0].IdTAM.toString()!="anyType{}"||tipoID[0].IdTAM.toString()!=null  )) {

                         if (selProyecto.equals("26")) {
                             get_ENR = 0;
                             validar_emr=true;
                         } else {
                         get_ENR = 4;
                         txt_id_type.setText(getString(R.string.enrollment));
                         if (String.valueOf(tipoID[0].TipoENR).equals("2"))  //   Asignar automáticamente
                         {
                             //
                             String mIdAsig = "";
                             String resul = AsignarID(selLocal, selProyecto, codigopaciente, mIdAsig, codigousuario, url);
                             edt_participant_id.setText(resul);
                             Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();


                             btn_accept1.setEnabled(false);
                             //validar_emr = true;
                             //    AsigarIdENR(mTipoVisita,mTipoAsig);

                         }
                         if (String.valueOf(tipoID[0].TipoENR).equals("0") || String.valueOf(tipoID[0].TipoENR).equals("1"))  //     Asignar manualmente
                         {
                             ActivarBotenes();
                             // edt_participant_id.setFocusable(true);
                             // btn_accept1.setEnabled(true);
                             //edt_participant_id.setEnabled(true);
                             // validar_emr = true;
                             // AsigarIdENR(mTipoVisita,mTipoAsig);
                         }
                     }
                     }




                 }

                else if (get_ENR > 0) {

                     mTipoVisita = getString(R.string.enrollment);

                     edt_participant_id.setText("");
                     edt_participant_id.setFocusable(true);
                     edt_participant_id.setEnabled(true);



                txt_id_type.setText(mTipoVisita);
                mTipoAsig = String.valueOf(tipoID[0].TipoENR);
                Log.i("AsigID", "mTipoAsig:" + mTipoAsig);
                if (tipoID[0].IdENR == null || tipoID[0].IdENR.equals("anyType{}")){
                    edt_participant_id.setText("");
                    edt_participant_id.setFocusable(true);
                    edt_participant_id.setEnabled(true);

                }else{
                    edt_participant_id.setText(tipoID[0].IdTAM);
                    edt_participant_id.setFocusable(false);
                }
                if (mTipoAsig.equals("2"))  //   Asignar automáticamente
                {
                    //
                    btn_accept1.setEnabled(false);
                    String mIdAsig = "";
                    get_ENR=4;
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();
                    edt_participant_id.setText(resul);


                     validar_emr = true;
                //    AsigarIdENR(mTipoVisita,mTipoAsig);

                }
                if (mTipoAsig.equals("0") || mTipoAsig.equals("1"))  //     Asignar manualmente
                {
                    ActivarBotenes();
                    validar_emr = true;
                   // AsigarIdENR(mTipoVisita,mTipoAsig);
                }

                 }
            }



            if (selGrupo.equals("2")  && selVisita.equals("1")   && estadoENR.equals("0") && estadoTAM.equals("1"))
            {

                txt_id_type.setText(getString(R.string.screening));
                mTipoAsig = String.valueOf(tipoID[0].TipoTAM);
                edt_participant_id.setText(tipoID[0].IdENR);
                if (tipoID[0].IdTAM == null || tipoID[0].IdTAM.equals("anyType{}")){
                    edt_participant_id.setText("");
                    edt_participant_id.setFocusable(true);
                }else{
                    edt_participant_id.setText(tipoID[0].IdENR);
                    edt_participant_id.setFocusable(false);
                }
                if (mTipoAsig.equals("2"))      // Asignar automáticamente
                {
                    //
                    String mIdAsig = "";
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();
                    edt_participant_id.setText(resul);
                    //
                    edt_participant_id.setFocusable(false);
                    btn_accept1.setEnabled(false);
                }
                if (mTipoAsig.equals("0") || mTipoAsig.equals("1"))  //     Asignar manualmente
                {   get_ENR = 22;
                    edt_participant_id.setFocusable(true);
                    btn_accept1.setEnabled(true);

                }
            }


            if (selGrupo.equals("2")  && selVisita.equals("1")   && estadoENR.equals("1") && estadoTAM.equals("0"))
            {

                txt_id_type.setText(getString(R.string.enrollment));
                mTipoAsig = String.valueOf(tipoID[0].TipoENR);
                edt_participant_id.setText(tipoID[0].IdENR);
                if (tipoID[0].IdENR == null || tipoID[0].IdENR.equals("anyType{}")){
                    edt_participant_id.setText("");
                    edt_participant_id.setFocusable(true);
                }else{
                    edt_participant_id.setText(tipoID[0].IdENR);
                    edt_participant_id.setFocusable(false);
                }
                if (mTipoAsig.equals("2"))      // Asignar automáticamente
                {
                    //
                    String mIdAsig = "";
                    String resul = AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
                    Toast.makeText(getBaseContext(), resul, Toast.LENGTH_SHORT).show();
                    edt_participant_id.setText(resul);
                    //
                    edt_participant_id.setFocusable(false);
                    btn_accept1.setEnabled(false);
                }
                if (mTipoAsig.equals("0") || mTipoAsig.equals("1"))  //     Asignar manualmente
                {
                    edt_participant_id.setFocusable(true);
                    btn_accept1.setEnabled(true);

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }




//AsignarID(selLocal, selProyecto,codigopaciente, mIdAsig,  codigousuario, url);
    private String AsignarID(String selLocal,String selProyecto,String selPaciente, String idAsig,String selUsuario, String url){
        String msj = "";
        String tipoAsig = "";
        try {

            if (selGrupo.equals("1")  && selVisita.equals("1")) {
                tipoAsig = String.valueOf(tipoID[0].TipoTAM);
                idAsigTAM = idAsig;
                GenerarIdTAMTask generarIdTAM = new GenerarIdTAMTask();
                //String tipoReg, int CodigoLocal, int CodigoProyecto, String CodigoPaciente, String IdAsig, int CodigoUsuario
                genIdTAM =  generarIdTAM.execute(tipoAsig, selLocal, selProyecto, selPaciente, idAsigTAM, selUsuario, url);
                msj= genIdTAM.get();
                if (mTipoAsig.equals("2")){
                    edt_participant_id.setText(msj);

                }
            }


            if (selGrupo.equals("1")  && selVisita.equals("1")  && get_ENR ==23) {

                tipoAsig = String.valueOf(tipoID[0].TipoTAM);
                idAsigTAM = idAsig;
                GenerarIdTAMTask generarIdTAM = new GenerarIdTAMTask();
                //String tipoReg, int CodigoLocal, int CodigoProyecto, String CodigoPaciente, String IdAsig, int CodigoUsuario
                genIdTAM =  generarIdTAM.execute(tipoAsig,selLocal,selProyecto,selPaciente,idAsigTAM,selUsuario,url);
                msj= genIdTAM.get();

            }
            if (selGrupo.equals("2")  && selVisita.equals("1")&& get_ENR ==0) {
                tipoAsig = String.valueOf(tipoID[0].TipoTAM);
                idAsigTAM = idAsig;
                GenerarIdTAMTask generarIdTAM = new GenerarIdTAMTask();
                //String tipoReg, int CodigoLocal, int CodigoProyecto, String CodigoPaciente, String IdAsig, int CodigoUsuario
                genIdTAM =  generarIdTAM.execute(tipoAsig,selLocal,selProyecto,selPaciente,idAsigTAM,selUsuario,url);
                msj= genIdTAM.get();

            }
            if (selGrupo.equals("2")  && selVisita.equals("1") &&get_ENR > 0) {
                tipoAsig = String.valueOf(tipoID[0].TipoENR);
                idAsigENR = idAsig;
                GenerarIdENRTask generarIdENR = new GenerarIdENRTask();
                genIdENR =  generarIdENR.execute(tipoAsig,selLocal,selProyecto,selPaciente,idAsigENR,selUsuario,url);
                msj= genIdENR.get();

            }

            if (selGrupo.equals("2")  && selVisita.equals("1") &&get_ENR  ==4) {
                tipoAsig = String.valueOf(tipoID[0].TipoENR);
                idAsigENR = idAsig;
                GenerarIdENRTask generarIdENR = new GenerarIdENRTask();
                genIdENR =  generarIdENR.execute(tipoAsig,selLocal,selProyecto,selPaciente,idAsigENR,selUsuario,url);
                msj= genIdENR.get();

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            msj=e.getMessage();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
            msj=ex.getMessage();
        }
        Log.i("AsignarID","AsignarID: " + msj);
        return msj;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            //case VISITLIST_ID:
    	    //    if (!UrlUtils.validData(doc_identidad, "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")){
    		//		Toast.makeText(getBaseContext(), "Nro. de DNI invalido!!",Toast.LENGTH_SHORT).show();
    		//	}
    	    //    else
    		//	{
            //        VISITLIST();
    		//	}
            //    return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }



    public  void ActivarBotenes (){
         btn_accept1.setEnabled(true);
         edt_participant_id.setEnabled(true);



    }


    public  void  ValidarActBotones(){

          if (get_ENR>0  && get_ENR<10){

              edt_participant_id.setEnabled(true);
              edt_participant_id.setText("");
          }

          else if (get_ENR==20){



          }

        else {}

    }
	
}
