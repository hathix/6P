package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



/**
 * Created by franciscorivera on 1/8/16.
 * Is initialized by a codigoPaciente (primary key corresponding to a participant,
 * then the doInBackground method takes a 400 byte array representing a fingerprint
 * and saves it to the database corresponding to the codigoPaciente. Returns
 * whatever message the SOAP API returns.
 */
public class AgregarHuellaTask extends AsyncTask<Byte, Void, String> {

    private String codigoPaciente;

    public  AgregarHuellaTask(String codigoPaciente)
    {
        this.codigoPaciente = codigoPaciente;
    }

    @Override
    protected String doInBackground(Byte ... params)
    {
        String resul = "";

        final String NAMESPACE = StringConexion.conexion;
        final String SERVICE_NAME = StringConexion.serviceName;
        final String URL=NAMESPACE+SERVICE_NAME;
        final String METHOD_NAME = "AgregarHuella";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("huella", params);
        request.addProperty("codigo", codigoPaciente);

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
            Log.i("mensaje", resul);


        }
        catch (Exception e)
        {
            resul = "";
        }

        return resul;

    }
}
