package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by fanneyzhu on 1/20/16.
 */
public class PacienteTieneHuellaTask extends AsyncTask<String, String, Integer> {

    @Override
    protected Integer doInBackground(String ... params)
    {
        int resul;

        final String NAMESPACE = StringConexion.conexion;
        final String SERVICE_NAME = StringConexion.serviceName;
        final String URL=NAMESPACE+SERVICE_NAME;
        final String METHOD_NAME = "PacienteTieneHuella";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoPaciente", params[0]);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
            resul = Integer.parseInt(resultado_xml.toString());
            Log.i("PacienteTieneHuellaTask", "resul: " + resul);
        }
        catch (Exception e)
        {
            resul = -1;
        }

        return resul;

    }
}
