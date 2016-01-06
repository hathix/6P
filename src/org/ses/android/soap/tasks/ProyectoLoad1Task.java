package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Proyecto;

public class ProyectoLoad1Task extends AsyncTask<String,String,Proyecto[]> {


    private Proyecto[] lstProyecto;

    @Override
    protected Proyecto[] doInBackground(String... params) {

        Proyecto[] resul= null;

        String urlserver = params[2];
        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ListadoProyectos1";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject("demo.sociosensalud.org.pe"+"/", METHOD_NAME);

        request.addProperty("CodigoLocal", params[0]);
        request.addProperty("CodigoUsuario", params[1]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstProyecto = new Proyecto[resSoap.getPropertyCount()];

            for (int i = 0; i < lstProyecto.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Proyecto pro = new Proyecto();

                pro.id = Integer.parseInt(ic.getProperty(0).toString());
                pro.nombre = ic.getProperty(1).toString();
                lstProyecto[i] = pro;
            }
            if (resSoap.getPropertyCount()>0){
                resul = lstProyecto;
            }

        }
        catch (Exception e)
        {
            resul = null;
        }

        return resul;
    }

}
