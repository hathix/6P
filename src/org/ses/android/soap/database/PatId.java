package org.ses.android.soap.database;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by jtomaylla on 19/09/2014.
 */

public class PatId implements KvmSerializable {

    public String CodigoProyecto;
    public String Proyecto;
    public String IdTAM;
    public String IdENR;

    public PatId()
    {
        CodigoProyecto = "";
        Proyecto = "";
        IdTAM = "";
        IdENR = "";
    }

    public PatId(
            String CodigoProyecto,
            String Proyecto,
            String IdTAM,
            String IdENR
    )
    {
        this.CodigoProyecto = CodigoProyecto;
        this.Proyecto = Proyecto;
        this.IdTAM = IdTAM;
        this.IdENR  = IdENR;
    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return CodigoProyecto;
            case 1:
                return Proyecto;
            case 2:
                return IdTAM;
            case 3:
                return IdENR;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CodigoProyecto";
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Proyecto";
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "IdTAM";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "IdENR";
                break;

            default:break;
        }
    }

    @Override
    public void  setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                CodigoProyecto = val.toString();
                break;
            case 1:
                Proyecto = val.toString();
                break;
            case 2:
                IdTAM = val.toString();
                break;
            case 3:
                IdENR  = val.toString();
                break;
            default:
                break;
        }
    }
}
