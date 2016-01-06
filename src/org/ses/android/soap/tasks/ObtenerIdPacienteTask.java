package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by dvillanueva on 18/11/2015.
 */
public class ObtenerIdPacienteTask extends AsyncTask<String,String,String> {
    @Override
    protected String doInBackground(String... params) {
        String resul  = "";
        String urlserver = params[4];
        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ObtenerIdPaciente";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("nombre", params[0]);
        request.addProperty("apellp",params[1]);
        request.addProperty("apellm",params[2]);
        request.addProperty("fechaNac",params[3]);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
            String res = resultado_xml.toString();
            resul = res;
            Log.i("CodigoPaciente", "res: " + res);

        }
        catch (Exception e)
        {
            resul = "";
            Log.i("Exception" ,e.getMessage());
        }


        return resul;
    }
}
