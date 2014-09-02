package org.ses.android.soap.database;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by jtomaylla on 25/08/2014.
 */

public class Idreg implements KvmSerializable {

    public String NombreCompleto;
    public int TipoTAM;
    public String IdTAM;
    public int TipoENR;
    public String IdENR;

    public Idreg()
    {
        NombreCompleto = "";
        TipoTAM = 0;
        IdTAM = "";
        TipoENR = 0;
        IdENR = "";
    }

    public Idreg(

            String NombreCompleto,
            int TipoTAM,
            String IdTAM,
            int TipoENR,
            String IdENR

    )
    {
        this.NombreCompleto = NombreCompleto;
        this.TipoTAM  = TipoTAM ;
        this.IdTAM = IdTAM;
        this.TipoENR = TipoENR;
        this.IdENR  = IdENR;
    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return NombreCompleto;
            case 1:
                return TipoTAM;
            case 2:
                return IdTAM;
            case 3:
                return TipoENR;
            case 4:
                return IdENR;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NombreCompleto";
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "TipoTAM";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "IdTAM";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "TipoENR";
                break;
            case 4:
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
                NombreCompleto = val.toString();
                break;
            case 1:
                TipoTAM  = Integer.parseInt(val.toString());
                break;
            case 2:
                IdTAM = val.toString();
                break;
            case 3:
                TipoENR  = Integer.parseInt(val.toString());
                break;
            case 4:
                IdENR  = val.toString();
                break;

            default:
                break;
        }
    }
}
