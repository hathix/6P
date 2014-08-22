package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Visita implements KvmSerializable {

	public int CodigoProyecto;
	public int CodigoGrupoVisita;
	public String NombreGrupoVisita;
	public int CodigoVisita;
	public String DescripcionVisita;
	public Boolean GenerarAuto;
	public int Dependiente;
	
	public Visita()
	{
		CodigoProyecto = 0;
		CodigoGrupoVisita = 0;
		NombreGrupoVisita = "";
		CodigoVisita= 0;
		DescripcionVisita= "";
		GenerarAuto = false;
		Dependiente = 0;
	}
	
	public Visita(
			int CodigoProyecto,
			int CodigoGrupoVisita,
			String NombreGrupoVisita,
			int CodigoVisita,
			String DescripcionVisita,
			Boolean GenerarAuto,
			int Dependiente
			)
	{
		this.CodigoProyecto = CodigoProyecto;
		this.CodigoGrupoVisita  = CodigoGrupoVisita ;
		this.NombreGrupoVisita = NombreGrupoVisita;
		this.CodigoVisita = CodigoVisita ;
		this.DescripcionVisita  = DescripcionVisita ;
		this.GenerarAuto = GenerarAuto;
		this.Dependiente = Dependiente;

	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return CodigoProyecto;
        case 1:
            return CodigoGrupoVisita;
        case 2:
            return NombreGrupoVisita;
        case 3:
            return CodigoVisita;
        case 4:
            return DescripcionVisita;
        case 5:
            return GenerarAuto;
        case 6:
            return Dependiente;

        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 7;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
        case 0:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "CodigoProyecto";
            break;
        case 1:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "CodigoGrupoVisita";
            break;
        case 2:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "NombreGrupoVisita";
            break;
        case 3:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "CodigoVisita";
            break;
        case 4:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "DescripcionVisita";
            break;
        case 5:
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "GenerarAuto";
            break;
        case 6:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "Dependiente";
            break;	            
            
	            
	        default:break;
        }
	}
	
	@Override
	public void  setProperty(int ind, Object val) {
		switch(ind)
        {
        case 0:
        	CodigoProyecto  = Integer.parseInt(val.toString());
            break;
        case 1:
        	CodigoGrupoVisita  = Integer.parseInt(val.toString());
            break;
        case 2:
        	NombreGrupoVisita = val.toString();
            break;
        case 3:
        	CodigoVisita  = Integer.parseInt(val.toString());
            break;
        case 4:
        	DescripcionVisita  = val.toString();
            break;
        case 5:
        	GenerarAuto = Boolean.valueOf(val.toString());
            break;
        case 6:
        	Dependiente  = Integer.parseInt(val.toString());
            break;

	    default:
	        break;
        }
	}
}
