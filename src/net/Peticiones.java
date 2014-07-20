package net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.PeticionesPartida.AsyncHttpPost;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import ui.V_Combate;
import xml.xmlConectarMesa;
import xml.xmlConectarSalon;
import xml.xmlEsperaJugar;
import xml.xmlMovimientoJugador;
import xml.xmlSituacionPartida;
import android.os.AsyncTask;
import android.util.Log;
import dto.dtoConectarMesa;
import dto.dtoConectarSalon;
import dto.dtoEsperaJugar;
import dto.dtoMovimientoJugador;
import dto.dtoSituacionPartida;


public class Peticiones {

	private static String TAG = "Cinefilo.Peticiones";
	
	static int TIMEOUT = 5000;
	
	static int P_CONECTAR_SALON = 0, P_CONECTAR_MESA = 1, P_ESPERA_JUGAR = 2, P_SITUACION_PARTIDA = 3, P_MOVIMIENTO = 4;
	
	static String ip = "192.168.1.66";
	static String DOMINIO = "http://" + ip + ":8080/serverLoco/salon";
	static AsyncHttpPost asyncHttpPost;
		
	
	public static void peticionConectarSalon(String udid, String nombre, V_Combate v_comb) {	 
        
        HashMap<String, String> data = new HashMap<String, String>();    	
    	data.put("servicio", "CONECTAR_SALON");
    	data.put("udid", udid);   	
    	data.put("nombre", nombre);   	
    	
    	if (asyncHttpPost != null)
    		asyncHttpPost.cancel(true);
    	
    	asyncHttpPost = new AsyncHttpPost(data,P_CONECTAR_SALON,v_comb);
    	asyncHttpPost.execute(DOMINIO);
	}
	
	public static void peticionConectarMesa(String udid, int idJugador, V_Combate v_comb) {	 
        
        HashMap<String, String> data = new HashMap<String, String>();    	
    	data.put("servicio", "CONECTAR_MESA");
    	data.put("udid", udid);   	
    	data.put("idJugador", "" + idJugador);   	
    	
    	if (asyncHttpPost != null)
    		asyncHttpPost.cancel(true);
    	
    	asyncHttpPost = new AsyncHttpPost(data,P_CONECTAR_MESA,v_comb);
    	asyncHttpPost.execute(DOMINIO);
	}

	public static void peticionEsperaJugar(String udid, int idJugador, V_Combate v_comb) {	 
        
        HashMap<String, String> data = new HashMap<String, String>();    	
    	data.put("servicio", "ESPERA_JUGAR");
    	data.put("udid", udid);   	
    	data.put("idJugador", "" + idJugador);   	
    	
    	if (asyncHttpPost != null)
    		asyncHttpPost.cancel(true);
    	
    	asyncHttpPost = new AsyncHttpPost(data,P_ESPERA_JUGAR,v_comb);
    	asyncHttpPost.execute(DOMINIO);
	}
	
	public static void peticionSituacionPartida(String udid, int idJugador, int mov, V_Combate v_comb) {	 
        
        HashMap<String, String> data = new HashMap<String, String>();    	
    	data.put("servicio", "SITUACION_PARTIDA");
    	data.put("udid", udid);   	
    	data.put("idJugador", "" + idJugador);   	
    	data.put("mov", "" + mov);   	
    	
    	if (asyncHttpPost != null)
    		asyncHttpPost.cancel(true);
    	
    	asyncHttpPost = new AsyncHttpPost(data,P_SITUACION_PARTIDA,v_comb);
    	asyncHttpPost.execute(DOMINIO);
	}
	
	public static void peticionMovimientoJugador(String udid, int idJugador, int mov, V_Combate v_comb) {	 
        
        HashMap<String, String> data = new HashMap<String, String>();    	
    	data.put("servicio", "MOV_JUGADOR");
    	data.put("udid", udid);   	
    	data.put("idJugador", "" + idJugador);   
    	data.put("mov", "" + mov);   
    	
    	if (asyncHttpPost != null)
    		asyncHttpPost.cancel(true);
    	
    	asyncHttpPost = new AsyncHttpPost(data,P_MOVIMIENTO,v_comb);
    	asyncHttpPost.execute(DOMINIO);
	}
	
	
	
	public static class AsyncHttpPost extends AsyncTask<String, String, String> {
				
	    private HashMap<String, String> mData = null;// post data
	    private boolean error = false;
	    private int tipo;
	    private V_Combate v_comb;

	    // informar
	    public AsyncHttpPost(HashMap<String, String> data, int p_tipo, V_Combate p_v_reto) {
	        mData = data;
	        error = false;
	        tipo = p_tipo;
	        v_comb = p_v_reto;
	    }

	    // fijar timeout
	    @Override
	    protected String doInBackground(String... params) {
	        byte[] result = null;
	        String str = "";
	        
	          HttpParams httpParameters = new BasicHttpParams();
	         // Set the timeout in milliseconds until a connection is established.
	         int timeoutConnection = TIMEOUT;
	         HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	         // Set the default socket timeout (SO_TIMEOUT) 
	         // in milliseconds which is the timeout for waiting for data.
	         int timeoutSocket = TIMEOUT;
	         HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	        
	        HttpClient client = new DefaultHttpClient(httpParameters);
	        HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL

	            // set up post data
	            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	            Iterator<String> it = mData.keySet().iterator();
	            while (it.hasNext()) {
	                String key = it.next();
	                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
	            }

	            try {
					post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
					
	                HttpResponse response;
					try {

						response = client.execute(post);				
		                StatusLine statusLine = response.getStatusLine();
		                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
		                    result = EntityUtils.toByteArray(response.getEntity());
		                    str = new String(result, "UTF-8");
		                }
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						error = true;
					} catch (IOException e) {
						e.printStackTrace();
						error = true;
					}    
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					error = true;
				}        
	        return str;
	    }
	    
	    // tratar respuesta
	    @Override
	    protected void onPostExecute(final String result) {
	                	
	    	if (error) { // hay error
	    		
	    		if (tipo == P_CONECTAR_SALON)
	    			v_comb.respuestaConectarSalon(null);
	    		else if (tipo == P_CONECTAR_MESA)
	    			v_comb.respuestaConectarMesa(null);
	    		else if (tipo == P_ESPERA_JUGAR)
	    			v_comb.respuestaEsperaJugar(null);
	    		else if (tipo == P_SITUACION_PARTIDA)
	    			v_comb.respuestaEsperaJugar(null);
	    		else if (tipo == P_MOVIMIENTO)
	    			v_comb.respuestaMovimientoJugador(null);
	    	}
	    	else {
	    		
	    		Log.d(TAG,result);
	    		
	    		if (tipo == P_CONECTAR_SALON) {
	    				    			
	    			xmlConectarSalon simple = new xmlConectarSalon();
	    			dtoConectarSalon values = new dtoConectarSalon();
	    			try {
	    				values = simple.lanza(result);
					} 
	    			catch (XmlPullParserException e) {} 
	    			catch (IOException e) {}
	    			
	    			v_comb.respuestaConectarSalon(values);
	    		}
	    		else if (tipo == P_CONECTAR_MESA) {
	    			
	    			xmlConectarMesa simple = new xmlConectarMesa();
	    			dtoConectarMesa values = new dtoConectarMesa();
	    			try {
	    				values = simple.lanza(result);
					} 
	    			catch (XmlPullParserException e) {} 
	    			catch (IOException e) {}
	    			
	    			v_comb.respuestaConectarMesa(values);
	    		}
	    		else if (tipo == P_ESPERA_JUGAR) {
	    			
	    			xmlEsperaJugar simple = new xmlEsperaJugar();
	    			dtoEsperaJugar values = new dtoEsperaJugar();
	    			try {
	    				values = simple.lanza(result);
					} 
	    			catch (XmlPullParserException e) {} 
	    			catch (IOException e) {}
	    			
	    			v_comb.respuestaEsperaJugar(values);
	    		}
	    		else if (tipo == P_SITUACION_PARTIDA) {
	    			
	    			xmlSituacionPartida simple = new xmlSituacionPartida();
	    			dtoSituacionPartida values = new dtoSituacionPartida();
	    			try {
	    				values = simple.lanza(result);
					} 
	    			catch (XmlPullParserException e) {} 
	    			catch (IOException e) {}
	    			
	    			v_comb.respuestaSituacionPartida(values);
	    		}
	    		else if (tipo == P_MOVIMIENTO) {
	    			
					xmlMovimientoJugador simple = new xmlMovimientoJugador();
					dtoMovimientoJugador values = new dtoMovimientoJugador();
					try {
						values = simple.lanza(result);
					} 
					catch (XmlPullParserException e) {} 
					catch (IOException e) {}
					
					v_comb.respuestaMovimientoJugador(values);
				}
	    	}        	
	    }
	}
}
