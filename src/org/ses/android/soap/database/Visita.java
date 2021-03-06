package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ses.android.soap.models.Cacheable;

import org.json.JSONException;
import org.json.JSONObject;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Visita extends Cacheable implements KvmSerializable {

	public int CodigoProyecto;
	public int CodigoGrupoVisita;
	public String NombreGrupoVisita;
	public int CodigoVisita;
	public String DescripcionVisita;
	public Boolean GenerarAuto;
	public int Dependiente;
	public int DiasVisitaProx;
	public int DiasAntes;
	public int DiasDespues;
    public int OrdenVisita;

	public Visita(Cacheable cacheable) {
		JSONObject jsonObject = cacheable.jsonObject;
		try {
			this.CodigoProyecto = jsonObject.getInt("CodigoProyecto");
			this.CodigoGrupoVisita = jsonObject.getInt("CodigoGrupoVisita");
			this.NombreGrupoVisita = jsonObject.getString("NombreGrupoVisita");
			this.CodigoVisita = jsonObject.getInt("CodigoVisita");
			this.DescripcionVisita = jsonObject.getString("DescripcionVisita");
			this.GenerarAuto = jsonObject.getBoolean("GenerarAuto");
			this.Dependiente = jsonObject.getInt("Dependiente");
			this.DiasVisitaProx = jsonObject.getInt("DiasVisitaProx");
			this.DiasAntes = jsonObject.getInt("DiasAntes");
			this.DiasDespues = jsonObject.getInt("DiasDespues");
			this.OrdenVisita = jsonObject.getInt("OrdenVisita");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject toJSON() {
		JSONObject temp = new JSONObject();
		try {
			temp.put("CodigoProyecto", this.CodigoProyecto);
			temp.put("CodigoGrupoVisita", this.CodigoGrupoVisita);
			temp.put("NombreGrupoVisita", this.NombreGrupoVisita);
			temp.put("CodigoVisita", this.CodigoVisita);
			temp.put("DescripcionVisita", this.DescripcionVisita);
			temp.put("GenerarAuto", this.GenerarAuto);
			temp.put("Dependiente", this.Dependiente);
			temp.put("DiasVisitaProx", this.DiasVisitaProx);
			temp.put("DiasAntes", this.DiasAntes);
			temp.put("DiasDespues", this.DiasDespues);
			temp.put("OrdenVisita", this.OrdenVisita);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return temp;
	}
	
	public Visita()
	{
		CodigoProyecto = 0;
		CodigoGrupoVisita = 0;
		NombreGrupoVisita = "";
		CodigoVisita= 0;
		DescripcionVisita= "";
		GenerarAuto = false;
		Dependiente = 0;
		DiasVisitaProx = 0;
        DiasAntes = 0;
        DiasDespues = 0;
        OrdenVisita = 0;

	}
	
	public Visita(
			int CodigoProyecto,
			int CodigoGrupoVisita,
			String NombreGrupoVisita,
			int CodigoVisita,
			String DescripcionVisita,
			Boolean GenerarAuto,
			int Dependiente,
			int DiasVisitaProx,
			int DiasAntes,
			int DiasDespues,
            int OrdenVisita
			)
	{
		this.CodigoProyecto = CodigoProyecto;
		this.CodigoGrupoVisita  = CodigoGrupoVisita ;
		this.NombreGrupoVisita = NombreGrupoVisita;
		this.CodigoVisita = CodigoVisita ;
		this.DescripcionVisita  = DescripcionVisita ;
		this.GenerarAuto = GenerarAuto;
		this.Dependiente = Dependiente;
		this.DiasVisitaProx = DiasVisitaProx;
		this.DiasAntes = DiasAntes;
		this.DiasDespues = DiasDespues;
        this.OrdenVisita = OrdenVisita;

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
		case 7:
			return DiasVisitaProx;
        case 8:
            return DiasAntes;
        case 9:
            return DiasDespues;
        case 10:
            return OrdenVisita;

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
		case 7:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "DiasVisitaProx";
            break;
        case 8:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "DiasAntes";
            break;
        case 9:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "DiasDespues";
            break;
        case 10:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "OrdenVisita";
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
		case 7:
			DiasVisitaProx = Integer.parseInt(val.toString());
			break;
        case 8:
            DiasAntes = Integer.parseInt(val.toString());
            break;
        case 9:
            DiasDespues = Integer.parseInt(val.toString());
            break;
        case 10:
            OrdenVisita = Integer.parseInt(val.toString());
            break;

	    default:
	        break;
        }
	}
}
