package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



/**
 * Created by franciscorivera on 1/8/16.
 * Take codigoPaciente and fingerprint in string form
 * and save fingerprint to the database corresponding to the codigoPaciente.
 */
public class AgregarHuellaTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String ... params)
    {
        String resul = "OK";

        final String NAMESPACE = StringConexion.conexion;
        final String SERVICE_NAME = StringConexion.serviceName;
        final String URL=NAMESPACE+SERVICE_NAME;
        final String METHOD_NAME = "AgregarHuella";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoPaciente", params[0]);
        request.addProperty("Huella", params[1]);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
            resul = resultado_xml.toString();
            Log.i("AgregarHuellaTask", "resul: " + resul);
            int intRes = Integer.parseInt(resul);
            if(intRes < 1)
                resul = "";
        }
        catch (Exception e)
        {
            resul = "";
        }

        return resul;

    }
}
