package net;

import java.util.ArrayList;

import txl.loco.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class UserItemAdapter extends ArrayAdapter<UserRecord> {
	
    private ArrayList<UserRecord> users;
    private Context context;
    private int modo;

    public UserItemAdapter(Context context, int textViewResourceId, ArrayList<UserRecord> users, int modo) {
        super(context, textViewResourceId, users);
        this.users = users;
        this.context = context;
        this.modo = modo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lista_puntuaciones, null);
        }
      
        UserRecord user = users.get(position);
        if (user != null) {
            TextView nombre = (TextView) v.findViewById(R.id.lnombre);
            TextView posicion = (TextView) v.findViewById(R.id.lposicion);
            TextView fecha = (TextView) v.findViewById(R.id.lfecha);
            TextView puntuacion = (TextView) v.findViewById(R.id.lpuntuacion);

      if (nombre != null) {
    	  nombre.setText(" " + user.nombre);
      }

      if(posicion != null) {
    	  posicion.setText(user.posicion + " ");
      }
      
      if (fecha != null) {
    	  fecha.setText(" " + user.fecha + " ");
      }

      if (puntuacion != null) {
    	 
    	  if (modo == 0) puntuacion.setText(" " + transformaTiempo(user.facil));
    	  else if (modo == 1) puntuacion.setText(" " + transformaTiempo(user.normal));
    	  else if (modo == 2) puntuacion.setText(" " + transformaTiempo(user.dificil));
    	  else puntuacion.setText(" " + transformaTiempo(user.insano));
      }
}
  return v;
    }
    
    public String transformaTiempo(String tiempo) {
    	
    	String res;
    	int aux = Integer.valueOf(tiempo);
    	
    	if (aux < 10) res = "00:0" + tiempo;
    	else if (aux < 100) res = "00:" + tiempo;
    	else if (aux < 1000) {
    		res = "0" + tiempo;
    		res = res.substring(0,2) + ":" + res.substring(2,4);
    	}
    	else {
    		if (!tiempo.equals("9999"))
    			res = tiempo.substring(0,2) + ":" + tiempo.substring(2,4);
    		else res = "-";
    	}
    	
    	return res;
    }
}
