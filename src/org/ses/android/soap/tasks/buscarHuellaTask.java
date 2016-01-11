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
 * Used to query the SOAP API for a fingerprint and retrieve the
 * participant object that the fingerprint belongs to (null if
 * error)
 */

public class BuscarHuellaTask extends AsyncTask<Byte, Void, Participant> {


    @Override
    protected Participant doInBackground(Byte ... params)
    {
        Participant participant = new Participant();
        final String NAMESPACE = StringConexion.conexion;
        final String SERVICE_NAME = StringConexion.serviceName;
        final String URL=NAMESPACE+SERVICE_NAME;
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
            SoapObject dataObject = (SoapObject)resSoap.getProperty(0);

            participant.CodigoPaciente = dataObject.getProperty(0).toString();
            participant.Nombres = dataObject.getProperty(1).toString();
            participant.ApellidoPaterno = dataObject.getProperty(2).toString();
            participant.ApellidoMaterno = dataObject.getProperty(3).toString();
            participant.CodigoTipoDocumento = Integer.parseInt(dataObject.getProperty(4).toString());
            participant.DocumentoIdentidad = dataObject.getProperty(5).toString();
            participant.FechaNacimiento = dataObject.getProperty(6).toString();
            participant.Sexo = Integer.parseInt(dataObject.getProperty(7).toString());
            Log.i("CodigoPaciente", "res: " + participant.DocumentoIdentidad);

        }
        catch (Exception e)
        {
            participant = null;
            Log.i("Exception" ,e.getMessage());
        }


        return participant;

    }
}
