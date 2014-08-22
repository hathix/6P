package org.ses.android.soap.widgets;

import org.ses.android.seispapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GrupoBotones extends LinearLayout
	{
	private Button btnAnterior;
	private Button btnSiguiente;
	private OnAnteriorListener listenerAnt;
	private OnSiguienteListener listenerSig;
	
	public GrupoBotones(Context context) {
		super(context);
		inicializar();
	}
	public GrupoBotones(Context context, AttributeSet attrs) {
	super(context, attrs);
		inicializar();
		}
	private void inicializar()
	{
	//Utilizamos el layout 'control_login' como interfaz del control
	String infService = Context.LAYOUT_INFLATER_SERVICE;
	LayoutInflater li =
	(LayoutInflater)getContext().getSystemService(infService);
	li.inflate(R.layout.grupo_botones, this, true);
	//Obtenemoslas referencias a los distintos control

	btnAnterior = (Button)findViewById(R.id.btnAnteriorId);
	btnSiguiente = (Button)findViewById(R.id.btnSiguienteId);

	//Asociamos los eventos necesarios
	asignarEventos();
	}
	
	private void asignarEventos()
	{
		btnAnterior.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				listenerAnt.OnAnterior();
			}
		});
		btnSiguiente.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				listenerSig.OnSiguiente();
			}
		});
	}
	public interface OnAnteriorListener
	{
	void OnAnterior();
	}
	
	public interface OnSiguienteListener
	{
	void OnSiguiente();
	}
	public void setOnAnteriorListener(OnAnteriorListener a)
	{
	listenerAnt = a;
	}
	public void setOnSiguienteListener(OnSiguienteListener s)
	{
	listenerSig = s;
	}
}
