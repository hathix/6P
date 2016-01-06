package org.ses.android.soap.database;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by dvillanueva on 06/11/2015.
 */
public class Departamento implements KvmSerializable {
     public  String  cod ;
    public  String descrip;


    public  Departamento(){

        cod = "";
        descrip = "";

    }

    public  Departamento ( String codigo ,String Descrip  ){
         this.cod = codigo;
        this.descrip = Descrip;


    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getProperty(int arg0) {
        switch(arg0)
        {
            case 0:
                return cod;
            case 1:
                return descrip;

        }

        return  null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch(i)
        {
            case 0:
                cod =  o.toString();
                           //toString.(val.toString());
                break;
            case 1:
                descrip  = o.toString();
                break;
            default:
                break;
        }


    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo info) {

        switch(i)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "cod";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "descripcion";
                break;
            default:break;
        }


    }
}
