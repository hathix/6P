/**
 * Created by saranya on 1/19/16.
 * This logs in the passed in Visitas bundle into the database. Changes FechaUpdEstado and the Estado
 * Changes Estado with EstadoVisitaTask
 */

package org.ses.android.soap;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.FormListTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.tasks.VisitasListTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LogVisitActivity extends Activity {
    static final int DATE_DIALOG_ID = 999;
    private static final int ACTIVITY_VISITLIST = 1;
    private static final int PROGRESS_DIALOG = 1;
    private String url = "";
    String codigopaciente = "";
    String patientname = "";
    String codigoproyecto = "";
    private AsyncTask<String, String, Visitas[]> loadVisitas;
    SharedPreferences mPreferences;
    private String mAlertMsg;
    private TextView lbl_novisits;
    private ListView lstVisit;
    private TextView lbl_nombres;
    private Visitas[] datos;
    private Visitas vis;

    public ProgressDialog mProgressDialog;
    private VisitasListTask mVisitaListTask;
    private AsyncTask<String, String, String> formListTask;
    private AsyncTask<String, String, String> estadoVisita;
    private AsyncTask<String, String, Idreg[]> mostrarTipoID;
    private Idreg[] tipoID;
    String participantID = "";
    String participantName = "";
    private AsyncTask<String, String, String> formList1Task;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_visit_layout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = StringConexion.conexion;
        //lbl_nombres = (TextView) findViewById(R.id.lbl_nombres);
        codigopaciente = mPreferences.getString("CodigoPaciente", "");
        Log.i("codigo paciente:", codigopaciente);
        patientname = getString(R.string.txtvisit) + "(" + mPreferences.getString("patient_name", "") + ")";
        //lbl_nombres.setText(patientname);
        mAlertMsg = getString(R.string.please_wait);
        //codigoproyecto = mPreferences.getString("CodigoProyecto", "");
        codigoproyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        Log.i("LogVisitActivity", ".codigoproyecto:" + codigoproyecto);
        int intProject = Integer.valueOf(codigoproyecto);
        //button?

    }


    static class VisitHolder {
        TextView lblTitulo;
        TextView lblSubtitulo;
        Button btnDone;
        Button btnSeisD;


    }


    public void loadVisitas() {
        VisitasListTask tareaVisits = new VisitasListTask();
        AdaptadorVisitas adaptador;
        //should be a bundle, not an array

        String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        loadVisitas = tareaVisits.execute(codigopaciente, codigousuario, codigoproyecto, StringConexion.conexion);

        //pass a bundle tho

    }


    class AdaptadorVisitas extends ArrayAdapter<Visitas> {

        Context context;
        int layoutResourceId;
        ArrayList<Visitas> data = new ArrayList<Visitas>();

        AdaptadorVisitas(Context context, int layoutResourceId,
                         ArrayList<Visitas> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {

            View row = convertView;
            //final  int pos = position;
            VisitHolder holder = null;
            vis = data.get(position);

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new VisitHolder();
                /*
                holder.lblTitulo= (TextView) row.findViewById(R.id.LblTitulo);
                holder.lblSubtitulo= (TextView) row.findViewById(R.id.LblSubTitulo);
                holder.btnDone = (Button) row.findViewById(R.id.btnDone);
                holder.btnDone.setEnabled(false);
                holder.btnSeisD.setEnabled(false);
                //holder.ActivarBotones(vis.EstadoVisita.toString());
        */


                row.setTag(holder);
            } else {

                holder = (VisitHolder) convertView.getTag();
            }

            VisitHolder visitHolder = (VisitHolder) row.getTag();

            if (vis.EstadoVisita.equals("Atendido")) {

                visitHolder.btnSeisD.setEnabled(false);
                visitHolder.btnDone.setEnabled(false);
                Log.i("Enable:", "false");
            } else if (vis.EstadoVisita.toString() != "Atendido") {

                visitHolder.btnSeisD.setEnabled(true);
                visitHolder.btnDone.setEnabled(true);
                Log.i("Enable:", "true");

            }

            //lstVisit.setClickable(false);


            holder.lblTitulo.setText(vis.Proyecto + "/" + vis.Visita);
            holder.lblSubtitulo.setText(vis.FechaVisita + "-" + vis.HoraCita + "-" + vis.EstadoVisita);

            holder.btnDone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.i("btnDone Clicked", "**********");
//                  Toast.makeText(context, "btnDone Clicked",
//                    Toast.LENGTH_LONG).show();
//                  actualizarVisita(position,data);

                    //
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogVisitActivity.this);
                    builder.setMessage(getString(R.string.question_visit_done))
                            .setTitle(getString(R.string.opc_visit))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.answer_yes),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            actualizarVisita(position, data);
//                                              finish();
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
                    //
                }
            });

        }
    }

    class VisitHolder {
        TextView lblTitulo;
        TextView lblSubtitulo;
        Button btnDone;
        Button btnSeisD;

        public void AsignarBotones(View row) {


        }
    }

    // state update
    public void actualizarVisita(int pos, ArrayList<Visitas> data) {
        EstadoVisitaTask mEstadoVisitaTask = new EstadoVisitaTask();
        String url = StringConexion.conexion;
        String codigopaciente = mPreferences.getString("CodigoPaciente", "");
        String local_id = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        String user_id = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        Visitas visrow = data.get(pos);

        String project_id = visrow.CodigoProyecto;
        String visit_id = visrow.CodigoVisita;
        String visits_id = visrow.CodigoVisitas;
        String estado_visita_id = "3";
        String codigo_estatus_paciente = "1";

        estadoVisita = mEstadoVisitaTask.execute(
                local_id,
                project_id,
                visit_id,
                visits_id,
                codigopaciente,
                estado_visita_id,
                codigo_estatus_paciente,
                user_id,
                url);

        String actualizado;
        try {
            actualizado = estadoVisita.get();
            if (!actualizado.equals("OK")) {
                Toast.makeText(getBaseContext(), "No se actualizo Estado de Visita!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Estado de Visita actualizado!!", Toast.LENGTH_SHORT).show();
            }
            finish();
            Intent i = new Intent(LogVisitActivity.this, LogVisitActivity.class);
            startActivityForResult(i, ACTIVITY_VISITLIST);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}














