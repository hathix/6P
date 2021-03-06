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
public class BuscarHuellaFiltradoTask extends AsyncTask<String, String, String> {

    @Override
    // Takes four parameters: fingerprint, first names, paternal last, maternal last
    protected String doInBackground(String ... params)
    {
        String resul = "";

        final String NAMESPACE = StringConexion.conexion;
        final String SERVICE_NAME = StringConexion.serviceName;
        final String URL=NAMESPACE+SERVICE_NAME;
        final String METHOD_NAME = "BuscarHuellaFiltrado";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("Huella", params[0]);
        request.addProperty("nombres", params[1]);
        request.addProperty("apellidop", params[2]);
        request.addProperty("apellidom", params[3]);

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
