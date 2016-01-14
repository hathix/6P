package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Visitas;

/**
 * Created by saranya on 1/13/16.
 */


public class ProyectoVisitaListTask extends AsyncTask<String,String,Visitas[]>{

    private Visitas[] lstVisitas;

    @Override
    protected Visitas[] doInBackground(String... params) {

        Visitas[] resul= null;

//        String urlserver = params[1];
        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
//		final String METHOD_NAME = "ListadoVisitas";
//  JT_2015_08_13:ListadoVisitas2 pide 2 parametros muestra todas las visitas
//		final String METHOD_NAME = "ListadoVisitas2";
//  JT_2015_08_13:ListadoVisitas3 pide 3 parametros  muestra todas las visitas x Py
        final String METHOD_NAME = "ListadoVisitas3";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


        request.addProperty("CodigoProyecto", params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstVisitas = new Visitas[resSoap.getPropertyCount()];

            for (int i = 0; i < lstVisitas.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Visitas vis = new Visitas();
                vis.Proyecto = ic.getProperty(0).toString();
                vis.Visita = ic.getProperty(1).toString();
                vis.FechaVisita= ic.getProperty(2).toString();
                vis.HoraCita = ic.getProperty(3).toString();
                vis.EstadoVisita= ic.getProperty(4).toString();

                vis.CodigoProyecto= ic.getProperty(5).toString();
                vis.CodigoGrupoVisita= ic.getProperty(6).toString();
                vis.CodigoVisita= ic.getProperty(7).toString();
                vis.CodigoVisitas= ic.getProperty(8).toString();
                lstVisitas[i] = vis;

            }
            if (resSoap.getPropertyCount()>0){
                resul = lstVisitas;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            resul = null;
        }

        return resul;
    }

}


