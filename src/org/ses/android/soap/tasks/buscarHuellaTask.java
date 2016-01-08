package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ses.android.soap.database.Participant;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



/**
 * Created by franciscorivera on 1/8/16.
 */
public class buscarHuellaTask extends AsyncTask<Byte, Void, Participant> {

    @Override
    protected Participant doInBackground(Byte ... params)
    {
        // return bogus information
        Participant resul = new Participant("", "", "", "", 0, "", "", 0);


        /* what you should actually do--presently unbuilt because of lack of backend */

        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "BuscarHuella";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("huella", params);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();
            SoapObject ic = (SoapObject)resSoap.getProperty(0);

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
