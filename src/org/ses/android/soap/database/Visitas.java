package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Visitas implements KvmSerializable {

	public String Proyecto;
	public String Visita;
	public String FechaVisita;
	public String HoraCita;
	public String EstadoVisita;
	public String CodigoProyecto;
	public String CodigoGrupoVisita;
	public String CodigoVisita;
	public String CodigoVisitas;
	public String CodigoEstatusPaciente;
	public String CodigoUsuario;
	
	public Visitas()
	{
		Proyecto = "";
		Visita = "";
		FechaVisita = "";
		HoraCita = "";
		EstadoVisita= "";
		CodigoProyecto= "";
		CodigoGrupoVisita= "";
		CodigoVisita= "";
		CodigoVisitas= "";
		CodigoEstatusPaciente= "";
		CodigoUsuario= "";
	}
	
	public Visitas(
			String Proyecto,
			String Visita,
			String FechaVisita,
			String HoraCita,
			String EstadoVisita,
			String CodigoProyecto,
			String CodigoGrupoVisita,
			String CodigoVisita,
			String CodigoVisitas,
			String CodigoEstatusPaciente,
			String CodigoUsuario			
	)
	{
		this.Proyecto = Proyecto;
		this.Visita = Visita;
		this.FechaVisita = FechaVisita;
		this.HoraCita = HoraCita;
		this.EstadoVisita  = EstadoVisita ;
		this.CodigoProyecto = CodigoProyecto;
		this.CodigoGrupoVisita = CodigoGrupoVisita;
		this.CodigoVisita = CodigoVisita;
		this.CodigoVisitas = CodigoVisitas;
		this.CodigoEstatusPaciente= CodigoEstatusPaciente;
		this.CodigoUsuario= CodigoUsuario;
	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return Proyecto;
        case 1:
            return Visita;
        case 2:
            return FechaVisita;
        case 3:
            return HoraCita;
        case 4:
            return EstadoVisita;
        case 5:
            return CodigoProyecto;
        case 6:
            return CodigoGrupoVisita;
        case 7:
            return CodigoVisita;
        case 8:
            return CodigoVisitas;
        case 9:
            return CodigoEstatusPaciente;
        case 10:
            return CodigoUsuario;            
        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 11;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
        case 0:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Proyecto";
            break;
        case 1:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Visita";
            break;
        case 2:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "FechaVisita";
            break;
        case 3:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "HoraCita";
            break;
        case 4:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "EstadoVisita";
            break;
        case 5:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoProyecto";
            break;
        case 6:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoGrupoVisita";
            break;
        case 7:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoVisita";
            break;            
        case 8:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoVisitas";
        case 9:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoEstatusPaciente";            
        case 10:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoUsuario";            
            break; 
        default:break;
        }
	}
	
	@Override
	public void  setProperty(int ind, Object val) {
		switch(ind)
        {
        case 0:
        	Proyecto  = val.toString();
            break;
        case 1:
        	Visita  = val.toString();
            break;
        case 2:
        	FechaVisita = val.toString();
            break;
        case 3:
        	HoraCita  = val.toString();
            break;
        case 4:
        	EstadoVisita  = val.toString();
            break;
        case 5:
        	CodigoProyecto  = val.toString();
            break;
        case 6:
        	CodigoGrupoVisita  = val.toString();
            break;
        case 7:
        	CodigoVisita  = val.toString();
            break;            
        case 8:
        	CodigoVisitas  = val.toString();
            break;
        case 9:
        	CodigoEstatusPaciente  = val.toString();
            break;
        case 10:
        	CodigoUsuario  = val.toString();
            break;            
        default:break;
        }
	}
}
