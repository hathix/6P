package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Participant;

public class ParticipantLoadFromCodigoTask extends AsyncTask<String, String, Participant> {

    @Override
    protected Participant doInBackground(String... params) {

        Participant[] lstParticipant = null;
        Participant resul = null;

        String urlserver = params[1];
        final String NAMESPACE = StringConexion.conexion;
        final String URL = NAMESPACE + "WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ObtenerPacienteDeCodigoPaciente";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("codigopaciente", params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap = (SoapObject) envelope.getResponse();

            lstParticipant = new Participant[resSoap.getPropertyCount()];

            SoapObject ic = (SoapObject) resSoap.getProperty(0);

            Participant par = new Participant();

            par.CodigoPaciente = ic.getProperty(0).toString();
            par.Nombres = ic.getProperty(1).toString();
            par.ApellidoPaterno = ic.getProperty(2).toString();
            par.ApellidoMaterno = ic.getProperty(3).toString();
            par.CodigoTipoDocumento = Integer.parseInt(ic.getProperty(4).toString());
            par.DocumentoIdentidad = ic.getProperty(5).toString();
            par.FechaNacimiento = ic.getProperty(6).toString();
            par.Sexo = Integer.parseInt(ic.getProperty(7).toString());
            lstParticipant[0] = par;

            if (resSoap.getPropertyCount() > 0) {
                resul = lstParticipant[0];
            }

        } catch (Exception e) {
            resul = null;
        }

        return resul;
    }
}
