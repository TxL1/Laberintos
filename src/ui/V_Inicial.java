package ui;

import java.util.ArrayList;
import java.util.List;

import log.Laberintos;
import txl.loco.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class V_Inicial extends Activity implements OnItemSelectedListener {

	private int myWidth;
	private int myHeight;
	
	private static int N = 5;
	private static int numNodos = N*N;	 	 
	private static int TAM_BASE;
 
	private int[][] coord;
	private List<String> codigos; 
	
	private int tam;
	
	private ImageView grande, pildoras, titulo;
	private Button bCombate, bSolo, bPuntuaciones;
	
	private Laberintos laberintos;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                       WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setContentView(R.layout.v_inicial);  
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        myHeight = metrics.heightPixels;
        myWidth = metrics.widthPixels;
        
        TAM_BASE = myWidth / (N*3);
        
        iniciaPantalla();
        ajustaPantalla();
        
        new LongOperation().execute("");
    }
    
    public void cargaCombate() {
    	
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final SharedPreferences preferences = getSharedPreferences("loco",MODE_PRIVATE); 
		
    	alert.setTitle(R.string.combate);
    	alert.setIcon(getResources().getDrawable(R.drawable.icono_small));
    	
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	
    	TextView nombre = new TextView(this);
    	nombre.setText(getString(R.string.nombre) + ":");
    	nombre.setGravity(Gravity.CENTER);
    	layout.addView(nombre);
    	
        final MyEditText input = new MyEditText(this);
        input.setText(preferences.getString("ultimoNombreCombate", "JUG"));
        input.setMaxLines(1);
        layout.addView(input);
        
    	alert.setView(layout);
  	    	
    	alert.setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
			
    	}});
    	
    	alert.setNegativeButton(R.string.continuar, new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {

    		 SharedPreferences.Editor editor = preferences.edit();
			 editor.putString("ultimoNombreCombate", input.getText().toString());
			 editor.commit();
			 
			 Intent i = new Intent(getApplicationContext(), V_Combate.class);
			 startActivity(i);	    		
    	}});
    	
    	alert.show();
    }
    
	public void cargaSolo() {
		 
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final SharedPreferences preferences = getSharedPreferences("loco",MODE_PRIVATE); 
			
	    	alert.setTitle(R.string.solo);
	    	alert.setIcon(getResources().getDrawable(R.drawable.icono_small));
	    	
	    	LinearLayout layout = new LinearLayout(this);
	    	layout.setOrientation(LinearLayout.VERTICAL);
	    	
	    	TextView nombre = new TextView(this);
	    	nombre.setText(getString(R.string.nombre) + ":");
	    	nombre.setGravity(Gravity.CENTER);
	    	layout.addView(nombre);
	    	
	        final MyEditText input = new MyEditText(this);
	        input.setText(preferences.getString("ultimoNombre", "JUG"));
	        input.setMaxLines(1);
	        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
	        layout.addView(input);
	        
	    	TextView siz = new TextView(this);
	    	siz.setText(getString(R.string.dificultad) + ":");
	    	siz.setGravity(Gravity.CENTER);
	    	layout.addView(siz);
	    	
	    	Spinner spinner = new Spinner(this);
	    	spinner.setOnItemSelectedListener(this);
	    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tams, android.R.layout.simple_spinner_item);
	    	// Specify the layout to use when the list of choices appears
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	// Apply the adapter to the spinner
	    	spinner.setAdapter(adapter);
	    	layout.addView(spinner);	       	    	
	       
	    	alert.setView(layout);
	  	    	
	    	alert.setPositiveButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
				
	    	}});
	    	
	    	alert.setNegativeButton(R.string.continuar, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {

	    		 SharedPreferences.Editor editor = preferences.edit();
				 editor.putString("ultimoNombre", input.getText().toString());
				 editor.commit();
				 
				 Intent i = new Intent(getApplicationContext(), V_Principal.class);
				 i.putExtra("N",tam);
				 i.putExtra("MODO",1);
				 startActivity(i);	    		
	    	}});
	    	
	    	alert.show();
	 }
	 
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)

		if (pos == 0) tam = 8;
		else if (pos == 1) tam = 12;
		else if (pos == 2) tam = 14;
		else if (pos == 3) tam = 15;
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
		tam = 8;
	}

    private void iniciaPantalla() {
    	
    	grande = (ImageView) findViewById(R.id.grande);
    	pildoras = (ImageView) findViewById(R.id.pildoras);
    	titulo = (ImageView) findViewById(R.id.titulo);
    	
    	bCombate = (Button) findViewById(R.id.bCombate);
    	bCombate.setEnabled(false);
    	bCombate.setBackgroundResource(R.drawable.vacio_press);
    	bCombate.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bCombate.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bCombate.setBackgroundResource(R.drawable.vacio);
					
					cargaCombate();
				}
				return false;
			}
		});
    	
    	bSolo = (Button) findViewById(R.id.bSolo);
    	bSolo.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bSolo.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bSolo.setBackgroundResource(R.drawable.vacio);
					cargaSolo();
				}
				return false;
			}
		});
    	
    	bPuntuaciones = (Button) findViewById(R.id.bPuntuaciones);
    	bPuntuaciones.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bPuntuaciones.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bPuntuaciones.setBackgroundResource(R.drawable.vacio);
					
					 Intent i = new Intent(getApplicationContext(), V_Tiempos.class);
					 startActivity(i);	 
				}
				return false;
			}
		});
    	
    }
    
    private void ajustaPantalla() {
    	
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/6);
 	    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titulo.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(myWidth/4,myHeight/6);
        params.addRule(RelativeLayout.BELOW,titulo.getId());
 	    //params.leftMargin = (myWidth / 6);
        grande.setLayoutParams(params);
        grande.bringToFront();
        
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.relativeLayout1);
        params = new RelativeLayout.LayoutParams(rel.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW,titulo.getId());
        rel.setLayoutParams(params);
        //rel.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(myWidth/3,myHeight/6);
        params.addRule(RelativeLayout.BELOW,titulo.getId());
 	    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
 	    pildoras.setLayoutParams(params);
        pildoras.bringToFront();
        
        params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/8);
        params.addRule(RelativeLayout.BELOW,grande.getId());
        params.topMargin = (myHeight / 10);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);        
  	    bCombate.setLayoutParams(params);
  	    
        params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/8);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
  	    params.addRule(RelativeLayout.BELOW,bCombate.getId());
  	    params.topMargin = (myHeight / 70);
  	    bSolo.setLayoutParams(params);
  	    
        params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/8);
  	    params.addRule(RelativeLayout.BELOW,bSolo.getId());
  	    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
  	    params.topMargin = (myHeight / 70);
  	    bPuntuaciones.setLayoutParams(params);
    }
    
    private void cargaLaberinto(List<String> cods) {
    	
    	RelativeLayout rel = (RelativeLayout) findViewById(R.id.relativeLayout1);
    	int col = 0, fila = 0;
    	for (int i = 0; i < numNodos; i++) {
    	    		
    		ImageView imageView = new ImageView(this);  
    		Drawable image = getResources().getDrawable(getResources().getIdentifier("c" + cods.get(i), "drawable", getPackageName()));
    		imageView.setImageDrawable(image);

	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
	        if (col != 0) params.topMargin += (TAM_BASE) * col;      
	        if (fila != 0) params.leftMargin = (TAM_BASE) * fila;

	        
    		fila = (fila + 1) % N;
    		
    		if (fila % N == 0) 
    			col = (col + 1) % N;
    		
    		rel.addView(imageView,params);
    		grande.bringToFront();
    	}
    }  
    
    
    class MyEditText extends EditText {

        public MyEditText(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
        public boolean onKeyDown(int keyCode, KeyEvent event)
        {
            if (keyCode==KeyEvent.KEYCODE_ENTER) 
            {
                // Just ignore the [Enter] key
                return true;
            }
            // Handle all other keys in the default way
            return super.onKeyDown(keyCode, event);
        }
    }
    
    
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
             
        	laberintos = new Laberintos();
        	
        	List<List<Integer>> ady = new ArrayList<List<Integer>>();
        	coord = new int[numNodos][N];
        	
        	// inicializa matriz con pesos aleatorios
    	   	coord =	laberintos.cargaMatriz(N,ady,numNodos);
    	   	
    	   	// calcula Kruskal
    	   	ady = laberintos.Grafo(ady,numNodos);
    	   	
    	   	// genera códigos imagenes
    	   	codigos = laberintos.generaCodigos(ady, coord,numNodos);

    	   	return "Executed";
        }      

        @Override
        protected void onPostExecute(String result) {
        	    	   	
            // carga laberinto
    	   	cargaLaberinto(codigos);
        }

        @Override
        protected void onPreExecute() {
        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        	
        }
  }   
}
