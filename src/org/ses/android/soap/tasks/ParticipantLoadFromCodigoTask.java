package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Participant;

public class ParticipantLoadFromCodigoTask extends AsyncTask<String, String, Participant> {

    @Override
    // Takes two parameters, the first of which is the codigoPaciente and the second of which
    // is the URL
    protected Participant doInBackground(String... params) {

        // C# method returns a list
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

            SoapObject resultSoap = (SoapObject) envelope.getResponse();

            lstParticipant = new Participant[resultSoap.getPropertyCount()];

            SoapObject propertySoap = (SoapObject) resultSoap.getProperty(0);

            Participant participantObject = new Participant();

            participantObject.CodigoPaciente = propertySoap.getProperty(0).toString();
            participantObject.Nombres = propertySoap.getProperty(1).toString();
            participantObject.ApellidoPaterno = propertySoap.getProperty(2).toString();
            participantObject.ApellidoMaterno = propertySoap.getProperty(3).toString();
            participantObject.CodigoTipoDocumento = Integer.parseInt(propertySoap.getProperty(4).toString());
            participantObject.DocumentoIdentidad = propertySoap.getProperty(5).toString();
            participantObject.FechaNacimiento = propertySoap.getProperty(6).toString();
            participantObject.Sexo = Integer.parseInt(propertySoap.getProperty(7).toString());
            lstParticipant[0] = participantObject;

            if (resultSoap.getPropertyCount() > 0) {
                resul = lstParticipant[0];
            }

        } catch (Exception e) {
            resul = null;
        }

        return resul;
    }
}
