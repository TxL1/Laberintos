package ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.SimpleXmlPullApp;
import net.UserItemAdapter;
import net.UserRecord;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import txl.loco.R;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class V_Tiempos extends Activity implements OnItemSelectedListener {

	private int myWidth;
	private int myHeight;
	
	private ImageView person;
	private TextView tmejor, ttmejor, ttop;
	private Button bContinuar;
	private Spinner spinner;
	
	private ListView listView;
	
	private ArrayList<UserRecord> values;
	private UserItemAdapter adapt;
	
	private ProgressDialog myProgressDialog = null; 
	
	private AsyncHttpPost asyncHttpPost;
	
	SharedPreferences preferences;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                       WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setContentView(R.layout.v_tiempos);  
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        myHeight = metrics.heightPixels;
        myWidth = metrics.widthPixels;

        iniciaPantalla();
        ajustaPantalla();        
        
        preferences = getSharedPreferences("loco",MODE_PRIVATE);          
        ttmejor.setText(preferences.getString("ultimoNombre", "JUG") + "  " + preferences.getString("tiempoFacil", "-"));
        
        /*
		 if (isOnline()) {
	      	
	        cargaLista(0);	
	     }
	     else Toast.makeText(getApplicationContext(), R.string.mConexionLista, Toast.LENGTH_SHORT).show();  
	     */
    }
    
    public void cargaLista(int cual) {
    	
    	
    	if (cual == 0) { // top facil
    		
    		ttmejor.setText(preferences.getString("ultimoNombre", "JUG") + "  " + preferences.getString("tiempoFacil", "-"));
    		
			myProgressDialog = ProgressDialog.show(V_Tiempos.this, getString(R.string.mCargandoFacil), getString(R.string.mEsperandoRespuesta), true); 
			myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
						
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("clave", "en1LdeLM4");
	    	data.put("servicio", "0");
	    	
	    	if (asyncHttpPost != null)
	    		asyncHttpPost.cancel(true);
	    	
	    	asyncHttpPost = new AsyncHttpPost(data,0);
	    	asyncHttpPost.execute("http://txl-estudios.es:8080/topPuntosLoco/puntuacionesLoco");
    	}
    	else if (cual == 1) { // top normal

    		ttmejor.setText(preferences.getString("ultimoNombre", "JUG") + "  " + preferences.getString("tiempoNormal", "-"));
    		
			myProgressDialog = ProgressDialog.show(V_Tiempos.this, getString(R.string.mCargandoNormal), getString(R.string.mEsperandoRespuesta), true); 
			myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
						
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("clave", "en1LdeLM4");
	    	data.put("servicio", "1");
	    	
	    	if (asyncHttpPost != null)
	    		asyncHttpPost.cancel(true);
	    	
	    	asyncHttpPost = new AsyncHttpPost(data,1);
	    	asyncHttpPost.execute("http://txl-estudios.es:8080/topPuntosLoco/puntuacionesLoco");
    	}
    	else if (cual == 2) { // top dificil

    		ttmejor.setText(preferences.getString("ultimoNombre", "JUG") + "  " + preferences.getString("tiempoDificil", "-"));
    		
			myProgressDialog = ProgressDialog.show(V_Tiempos.this, getString(R.string.mCargandoDificil), getString(R.string.mEsperandoRespuesta), true); 
			myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
						
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("clave", "en1LdeLM4");
	    	data.put("servicio", "2");
	    	
	    	if (asyncHttpPost != null)
	    		asyncHttpPost.cancel(true);
	    	
	    	asyncHttpPost = new AsyncHttpPost(data,2);
	    	asyncHttpPost.execute("http://txl-estudios.es:8080/topPuntosLoco/puntuacionesLoco");
    	}
    	else if (cual == 3) { // top insano

    		ttmejor.setText(preferences.getString("ultimoNombre", "JUG") + "  " + preferences.getString("tiempoInsano", "-"));
    		
			myProgressDialog = ProgressDialog.show(V_Tiempos.this, getString(R.string.mCargandoInsano), getString(R.string.mEsperandoRespuesta), true); 
			myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
						
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("clave", "en1LdeLM4");
	    	data.put("servicio", "3");
	    	
	    	if (asyncHttpPost != null)
	    		asyncHttpPost.cancel(true);
	    	
	    	asyncHttpPost = new AsyncHttpPost(data,3);
	    	asyncHttpPost.execute("http://txl-estudios.es:8080/topPuntosLoco/puntuacionesLoco");
    	}
    }
    
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
    private void iniciaPantalla() {
    	
    	listView = (ListView) findViewById(R.id.lista);
    	
	 	person = (ImageView) findViewById(R.id.grande);
	 	
	 	tmejor = (TextView) findViewById(R.id.tmejor);
	 	ttmejor = (TextView) findViewById(R.id.ttmejor);
	 	ttop = (TextView) findViewById(R.id.ttop);
    	
	    spinner = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.tams, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    	    
	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	            Object item = parent.getItemAtPosition(pos);
	            
		   		 if (isOnline()) {
		 	      	
		 	        cargaLista(pos);	
		 	     }
		 	     else Toast.makeText(getApplicationContext(), R.string.mConexionLista, Toast.LENGTH_SHORT).show();  
	        }
	        public void onNothingSelected(AdapterView<?> parent) {       	
	        }
	    });
	 	
	 	bContinuar = (Button) findViewById(R.id.bContinuar);
	 	bContinuar.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bContinuar.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bContinuar.setBackgroundResource(R.drawable.vacio);
					
					finish();
				}
				return false;
			}
		});
 }
	
    private void ajustaPantalla() {
    	
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myWidth/4,myHeight/6);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        person.setLayoutParams(params);
      
        params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/8);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
  	    bContinuar.setLayoutParams(params);
  	  
        params = new RelativeLayout.LayoutParams((myWidth/2) + (myWidth/6),LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,person.getId());
        spinner.setLayoutParams(params);
  	    
        params = new RelativeLayout.LayoutParams(tmejor.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,spinner.getId());
        params.topMargin = (myWidth/50);
        tmejor.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(ttmejor.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,tmejor.getId());
        params.topMargin = (myWidth/50);
        ttmejor.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(ttop.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,ttmejor.getId());
        params.topMargin = (myWidth/50);
        ttop.setLayoutParams(params);
  	    

    }
    
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub		
	}
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub		
	}
	
	
    // Thread de peticiones

	
   	public class AsyncHttpPost extends AsyncTask<String, String, String> {
           private HashMap<String, String> mData = null;// post data
           private boolean error = false;
           private int tipo;

           // informar
           public AsyncHttpPost(HashMap<String, String> data, int p_tipo) {
               mData = data;
               error = false;
               tipo = p_tipo;
           }

           // fijar timeout y encoding
           @Override
           protected String doInBackground(String... params) {
               byte[] result = null;
               String str = "";
               
                 HttpParams httpParameters = new BasicHttpParams();
   	         // Set the timeout in milliseconds until a connection is established.
   	         int timeoutConnection = 5000;
   	         HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
   	         // Set the default socket timeout (SO_TIMEOUT) 
   	         // in milliseconds which is the timeout for waiting for data.
   	         int timeoutSocket = 5000;
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
   					post.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
   					
   	                HttpResponse response;
   					try {

   						response = client.execute(post);				
   						HttpEntity httpEntity = response.getEntity();
   						str = EntityUtils.toString(httpEntity);
   						
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
           		
           		myProgressDialog.dismiss();
           		Toast.makeText(getApplicationContext(), R.string.mErrorCargando, Toast.LENGTH_SHORT).show();
           	}
           	else if (tipo == 0) { // tiempos facil

	           	SimpleXmlPullApp simple = new SimpleXmlPullApp();
	 	        try {
	 	        	myProgressDialog.dismiss();
	 	        	values = simple.lanza(result);
	 	        	adapt = new UserItemAdapter(V_Tiempos.this, R.layout.lista_puntuaciones , values, 0);
	 	            listView.setAdapter(adapt);
	
	 			} catch (XmlPullParserException e) {} 
	 	        catch (IOException e) {}
	       }
           	else if (tipo == 1) { // tiempos normal

	           	SimpleXmlPullApp simple = new SimpleXmlPullApp();
	 	        try {

	 	        	myProgressDialog.dismiss();
	 	        	values = simple.lanza(result);
	 	        	adapt = new UserItemAdapter(V_Tiempos.this, R.layout.lista_puntuaciones , values, 1);
	 	            listView.setAdapter(adapt);
	
	 			} catch (XmlPullParserException e) {} 
	 	        catch (IOException e) {}
	       }
           else if (tipo == 2) { // tiempos dificil
        	   
	           	SimpleXmlPullApp simple = new SimpleXmlPullApp();
	 	        try {

	 	        	myProgressDialog.dismiss();
	 	        	values = simple.lanza(result);
	 	        	adapt = new UserItemAdapter(V_Tiempos.this, R.layout.lista_puntuaciones , values, 2);
	 	            listView.setAdapter(adapt);
	
	 			} catch (XmlPullParserException e) {} 
	 	        catch (IOException e) {}
	       }
           else if (tipo == 3) { // tiempos insano
        	   
	           	SimpleXmlPullApp simple = new SimpleXmlPullApp();
	 	        try {

	 	        	myProgressDialog.dismiss();
	 	        	values = simple.lanza(result);
	 	        	adapt = new UserItemAdapter(V_Tiempos.this, R.layout.lista_puntuaciones , values, 3);
	 	            listView.setAdapter(adapt);
	
	 			} catch (XmlPullParserException e) {} 
	 	        catch (IOException e) {}
	       }
       }
   	}
}
