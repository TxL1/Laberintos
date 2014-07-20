package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import net.Peticiones;
import net.PeticionesPartida;
import txl.loco.R;
import util.Cadenas;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import dto.dtoConectarMesa;
import dto.dtoConectarSalon;
import dto.dtoEsperaJugar;
import dto.dtoMovimientoJugador;
import dto.dtoSituacionPartida;


public class V_Combate extends Activity {

	private int myWidth;
	private int myHeight;
	
	 private static int MODO = 0;
	 
	 private static int N = 6;
	 private static int numNodos = N*N;
	 	 
	 private static int TAM_BASE;

	 private List<String> codigos; // datos laberinto en curso 

	 private RelativeLayout rel;
	 private ScrollView scroll;
	 private HorizontalScrollView hscroll;
	
	 private String celdaActual;
	 private int posicionActual, posicionRivalActual, posicionSolucion, segundos, menuActual;
	 private boolean listoNext;
	
	 private ImageView person, barra, galleta, grande, gall0, gall1, gall2, gall3, evil, lucha;
	 private TextView tcuenta, tpuntos, tnivel, tmejor, ttpuntos, ttnivel, ttmejor;
	  
	 private Button bIzq, bDer, bTop, bBot, bOtra, bSalir, bMenu;
	 private View fondo;
	
	 private MediaPlayer mpRonq, mpPaso, mpExito;
	
	 private CountDownTimer countdown;
	 private Timer timer;
	
	 private AnimationDrawable personAnim, evilAnim, luchaAnim;
	
	 private ProgressDialog myProgressDialog = null; 
	
	 private String _UDID, _NOMBRE, _CODIGOS, _POSICIONES;
	 private int _ID_JUGADOR, _ID_MESA, _ID_POSICION, MOVIMIENTO;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                       WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setContentView(R.layout.v_combate);  
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        myHeight = metrics.heightPixels;
        myWidth = metrics.widthPixels;
        
        TAM_BASE = myWidth / N;
        
        menuActual = 0;
        MOVIMIENTO = -1;
        
        cargaPantalla();
        ajustaPantalla();
        galleta.setVisibility(View.GONE);

        // carga mp3
        mpExito = MediaPlayer.create(this, R.raw.exito);
        mpPaso = MediaPlayer.create(this, R.raw.paso);
        mpRonq = MediaPlayer.create(this, R.raw.ronq);
        
        
 	   myProgressDialog = ProgressDialog.show(V_Combate.this, "LOCO", "Buscando combate..", true); 
 	   myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));

 	  _UDID = "xcvasasdddasdsada456456sdsadasdasdasdasddasdasdasdgre";
 	  _NOMBRE = "txl";
 	    	   
 	   Peticiones.peticionConectarSalon(_UDID, _NOMBRE, this);
    }

    private void generaLaberinto() {
    	
    	codigos = traduceCadenaCodigos();
    	
    	// carga laberinto
	   	cargaLaberinto(codigos);
	   	    	   	
	   	ajustaPantalla();
	   	galleta.setVisibility(View.VISIBLE);
	   	
	   	iniciaPosiciones();
	   		   	
	   	iniciaAnimacion();
	   	
	   	Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, this);
    }
    
    private List<String> traduceCadenaCodigos() {
    	
    	List<String> res = new ArrayList<String>();
    	
    		for (int i = 0; i < numNodos; i++) {
    			
    			res.add(Cadenas.objetoEnCadena(i, numNodos, _CODIGOS));
    		}
    	
    	return res;
    }
    
    private void cargaLaberinto(List<String> cods) {
    	
    	rel = (RelativeLayout) findViewById(R.id.relativeLayout1);
    	int col = 0, fila = 0;
    	for (int i = 0; i < numNodos; i++) {
    	    		
    		ImageView imageView = new ImageView(this);  
    		Drawable image = getResources().getDrawable(getResources().getIdentifier("c" + cods.get(i), "drawable", getPackageName()));
    		imageView.setImageDrawable(image);

	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
	        params.topMargin = (myWidth/8);   
	        if (col != 0) params.topMargin += (TAM_BASE) * col;      
	        if (fila != 0) params.leftMargin = (TAM_BASE) * fila;

	        
    		fila = (fila + 1) % N;
    		
    		if (fila % N == 0) 
    			col = (col + 1) % N;
    		
    		rel.addView(imageView,params);
    	}
    }
    
    private void iniciaPosiciones() {
    	    	    	
    	posicionActual = Integer.valueOf(Cadenas.objetoEnCadena(_ID_POSICION, 2, _POSICIONES));
    	MOVIMIENTO = -1;
    	
    	posicionRivalActual = Integer.valueOf(Cadenas.objetoEnCadena((_ID_POSICION + 1)%2, 2, _POSICIONES));
    	
    	actualizaPosiciones();
    	
    	celdaActual = codigos.get(posicionActual);  
    	    	
    	posicionSolucion = numNodos - 1;
    }
    
    private void actualizaPosiciones() {
    	
    	RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
		params.topMargin = (myWidth/8);
		params.topMargin += (TAM_BASE) * (posicionActual / N);
		   
		if ((posicionActual % N) != 0) params.leftMargin = (TAM_BASE) * (posicionActual % N);
	    person.setLayoutParams(params);
	    
	    params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
		params.topMargin = (myWidth/8);
		params.topMargin += (TAM_BASE) * (posicionRivalActual / N);
		   
		if ((posicionRivalActual % N) != 0) params.leftMargin = (TAM_BASE) * (posicionRivalActual % N);
	    evil.setLayoutParams(params);
	    
	    if (posicionRivalActual == posicionActual) {
	    	 lucha.setLayoutParams(params);
	    	 lucha.setVisibility(View.VISIBLE);
	    	 lucha.bringToFront();
	    	 evil.setVisibility(View.GONE);
	    	 person.setVisibility(View.GONE);
	    }
	    else {
	    	 lucha.setVisibility(View.GONE); 
	    	 evil.setVisibility(View.VISIBLE);
	    	 person.setVisibility(View.VISIBLE);
	    }
    }
        
    private void iniciaAnimacion() {
    	
	    person.setBackgroundResource(R.drawable.animacion);
	    personAnim = (AnimationDrawable) person.getBackground();     
	    person.post(new Runnable(){    
	        public void run(){    
	        	personAnim.start();        
	    }
	    });
	    
	    evil.setBackgroundResource(R.drawable.animacion_evil);
	    evilAnim = (AnimationDrawable) evil.getBackground();     
	    evil.post(new Runnable(){    
	        public void run(){    
	        	evilAnim.start();        
	    }
	    });
	    
	    lucha.setBackgroundResource(R.drawable.animacion_lucha);
	    luchaAnim = (AnimationDrawable) lucha.getBackground();     
	    lucha.post(new Runnable(){    
	        public void run(){    
	        	luchaAnim.start();        
	    }
	    });
    }
    
    private void mueveIzquierda(){
    	
    	Log.d("XXX",celdaActual);
    	
    	if (celdaActual.charAt(1) == '1') { // puede izq	
    		    		
    		Log.d("XXX","PUEDE IZQ");
    		
	 		    //Peticiones.peticionMovimientoJugador(_UDID, _ID_JUGADOR, 0, this);
    		    MOVIMIENTO = 0;
    			Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, this);
	 		    deshabilitaBotones();
	 	       // comprueba solcion
	 	      // compruebaSolucion();
    	}
    }
    
    private void mueveDerecha(){
    	
     	Log.d("XXX",celdaActual);

    	if (celdaActual.charAt(0) == '1') { // puede der
    		
    		Log.d("XXX","PUEDE DER");
    		 		
 		    //Peticiones.peticionMovimientoJugador(_UDID, _ID_JUGADOR, 1, this);
    		MOVIMIENTO = 1;
    		Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, this);
 		    deshabilitaBotones();
	 	       // comprueba solcion
	 	      // compruebaSolucion();
    	}
    }
    
    private void mueveArriba(){
    	
    	Log.d("XXX",celdaActual);

    	if (celdaActual.charAt(2) == '1') { // puede top
    	    		
    		Log.d("XXX","PUEDE TOP");
    		
 		    //Peticiones.peticionMovimientoJugador(_UDID, _ID_JUGADOR, 2, this);
    		MOVIMIENTO = 2;
    		Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, this);
 		   
 		   
 		    deshabilitaBotones();
	 	       // comprueba solcion
	 	      // compruebaSolucion();
    	}
    }
    
    private void mueveAbajo(){
    	
    	Log.d("XXX",celdaActual);
    	
    	if (celdaActual.charAt(3) == '1') { // puede bot
    		
    		Log.d("XXX","PUEDE BOT");
    		
 		    //Peticiones.peticionMovimientoJugador(_UDID, _ID_JUGADOR, 3, this);
    		MOVIMIENTO = 3;
    		Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, this);	   
 		   
 		    
 		    deshabilitaBotones();
	 	       // comprueba solcion
	 	      // compruebaSolucion();
    	}
    }
        
    
    private void deshabilitaBotones() {
    	
    	bMenu.setEnabled(false);
    	
    	bIzq.setEnabled(false);
    	bDer.setEnabled(false);
    	bTop.setEnabled(false);
    	bBot.setEnabled(false);
    }
    
    private void habilitaBotones() {
    	    
    	bMenu.setEnabled(true);
    	
    	bIzq.setEnabled(true);
    	bDer.setEnabled(true);
    	bTop.setEnabled(true);
    	bBot.setEnabled(true);
    }
    
    // Metodos pantalla
    
    private void cargaPantalla() {
    	
    	fondo = (View) findViewById(R.id.fondo);
    	fondo.getBackground().setAlpha(200);
    	fondo.setVisibility(View.GONE);

    	tcuenta = (TextView) findViewById(R.id.tcuenta);
    	tpuntos = (TextView) findViewById(R.id.tpuntos);
    	tnivel = (TextView) findViewById(R.id.tnivel);
    	tmejor = (TextView) findViewById(R.id.tmejor);
    	ttpuntos = (TextView) findViewById(R.id.ttpuntos);
    	ttnivel = (TextView) findViewById(R.id.ttnivel);
    	ttmejor = (TextView) findViewById(R.id.ttmejor);
    	
    	scroll = (ScrollView) findViewById(R.id.scroll);
    	hscroll = (HorizontalScrollView) findViewById(R.id.hscroll);
    	
    	grande = (ImageView) findViewById(R.id.grande);
    	person = (ImageView) findViewById(R.id.person);
    	galleta = (ImageView) findViewById(R.id.galleta);
    	barra = (ImageView) findViewById(R.id.barra);    	
    	gall0 = (ImageView) findViewById(R.id.gall0);
    	gall1 = (ImageView) findViewById(R.id.gall1);
    	gall2 = (ImageView) findViewById(R.id.gall2);
    	gall3 = (ImageView) findViewById(R.id.gall3);
    	evil = (ImageView) findViewById(R.id.evil);
    	lucha = (ImageView) findViewById(R.id.lucha);
	    lucha.setVisibility(View.GONE);
    	
    	bIzq = (Button) findViewById(R.id.bIzq);
    	bIzq.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bIzq.setBackgroundResource(R.drawable.boton_press);

				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bIzq.setBackgroundResource(R.drawable.trans);
					
					mueveIzquierda();
				}
				return false;
			}
		});
    	bDer = (Button) findViewById(R.id.bDer);
    	bDer.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bDer.setBackgroundResource(R.drawable.boton_press);

				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bDer.setBackgroundResource(R.drawable.trans);
					
					mueveDerecha();
				}
				return false;
			}
		});
    	bTop = (Button) findViewById(R.id.bTop);
    	bTop.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bTop.setBackgroundResource(R.drawable.boton_press);

				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bTop.setBackgroundResource(R.drawable.trans);
					
					mueveArriba();
				}
				return false;
			}
		});
    	bBot = (Button) findViewById(R.id.bBot);
    	bBot.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bBot.setBackgroundResource(R.drawable.boton_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bBot.setBackgroundResource(R.drawable.trans);
					
					mueveAbajo();
				}
				return false;
			}
		});
    
    	bMenu = (Button) findViewById(R.id.bMenu);
    	/*
    	bMenu.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bMenu.setBackgroundResource(R.drawable.boton_salir_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bMenu.setBackgroundResource(R.drawable.boton_salir);
					
					menuActual = 1;
					cargaMenu();
				}
				return false;
			}
		});*/
    	
    	bOtra = (Button) findViewById(R.id.bOtra);
    	/*
    	bOtra.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bOtra.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bOtra.setBackgroundResource(R.drawable.vacio);
					
					if (menuActual == 2)
						nuevaPartida();
					else ocultaMenu();
					
					menuActual = 0;
				}
				return false;
			}
		});*/
    	
    	bSalir = (Button) findViewById(R.id.bSalir);
    	/*
    	bSalir.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bSalir.setBackgroundResource(R.drawable.vacio_press);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bSalir.setBackgroundResource(R.drawable.vacio);
					
					finish();
				}
				return false;
			}
		});
		*/
    }
    
    private void ajustaPantalla() {
    	
        int TAM_BOTONES = myWidth / 8;	
     	
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myWidth / 3, (TAM_BASE*N) / 2);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = (myWidth/8);
        bTop.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(myWidth / 3, ((TAM_BASE*N) / 3) * 2);
        params.topMargin = (myWidth/8) + ((TAM_BASE*N) / 6);
        bIzq.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(myWidth / 3, ((TAM_BASE*N) / 3) * 2);
        params.topMargin = (myWidth/8) + ((TAM_BASE*N) / 6);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bDer.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(myWidth / 3, (TAM_BASE*N) / 2);
        params.addRule(RelativeLayout.BELOW,bTop.getId());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bBot.setLayoutParams(params);
        				 			       
 	   params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	   params.topMargin = (myWidth/8);
       person.setLayoutParams(params);
       person.bringToFront();
        
  	   params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
  	   params.topMargin = (myWidth/8);
       evil.setLayoutParams(params);
       evil.bringToFront();
        
 	   params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	   params.topMargin = (myWidth/8) + ((TAM_BASE) * (N - 1));
 	   params.leftMargin = ((TAM_BASE) * (N - 1));
 	   galleta.setLayoutParams(params);
 	   galleta.bringToFront();
        
 	   params = new RelativeLayout.LayoutParams(myWidth, myWidth/8);
        barra.setLayoutParams(params);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
        bMenu.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(tcuenta.getLayoutParams());
 	   params.leftMargin = (myWidth/8)*2;
        tcuenta.setLayoutParams(params);
        
        params = new RelativeLayout.LayoutParams(tnivel.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,grande.getId());
        tnivel.setLayoutParams(params);
        tnivel.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(ttnivel.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,tnivel.getId());
 	   ttnivel.setLayoutParams(params);
 	   ttnivel.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(tpuntos.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,ttnivel.getId());
 	   tpuntos.setLayoutParams(params);
        tpuntos.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(ttpuntos.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,tpuntos.getId());
 	   ttpuntos.setLayoutParams(params);
        ttpuntos.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(tmejor.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,ttpuntos.getId());
 	   tmejor.setLayoutParams(params);
 	   tmejor.setVisibility(View.GONE);
 	   
        params = new RelativeLayout.LayoutParams(ttmejor.getLayoutParams());
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.addRule(RelativeLayout.BELOW,tmejor.getId());
 	   ttmejor.setLayoutParams(params);
 	   ttmejor.setVisibility(View.GONE);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
 	   params.leftMargin = myWidth/2;
 	   gall0.setLayoutParams(params);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
 	   params.addRule(RelativeLayout.RIGHT_OF,gall0.getId());
 	   gall1.setLayoutParams(params);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
 	   params.addRule(RelativeLayout.RIGHT_OF,gall1.getId());
 	   gall2.setLayoutParams(params);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
 	   params.addRule(RelativeLayout.RIGHT_OF,gall2.getId());
 	   gall3.setLayoutParams(params);
        
 	   params = new RelativeLayout.LayoutParams(myWidth/2,myHeight/4);
 	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
 	   params.topMargin = myHeight / 40;
        grande.setLayoutParams(params);
        grande.bringToFront();
        grande.setVisibility(View.GONE);
        
        
        params = new RelativeLayout.LayoutParams(myWidth/3,myHeight/8);
  	   params.addRule(RelativeLayout.BELOW,ttmejor.getId());
 	   params.topMargin = myHeight / 40;
 	   params.leftMargin = myWidth/6;
  	   bSalir.setLayoutParams(params);
  	   
        params = new RelativeLayout.LayoutParams(myWidth/3,myHeight/8);
  	   params.addRule(RelativeLayout.BELOW,ttmejor.getId());
 	   params.topMargin = myHeight / 40;
  	   params.addRule(RelativeLayout.RIGHT_OF,bSalir.getId());
  	   bOtra.setLayoutParams(params);
  	   
  	   bSalir.setVisibility(View.GONE);
    	   bOtra.setVisibility(View.GONE);
     }
    
    // Metodos respuestas
    
    public void respuestaConectarSalon(dtoConectarSalon values) {
    	
    	myProgressDialog.dismiss();
    	
    	if (values != null) {
    		Log.d("XXX","exito " + values.exito);
    		Log.d("XXX","idJugador " + values.idJugador);
    		
    		_ID_JUGADOR = Integer.valueOf(values.idJugador);
    		
	 	    myProgressDialog = ProgressDialog.show(V_Combate.this, "LOCO", "Buscando rival..", true); 
	 	    myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
	 	   
	 	    Peticiones.peticionConectarMesa(_UDID, _ID_JUGADOR, this);
    	}
    	else {
    		Log.d("XXX","ERRORRR");
    	}
    }
    
    public void respuestaConectarMesa(dtoConectarMesa values) {
    	
    	myProgressDialog.dismiss();
    	
    	if (values != null) {
    		Log.d("XXX","conectado " + values.conectado);
    		Log.d("XXX","exito " + values.exito);
    		Log.d("XXX","idMesa " + values.idMesa);
    		
    		_ID_MESA = Integer.valueOf(values.idMesa);
    		
    	 	myProgressDialog = ProgressDialog.show(V_Combate.this, "LOCO", "Esperando rival..", true); 
    	 	myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
    	 	
    	 	Peticiones.peticionEsperaJugar(_UDID, _ID_JUGADOR, this);
    	}
    	else Log.d("XXX","ERRORRR");
    }
    
    public void respuestaEsperaJugar(dtoEsperaJugar values) {   	
    	
    	if (values != null) {
    		Log.d("XXX","conectado " + values.conectado);
    		Log.d("XXX","listos " + values.listos);
    		Log.d("XXX","idSitio " + values.idSitio);
    		Log.d("XXX","codigos " + values.codigos);
    		Log.d("XXX","posiciones " + values.posiciones);
    		
    		if (values.listos.equals("2")) {
    			
    			myProgressDialog.dismiss();
    			
    			_ID_POSICION = Integer.valueOf(values.idSitio);
    			_CODIGOS = values.codigos;
    			_POSICIONES = values.posiciones;
    			
    			generaLaberinto();
    		}
    		else { // bucle peticion
	    			
	   			 new Handler().postDelayed(new Runnable() {
					  public void run() {
						  Peticiones.peticionEsperaJugar(_UDID, _ID_JUGADOR, V_Combate.this);
				 }}, 1000);	
    		}
    	}
    	else {
    		Log.d("XXX","ERRORRR");
    		myProgressDialog.dismiss();
    	}
    }
    
    
    
    
    
    public void respuestaMovimientoJugador(dtoMovimientoJugador values) {
    	
    	if (values != null) {
    		Log.d("XXX","conectado " + values.conectado);
    		Log.d("XXX","exito " + values.exito);
    		Log.d("XXX","posiciones " + values.posiciones);
    		
    		_POSICIONES = values.posiciones;
    		
        	posicionActual = Integer.valueOf(Cadenas.objetoEnCadena(_ID_POSICION, 2, _POSICIONES));
        	posicionRivalActual = Integer.valueOf(Cadenas.objetoEnCadena((_ID_POSICION + 1)%2, 2, _POSICIONES));
        	actualizaPosiciones();
        	celdaActual = codigos.get(posicionActual);  
        	        	
  			 new Handler().postDelayed(new Runnable() {
				  public void run() {
					habilitaBotones();
			        Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, V_Combate.this);
			 }}, 1000);	
    	}
    	else Log.d("XXX","ERRORRR");
    }
    
    public void respuestaSituacionPartida(dtoSituacionPartida values) {
    	
    	if (values != null) {
    		Log.d("XXX","conectado " + values.conectado);
    		Log.d("XXX","exito " + values.exito);
    		Log.d("XXX","posiciones " + values.posiciones);
    		
    		_POSICIONES = values.posiciones;
    		
        	posicionActual = Integer.valueOf(Cadenas.objetoEnCadena(_ID_POSICION, 2, _POSICIONES));
        	MOVIMIENTO = -1;
        	posicionRivalActual = Integer.valueOf(Cadenas.objetoEnCadena((_ID_POSICION + 1)%2, 2, _POSICIONES));
        	actualizaPosiciones();
        	celdaActual = codigos.get(posicionActual);  
        	
 			 new Handler().postDelayed(new Runnable() {
				  public void run() {
			        Peticiones.peticionSituacionPartida(_UDID, _ID_JUGADOR, MOVIMIENTO, V_Combate.this);
			 }}, 1000);	
    	}
    	else Log.d("XXX","ERRORRR");
		    
    	habilitaBotones();
    }
}
