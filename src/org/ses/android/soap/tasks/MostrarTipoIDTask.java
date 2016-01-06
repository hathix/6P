package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Idreg;

/**
 * Created by User on 25/08/2014.
 */

public class MostrarTipoIDTask extends AsyncTask<String,String,Idreg[]> {

    private Idreg[] lstIdregs;
    @Override
    protected Idreg[] doInBackground(String... params) {

        Idreg[] resul= null;

        int count = params.length;
        if(count==4){

            String urlserver = params[3];
            final String NAMESPACE = StringConexion.conexion;
            final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
            final String METHOD_NAME = "MostrarTipoId";
            final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("CodigoLocal", params[0]);
            request.addProperty("CodigoProyecto",params[1]);
            request.addProperty("CodigoPaciente",params[2]);


            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);
                SoapObject resSoap =(SoapObject)envelope.getResponse();

                lstIdregs = new Idreg[resSoap.getPropertyCount()];

                for (int i = 0; i < lstIdregs.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Idreg reg = new Idreg();
                    reg.NombreCompleto = ic.getProperty(0).toString();
                    reg.TipoTAM = Integer.valueOf(ic.getProperty(1).toString());
                    reg.IdTAM= ic.getProperty(2).toString();
                    reg.TipoENR = Integer.valueOf(ic.getProperty(3).toString());
                    reg.IdENR= ic.getProperty(4).toString();

                    lstIdregs[i] = reg;
                }
                if (resSoap.getPropertyCount()>0){
                    resul = lstIdregs;
                }            }
            catch (Exception e)
            {
                resul = null;
            }
        }
        return resul;
    }
}

