package ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import log.Laberintos;
import txl.loco.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class V_Principal extends Activity {
		
	private int myWidth;
	private int myHeight;
	 
	 private static int N = 6;
	 private static int numNodos = N*N;	 	 
	 private static int TAM_BASE;
	 
	 private int numGalletas;
	 
	 private int[][] coord; // datos laberinto en curso
	 private List<String> codigos; // datos laberinto en curso 
	 
	 private int[][] coordNext; // datos laberinto futuro
	 private List<String> codigosNext; // datos laberinto futuro
	
	private RelativeLayout rel;
	
	private String celdaActual, nombrePartida;
	private int posicionActual, segundos, menuActual;
	private boolean listoNext, silencio;
	private int[] posicionesSol;
	
	private ImageView person, barra, barra2, galleta0, galleta1, galleta2, galleta3, galleta4, grande, gall0, gall1, gall2, gall3, gall4, top, der, izq, bot, mini;
	private TextView tcuenta, ttiempo, tttiempo, tdificultad, ttdificultad, tmejor, ttmejor, tnombre;
	
	private Button bIzq, bDer, bTop, bBot, bOtra, bSalir, bMenu, bSilenciar;
	private View fondo;
	
	private MediaPlayer mpRonq, mpPaso, mpExito;
	
	private Timer timer;
	
	private AnimationDrawable personAnim;
	
	private ProgressDialog myProgressDialog = null; 
	
	private Laberintos laberintos;
	
	private AsyncTask<String, Void, String> cargaInicial, cargaBack;
	
	private SharedPreferences preferences;
		
			
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                       WindowManager.LayoutParams.FLAG_FULLSCREEN); 
              
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setContentView(R.layout.v_principal);  
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        myHeight = metrics.heightPixels;
        myWidth = metrics.widthPixels;
          
        preferences = getSharedPreferences("loco",MODE_PRIVATE); 
        
        N = getIntent().getIntExtra("N", 8);
        nombrePartida = preferences.getString("ultimoNombre", "JUG");
        
   	    numNodos = N*N;
        
        TAM_BASE = myWidth / N;
    	
        cargaInicial = new CargaInicial().execute("");

       menuActual = 0;
       silencio = false;
       
       cargaPantalla();
       ajustaPantalla();
       galleta0.setVisibility(View.GONE);
       galleta1.setVisibility(View.GONE);
       galleta2.setVisibility(View.GONE);
       galleta3.setVisibility(View.GONE);
       galleta4.setVisibility(View.GONE);
       
       // carga mp3
       mpExito = MediaPlayer.create(this, R.raw.exito);
       mpPaso = MediaPlayer.create(this, R.raw.paso);
       mpRonq = MediaPlayer.create(this, R.raw.ronq);
       
	   myProgressDialog = ProgressDialog.show(V_Principal.this, "LoCo", getString(R.string.mIniciando), true); 
	   myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
	   
	   //enviaTiempos();
    }      
    
	 @Override
	 public boolean onKeyUp(int keyCode, KeyEvent event) {

		 	if (keyCode == KeyEvent.KEYCODE_BACK) {
		 		
		 		if (menuActual == 0) {
			 		menuActual = 1;
			 		cargaMenu();
		 		}
		    }
		    return true;
	}
    
    //
    // Metodos iniciar datos
    //
    
    // inicia animacion botones y person
    private void iniciaAnimacion() {
    	
	    person.setBackgroundResource(R.drawable.animacion);
	    personAnim = (AnimationDrawable) person.getBackground();     
	    person.post(new Runnable(){    
	        public void run(){    
	        		personAnim.start();        
	    }
	    });
	    
	    top.setBackgroundResource(R.drawable.animacion_top);
	    final AnimationDrawable topAnim = (AnimationDrawable) top.getBackground();     
	    top.post(new Runnable(){    
	        public void run(){    
	        topAnim.start();        
	    }
	    });
	    
	    der.setBackgroundResource(R.drawable.animacion_der);
	    final AnimationDrawable derAnim = (AnimationDrawable) der.getBackground();     
	    der.post(new Runnable(){    
	        public void run(){    
	        	derAnim.start();        
	    }
	    });
	    
	    bot.setBackgroundResource(R.drawable.animacion_bot);
	    final AnimationDrawable botAnim = (AnimationDrawable) bot.getBackground();     
	    bot.post(new Runnable(){    
	        public void run(){    
	        	botAnim.start();        
	    }
	    });
	    
	    izq.setBackgroundResource(R.drawable.animacion_izq);
	    final AnimationDrawable izqAnim = (AnimationDrawable) izq.getBackground();     
	    izq.post(new Runnable(){    
	        public void run(){    
	        	izqAnim.start();        
	    }
	    });
    }
            
    // inicia cronometro
    private void iniciaTimer() {
    	    
    	if (timer != null)
    		timer.cancel();
    	
    	timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {        
    		int count = 0;
    		
    	        @Override
    	        public void run() {
    	            runOnUiThread(new Runnable() {
    	                public void run() {
    	                	
    	                	if (count%60 < 10) {
    	                		if ((count/60) < 10) {
    	                			tcuenta.setText("0"+(count/60)+":0"+count%60);
    	                			tttiempo.setText("" + "0"+(count/60)+":0"+count%60);
    	                		}
    	                		else {
    	                			tcuenta.setText((count/60)+":0"+count%60);
    	                			tttiempo.setText("" + (count/60)+":0"+count%60);
    	                		}
    	                	}
    	                	else {
    	                		if ((count/60) < 10) {
    	                			tcuenta.setText("0"+(count/60)+":"+count%60);
    	                			tttiempo.setText("" + "0"+(count/60)+":"+count%60);
    	                		}
    	                		else {
    	                			tcuenta.setText((count/60)+":"+count%60);
    	                			tttiempo.setText("" + (count/60)+":"+count%60);
    	                		}
    	                	}
    	                	
    	                    count++;                
    	                }
    	            });
    	        }
    	}, 1000, 1000);
    }
    
    // inicia posiciones personaje y galletas
    private void iniciaPosiciones() {
    	
    	posicionActual = 0;
    	celdaActual = codigos.get(posicionActual);  
    	    	
    	posicionesSol = new int[5];
    	posicionesSol = generaPosicionesSolucion();    	
    	
    	ajustaGalletasPantalla();
    	
    	numGalletas = 0;
    }
    
    // genera posiciones iniciales galletas
    private int[] generaPosicionesSolucion() {
    	
    	int[] res = new int[5];
    	
    		res[0] = numNodos - 1;
    		res[1] = N - 1; // o cero
    		res[2] = numNodos - N;
    		res[3] = (N + 1) * (N / 2);
    		res[4] = N + 2;
    	
    	return res;
    }
    
    // nueva partida reinicia y pinta
    private void nuevaPartida() {
    	
    	//fondo.setVisibility(View.GONE);
    	habilitaBotones();
    	bIzq.setVisibility(View.VISIBLE);
    	bDer.setVisibility(View.VISIBLE);
    	bTop.setVisibility(View.VISIBLE);
    	bBot.setVisibility(View.VISIBLE);
    	
    	Drawable image = getResources().getDrawable(R.drawable.galleta_press);
		gall0.setImageDrawable(image);
		gall1.setImageDrawable(image);
		gall2.setImageDrawable(image);
		gall3.setImageDrawable(image);
		gall4.setImageDrawable(image);
    	
		if (listoNext) {
		      
			codigos = codigosNext;
			coord = coordNext;		

	       galleta0.setVisibility(View.GONE);
	       galleta1.setVisibility(View.GONE);
	       galleta2.setVisibility(View.GONE);
	       galleta3.setVisibility(View.GONE);
	       galleta4.setVisibility(View.GONE);
		   	    
		   myProgressDialog = ProgressDialog.show(V_Principal.this, "LoCo", getString(R.string.mCargando), true); 
		   myProgressDialog.setIcon(getResources().getDrawable(R.drawable.icono_small));
		   
			 new Handler().postDelayed(new Runnable() {
				 
				 public void run() {

			            // carga laberinto
			    	   	cargaLaberinto(codigos);
			    	   	
			    	   	fondo.setVisibility(View.GONE);
			    	   	
			    	   	ajustaPantalla();
			 	        galleta0.setVisibility(View.VISIBLE);
				        galleta1.setVisibility(View.VISIBLE);
				        galleta2.setVisibility(View.VISIBLE);
				        galleta3.setVisibility(View.VISIBLE);
				        galleta4.setVisibility(View.VISIBLE);
			    	   	
			    	   	iniciaPosiciones();
			    	   	
			    	   	iniciaTimer();
			    	   	
						   person.setBackgroundResource(R.drawable.animacion);
						   personAnim = (AnimationDrawable) person.getBackground();     
						   person.post(new Runnable(){    
						       public void run(){    
						        		personAnim.start();        
						   }
						   });
			    	   	//iniciaAnimacion();
			    	   	
			    	   	myProgressDialog.dismiss();
			    	   	
			    	   	// ve cargando el proximo
			    	   	listoNext = false;
			    	   	cargaBack = new CargaBackground().execute("");
			        	}
			        }
			 , 1000);	   
		}
    }
    
    //
    // Metodos pinta pantalla
    //
    
    // pinta el laberinto en la pantalla
    private void cargaLaberinto(List<String> cods) {
    	
    	rel = (RelativeLayout) findViewById(R.id.relativeLayout1);
    	int col = 0, fila = 0;
    	for (int i = 0; i < numNodos; i++) {
    	    		
    		ImageView imageView = new ImageView(this);  
    		Drawable image = getResources().getDrawable(getResources().getIdentifier("c" + cods.get(i), "drawable", getPackageName()));
    		imageView.setImageDrawable(image);

	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
	        params.topMargin = ((myWidth/8) * 2);   
	        if (col != 0) params.topMargin += (TAM_BASE) * col;      
	        if (fila != 0) params.leftMargin = (TAM_BASE) * fila;

	        
    		fila = (fila + 1) % N;
    		
    		if (fila % N == 0) 
    			col = (col + 1) % N;
    		
    		rel.addView(imageView,params);
    	}
    } 
    
    private void cargaPantalla() {
    	
    	fondo = (View) findViewById(R.id.fondo);
    	fondo.getBackground().setAlpha(200);
    	fondo.setVisibility(View.GONE);

    	tnombre = (TextView) findViewById(R.id.tnombre);
    	tnombre.setText(nombrePartida);
    	tcuenta = (TextView) findViewById(R.id.tcuenta);
    	ttiempo = (TextView) findViewById(R.id.ttiempo);
    	tdificultad = (TextView) findViewById(R.id.tdificultad);
    	tmejor = (TextView) findViewById(R.id.tmejor);
    	tttiempo = (TextView) findViewById(R.id.tttiempo);
    	ttdificultad = (TextView) findViewById(R.id.ttdificultad);
    	ttmejor = (TextView) findViewById(R.id.ttmejor);
    	
    	mini = (ImageView) findViewById(R.id.mini); 
    	izq = (ImageView) findViewById(R.id.izq);
    	bot = (ImageView) findViewById(R.id.bot);
    	top = (ImageView) findViewById(R.id.top);
    	der = (ImageView) findViewById(R.id.der);
    	grande = (ImageView) findViewById(R.id.grande);
    	person = (ImageView) findViewById(R.id.person);
    	galleta0 = (ImageView) findViewById(R.id.galleta0);
    	galleta1 = (ImageView) findViewById(R.id.galleta1);
    	galleta2 = (ImageView) findViewById(R.id.galleta2);
    	galleta3 = (ImageView) findViewById(R.id.galleta3);
    	galleta4 = (ImageView) findViewById(R.id.galleta4);
    	barra = (ImageView) findViewById(R.id.barra);    	
    	barra2 = (ImageView) findViewById(R.id.barra2);    	
    	gall0 = (ImageView) findViewById(R.id.gall0);
    	gall1 = (ImageView) findViewById(R.id.gall1);
    	gall2 = (ImageView) findViewById(R.id.gall2);
    	gall3 = (ImageView) findViewById(R.id.gall3);
    	gall4 = (ImageView) findViewById(R.id.gall4);
    	
    	bIzq = (Button) findViewById(R.id.bIzq);
    	bIzq.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bIzq.setBackgroundResource(R.drawable.boton_press);
					if (!silencio)
						mpPaso.start();
					
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
					if (!silencio) 
						mpPaso.start();
					
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
					if (!silencio)
						mpPaso.start();
					
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
					if (!silencio) 
						mpPaso.start();
					
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bBot.setBackgroundResource(R.drawable.trans);
					
					mueveAbajo();
				}
				return false;
			}
		});
    
    	bMenu = (Button) findViewById(R.id.bMenu);
    	bMenu.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bMenu.setBackgroundResource(R.drawable.salir_press);
					if (!silencio) 
						mpPaso.start();
					
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bMenu.setBackgroundResource(R.drawable.salir);
					
					menuActual = 1;
					cargaMenu();
				}
				return false;
			}
		});
    	
    	bSilenciar = (Button) findViewById(R.id.bSilencio);
    	bSilenciar.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (silencio) 
						bSilenciar.setBackgroundResource(R.drawable.silenciar_press);						
					else {
						bSilenciar.setBackgroundResource(R.drawable.mute_press);	
						if (!silencio) 
							mpPaso.start();
						
					}
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bSilenciar.setBackgroundResource(R.drawable.silenciar);

					if (silencio) {
						
						silencio = false;
						bSilenciar.setBackgroundResource(R.drawable.silenciar);
					}
					else {
						silencio = true;
						bSilenciar.setBackgroundResource(R.drawable.mute);
					}
					
					//menuActual = 1;
					//cargaMenu();
				}
				return false;
			}
		});
    	
    	bOtra = (Button) findViewById(R.id.bOtra);
    	bOtra.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bOtra.setBackgroundResource(R.drawable.vacio_press);
					if (!silencio) 
						mpPaso.start();
					
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bOtra.setBackgroundResource(R.drawable.vacio);
					
					if (menuActual == 2) {
						//nuevaPartida();
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
					else ocultaMenu();
					
					menuActual = 0;
				}
				return false;
			}
		});
    	
    	bSalir = (Button) findViewById(R.id.bSalir);
    	bSalir.setOnTouchListener(new OnTouchListener() {	
			
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					bSalir.setBackgroundResource(R.drawable.vacio_press);
					if (!silencio) 
						mpPaso.start();
					
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)  {

					bSalir.setBackgroundResource(R.drawable.vacio);
					
					finish();
				}
				return false;
			}
		});
    }
    
    // carga el menu en pantalla
    private void cargaMenu() {
    	    	
    	fondo.setVisibility(View.VISIBLE);
    	fondo.bringToFront();
    	
    	bSalir.setVisibility(View.VISIBLE);
    	bSalir.bringToFront();
    	
    	bOtra.setVisibility(View.VISIBLE);
    	if (menuActual == 2) bOtra.setText(R.string.otra);
    	else bOtra.setText(R.string.seguir);
    	bOtra.bringToFront();
    	
    	grande.setVisibility(View.VISIBLE);
    	grande.bringToFront();
    	
    	tdificultad.setVisibility(View.VISIBLE);
    	tdificultad.bringToFront();
    	ttdificultad.setVisibility(View.VISIBLE);
    	ttdificultad.bringToFront();
    	
    	tdificultad.setText(R.string.dificultad);
    	
    	if (N == 8) ttdificultad.setText(R.string.facil);
    	else if (N == 12) ttdificultad.setText(R.string.normal);
    	else if (N == 14) ttdificultad.setText(R.string.dificil);
    	else if (N == 15) ttdificultad.setText(R.string.insano);
    	
    	ttiempo.setVisibility(View.VISIBLE);
    	ttiempo.bringToFront();
    	tttiempo.setVisibility(View.VISIBLE);
    	tttiempo.bringToFront();

    	ttiempo.setText(R.string.tiempo);
    	tttiempo.setText(tcuenta.getText().toString());
    	
    	tmejor.setVisibility(View.VISIBLE);
    	tmejor.bringToFront();
    	ttmejor.setVisibility(View.VISIBLE);
    	ttmejor.bringToFront();
    	
    	tmejor.setText(R.string.mejorTiempo);
    	
    	String claveTiempo;
    	
    	if (N == 8) claveTiempo = "tiempoFacil";
    	else if (N == 12) claveTiempo = "tiempoNormal";
    	else if (N == 14) claveTiempo = "tiempoDificil";
    	else claveTiempo = "tiempoInsano";
    	
    	ttmejor.setText(preferences.getString(claveTiempo, "-"));

    	deshabilitaBotones();
    	
    	bIzq.setVisibility(View.GONE);
    	bDer.setVisibility(View.GONE);
    	bTop.setVisibility(View.GONE);
    	bBot.setVisibility(View.GONE);
    }
    
    private void ocultaMenu() {
    	
    	fondo.setVisibility(View.GONE);
    	bSalir.setVisibility(View.GONE);
    	bOtra.setVisibility(View.GONE);
    	grande.setVisibility(View.GONE);
    	tdificultad.setVisibility(View.GONE);
    	ttdificultad.setVisibility(View.GONE);
    	ttiempo.setVisibility(View.GONE);
    	tttiempo.setVisibility(View.GONE);
    	tmejor.setVisibility(View.GONE);
    	ttmejor.setVisibility(View.GONE);
    	
    	habilitaBotones();
    	
    	bIzq.setVisibility(View.VISIBLE);
    	bDer.setVisibility(View.VISIBLE);
    	bTop.setVisibility(View.VISIBLE);
    	bBot.setVisibility(View.VISIBLE);
    }
    
    private void ajustaPantalla() {
    	
       int TAM_BOTONES = myWidth / 8;	
    	
       RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myWidth / 3, (TAM_BASE*N) / 2);
       params.addRule(RelativeLayout.CENTER_HORIZONTAL);
       params.topMargin = ((myWidth/8) * 2);
       bTop.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(myWidth / 4, myHeight/15);
       params.addRule(RelativeLayout.CENTER_HORIZONTAL);
       params.topMargin = ((myWidth/8) * 2);
       top.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(myWidth / 3, ((TAM_BASE*N) / 3) * 2);
       params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE*N) / 6);
       bIzq.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(myHeight/15, myWidth / 4);
       params.topMargin = ((myWidth/8) * 2) + (((TAM_BASE*N) / 2) - (myWidth/8));
       izq.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(myWidth / 3, ((TAM_BASE*N) / 3) * 2);
       params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE*N) / 6);
       params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
       bDer.setLayoutParams(params);       
       
       params = new RelativeLayout.LayoutParams(myHeight/15, myWidth / 4);
       params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
       params.topMargin = ((myWidth/8) * 2) + (((TAM_BASE*N) / 2) - (myWidth/8));
       der.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(myWidth / 3, (TAM_BASE*N) / 2);
       params.addRule(RelativeLayout.BELOW,bTop.getId());
       params.addRule(RelativeLayout.CENTER_HORIZONTAL);
       bBot.setLayoutParams(params);
       		
       params = new RelativeLayout.LayoutParams(myWidth / 4, myHeight/15);
       params.addRule(RelativeLayout.CENTER_HORIZONTAL);
       params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE*N) - (myHeight/15));
       bot.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
	   params.topMargin = ((myWidth/8) * 2);
       person.setLayoutParams(params);
       person.bringToFront();
       
	   params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
	  // params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE) * (N - 1));
	 //  params.leftMargin = ((TAM_BASE) * (N - 1));
	   galleta0.setLayoutParams(params);
	   galleta0.bringToFront();
	   galleta1.setLayoutParams(params);
	   galleta1.bringToFront();
	   galleta2.setLayoutParams(params);
	   galleta2.bringToFront();
	   galleta3.setLayoutParams(params);
	   galleta3.bringToFront();
	   galleta4.setLayoutParams(params);
	   galleta4.bringToFront();
       
	   params = new RelativeLayout.LayoutParams(myWidth, myWidth/8);
       barra.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   mini.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(tnombre.getLayoutParams());
       params.addRule(RelativeLayout.RIGHT_OF,mini.getId());
       params.leftMargin = (myWidth/28);
       params.topMargin = (myWidth/50);
       tnombre.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth, myWidth/8);
	   params.addRule(RelativeLayout.BELOW,barra.getId());
       barra2.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
       bMenu.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.LEFT_OF,bMenu.getId());
	   bSilenciar.setLayoutParams(params);       
       
       params = new RelativeLayout.LayoutParams(tcuenta.getLayoutParams());
	   params.leftMargin = (myWidth/12);
	   params.topMargin = (myWidth/8);
       tcuenta.setLayoutParams(params);
       
       params = new RelativeLayout.LayoutParams(tdificultad.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,grande.getId());
	   tdificultad.setLayoutParams(params);
	   tdificultad.setVisibility(View.GONE);
       
       params = new RelativeLayout.LayoutParams(ttdificultad.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,tdificultad.getId());
	   ttdificultad.setLayoutParams(params);
	   ttdificultad.setVisibility(View.GONE);
       
       params = new RelativeLayout.LayoutParams(ttiempo.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,ttdificultad.getId());
	   ttiempo.setLayoutParams(params);
	   ttiempo.setVisibility(View.GONE);
       
       params = new RelativeLayout.LayoutParams(tttiempo.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,ttiempo.getId());
	   tttiempo.setLayoutParams(params);
       tttiempo.setVisibility(View.GONE);
       
       params = new RelativeLayout.LayoutParams(tmejor.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,tttiempo.getId());
	   tmejor.setLayoutParams(params);
	   tmejor.setVisibility(View.GONE);
	   
       params = new RelativeLayout.LayoutParams(ttmejor.getLayoutParams());
	   params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	   params.addRule(RelativeLayout.BELOW,tmejor.getId());
	   ttmejor.setLayoutParams(params);
	   ttmejor.setVisibility(View.GONE);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.leftMargin = (myWidth/2) - (myWidth/8);
	   params.topMargin = (myWidth/8);
	   gall0.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.RIGHT_OF,gall0.getId());
	   params.topMargin = (myWidth/8);
	   gall1.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.RIGHT_OF,gall1.getId());
	   params.topMargin = (myWidth/8);
	   gall2.setLayoutParams(params);
       
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.RIGHT_OF,gall2.getId());
	   params.topMargin = (myWidth/8);
	   gall3.setLayoutParams(params);
	   
	   params = new RelativeLayout.LayoutParams(myWidth/8, myWidth/8);
	   params.addRule(RelativeLayout.RIGHT_OF,gall3.getId());
	   params.topMargin = (myWidth/8);
	   gall4.setLayoutParams(params);
       
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
    
    private void ajustaGalletasPantalla() {
    	
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	    params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE) * (N - 1));
 	    params.leftMargin = ((TAM_BASE) * (N - 1));
 	    galleta0.setLayoutParams(params);
 	    galleta0.bringToFront();
 	     	    
 	    params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	    params.topMargin = ((myWidth/8) * 2);
 	    params.leftMargin = ((TAM_BASE) * (N - 1));
 	    galleta1.setLayoutParams(params);
 	    galleta1.bringToFront();
 	    
 	    params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	    params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE) * (N - 1));
 	    galleta2.setLayoutParams(params);
 	    galleta2.bringToFront();
 	    
 	    params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	    params.topMargin = ((myWidth/8) * 2) + ((TAM_BASE) * (N / 2));
 	    params.leftMargin = ((TAM_BASE) * (N / 2));
 	    galleta3.setLayoutParams(params);
 	    galleta3.bringToFront();
 	    
 	    params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
 	    params.topMargin = ((myWidth/8) * 2) + (TAM_BASE);
 	    params.leftMargin = (TAM_BASE * 2);
 	    galleta4.setLayoutParams(params);
 	    galleta4.bringToFront();
    }
    
    private void animacionExito() {
    	
		 new Handler().postDelayed(new Runnable() {
			 
			 public void run() {	
				   					
				   habilitaBotones();
				   person.setBackgroundResource(R.drawable.animacion);
				   personAnim = (AnimationDrawable) person.getBackground();     
				   person.post(new Runnable(){    
				       public void run(){    
				        		personAnim.start();        
				   }
				   });
				   //iniciaAnimacion();
		        	}
		        }
		 , 1000);
    }
    
    private void actualizaGalletas() {
    	
    	if (numGalletas == 1) {
	    	
	    	Drawable image = getResources().getDrawable(R.drawable.galleta);
	    	gall4.setImageDrawable(image);
    	}
    	else if (numGalletas == 2) {
	    	
	    	Drawable image = getResources().getDrawable(R.drawable.galleta);
	    	gall3.setImageDrawable(image);
    	}
    	else if (numGalletas == 3) {
	    	
	    	Drawable image = getResources().getDrawable(R.drawable.galleta);
	    	gall2.setImageDrawable(image);
    	}
    	else if (numGalletas == 4) {
	    	
	    	Drawable image = getResources().getDrawable(R.drawable.galleta);
	    	gall1.setImageDrawable(image);
    	}
    	else if (numGalletas == 5) {
	    	
	    	Drawable image = getResources().getDrawable(R.drawable.galleta);
	    	gall0.setImageDrawable(image);
    	}
    }
    
    private void actualizaPerson() {
    	
		   RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(TAM_BASE, TAM_BASE);
		   params.topMargin = ((myWidth/8) * 2);
		   params.topMargin += (TAM_BASE) * (posicionActual / N);
		   
		   if ((posicionActual % N) != 0) params.leftMargin = (TAM_BASE) * (posicionActual % N);
	       person.setLayoutParams(params);	       
    }
    
    private void ocultaGalleta(int pos) {
    	
    	if (pos == 0)
    		galleta0.setVisibility(View.GONE);
    	else if (pos == 1)
    		galleta1.setVisibility(View.GONE);
    	else if (pos == 2)
    		galleta2.setVisibility(View.GONE);
    	else if (pos == 3)
    		galleta3.setVisibility(View.GONE);
    	 else if (pos == 4)
        galleta4.setVisibility(View.GONE);
    }
    
    //
    // Metodos acciones partida
    //

    private void mueveIzquierda(){
    	
    	Log.d("XXX",celdaActual);
    	
    	if (celdaActual.charAt(1) == '1') { // puede izq	
    		    		
    		Log.d("XXX","PUEDE IZQ");
    		
	 		   posicionActual--;
	 		   celdaActual = codigos.get(posicionActual);  
	 		
	 		   actualizaPerson();

	 	       // comprueba solcion
	 	       compruebaSolucion();
    	}
    }
    
    private void mueveDerecha(){
    	
     	Log.d("XXX",celdaActual);

    	if (celdaActual.charAt(0) == '1') { // puede der
    		
    		Log.d("XXX","PUEDE DER");
    		 		
	 		   posicionActual++;
	 		   celdaActual = codigos.get(posicionActual);  
	 		
	 		   actualizaPerson();   
	       
	 	       // comprueba solcion
	 	       compruebaSolucion();
    	}
    }
    
    private void mueveArriba(){
    	
    	Log.d("XXX",celdaActual);

    	if (celdaActual.charAt(2) == '1') { // puede top
    	    		
    		Log.d("XXX","PUEDE TOP");
    		
	 		   posicionActual = posicionActual - N;
	 		   celdaActual = codigos.get(posicionActual);  
	 		
	 		   actualizaPerson(); 
	 	       
	 	       // comprueba solcion
	 	       compruebaSolucion();
    	}
    }
    
    private void mueveAbajo(){
    	
    	Log.d("XXX",celdaActual);
    	
    	if (celdaActual.charAt(3) == '1') { // puede bot
    		
    		Log.d("XXX","PUEDE BOT");
    		
	 		   posicionActual = posicionActual + N;
	 		   celdaActual = codigos.get(posicionActual);  
	 		
	 		   actualizaPerson();
	 	       
	 	       // comprueba solcion
	 	       compruebaSolucion();
    	}
    }
        
    private void compruebaSolucion() {
    	    	
    	if (getNumeroGalleta(posicionActual) != -1) {		
    		
    			numGalletas++;
    			
    			if (numGalletas < 5) {
    				
    				int posSolucion = getNumeroGalleta(posicionActual);
    				ocultaGalleta(posSolucion);
    				posicionesSol[posSolucion] = -1;
    				
					personAnim.stop();
					person.setBackgroundResource(R.drawable.exito);

					if (!silencio) 
						mpExito.start();
					
			    							
			    	actualizaGalletas();
			    		
			    	deshabilitaBotones();
			    	animacionExito();
    			}
    			else {
    				
    				int posSolucion = getNumeroGalleta(posicionActual);
    				ocultaGalleta(posSolucion);
    				posicionesSol[posSolucion] = -1;
    				
    				personAnim.stop();
					person.setBackgroundResource(R.drawable.sobao);
					
					if (!silencio) 
						mpRonq.start();
					
		    		
					timer.cancel();
					
					guardaTiempo();
					
		    		actualizaGalletas();
					
					deshabilitaBotones();
					
					 new Handler().postDelayed(new Runnable() {
						 
						 public void run() {

							    menuActual = 2; 
			    				cargaMenu();
					        	}
					        }
					 , 3000);
    			}
    	}
    }
        
    private int getNumeroGalleta(int celda) {
    	
    	int res = -1;
    	
    		for (int i = 0; i < 5; i++)
    			if (celda == posicionesSol[i])
    				res = i;
    	
    	return res;
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
    
    private void guardaTiempo() {
    	
    	String claveTiempo;
    	
    	if (N == 8) claveTiempo = "tiempoFacil";
    	else if (N == 12) claveTiempo = "tiempoNormal";
    	else if (N == 14) claveTiempo = "tiempoDificil";
    	else claveTiempo = "tiempoInsano";
    	    	
    	if (preferences.getString(claveTiempo, "-").equals("-")) {
    		
    		SharedPreferences.Editor editor = preferences.edit();
			editor.putString(claveTiempo, tcuenta.getText().toString());
			editor.commit();
			
			enviaTiempos();
    	}
    	else {
    		
    		int tiempo = transformaTiempo(tcuenta.getText().toString());
    		
    		if (transformaTiempo(preferences.getString(claveTiempo, "-")) > tiempo) {    // es mejor
    			
	    		SharedPreferences.Editor editor = preferences.edit();
				editor.putString(claveTiempo, tcuenta.getText().toString());
				editor.commit();
				
				enviaTiempos();
    		}
    	}
    }
    
    private void enviaTiempos() {
    	
	        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	        String fecha = sdf.format(new Date());
         
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://txl-estudios.es:8080/topPuntosLoco/puntuacionesLoco");
            
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();  

            pairs.add(new BasicNameValuePair("clave","en1LdeLM4"));  
            pairs.add(new BasicNameValuePair("servicio","4")); 
            
            pairs.add(new BasicNameValuePair("udid",getDeviceId())); 
            pairs.add(new BasicNameValuePair("nombre",preferences.getString("ultimoNombre", "JUG"))); 
            pairs.add(new BasicNameValuePair("fecha",fecha)); 
            pairs.add(new BasicNameValuePair("facil","" + transformaTiempo(preferences.getString("tiempoFacil", "99:99")))); 
            pairs.add(new BasicNameValuePair("normal","" + transformaTiempo(preferences.getString("tiempoNormal", "99:99")))); 
            pairs.add(new BasicNameValuePair("dificil","" + transformaTiempo(preferences.getString("tiempoDificil", "99:99")))); 
            pairs.add(new BasicNameValuePair("insano","" + transformaTiempo(preferences.getString("tiempoInsano", "99:99")))); 
            
            UrlEncodedFormEntity entity;
			try {
				entity = new UrlEncodedFormEntity(pairs);
	            httpPost.setEntity(entity);  
	            
	            try {
					httpClient.execute(httpPost);
				} catch (ClientProtocolException e) {}
	            catch (IOException e) {} 
			} catch (UnsupportedEncodingException e1) {}  
    }
    
    public String getDeviceId() {
        String id = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return id;
     }
    
    private int transformaTiempo(String tiempo){
    	
    	int res = 0;
    	
    	res = Integer.valueOf(tiempo.replace(":",""));
    		    	
    	return res;
    }   
    

    private class CargaInicial extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

        	laberintos = new Laberintos();
        	
        	List<List<Integer>> ady = new ArrayList<List<Integer>>();
        	coord = new int[numNodos][N];
        	
        	// inicializa matriz con pesos aleatorios
    	   	coord =	laberintos.cargaMatriz(N,ady,numNodos);
    	   	
    	   	// calcula Kruskal
    	   	ady = laberintos.Grafo(ady,numNodos);
    	   	
    	   	Log.d("XXX","ady " + ady);
    	   	
    	   	// genera códigos imagenes
    	   	codigos = laberintos.generaCodigos(ady, coord,numNodos);
        	
    	   	Log.d("XXX","CODIGOS = " + codigos);
    	   	
    	   	return "Executed";
        }      

        @Override
        protected void onPostExecute(String result) {
        	    	   	
            // carga laberinto
    	   	cargaLaberinto(codigos);
    	   	    	   	
    	   	   ajustaPantalla();
    	       galleta0.setVisibility(View.VISIBLE);
    	       galleta1.setVisibility(View.VISIBLE);
    	       galleta2.setVisibility(View.VISIBLE);
    	       galleta3.setVisibility(View.VISIBLE);
    	       galleta4.setVisibility(View.VISIBLE);
    	   	
    	   	iniciaPosiciones();
    	   	
    	   	iniciaTimer();
    	   	
    	   	iniciaAnimacion();
    	   	
    	   	myProgressDialog.dismiss();
    	   	
    	   	// ve cargando el proximo
    	   	listoNext = false;
    	   	
    	   	
    	   //	cargaBack = new CargaBackground().execute("");
    	   	
    	   //	cargaInicial.cancel(true);
        }

        @Override
        protected void onPreExecute() {
        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        	
        	
        }
    }  
    
    private class CargaBackground extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

        	laberintos = new Laberintos();
        	
        	List<List<Integer>> ady = new ArrayList<List<Integer>>();
        	coordNext = new int[numNodos][N];
        	
        	// inicializa matriz con pesos aleatorios
        	coordNext =	laberintos.cargaMatriz(N,ady,numNodos);
    	   	
    	   	// calcula Kruskal
    	   	ady = laberintos.Grafo(ady,numNodos);
    	   	
    	   	// genera códigos imagenes
    	   	codigosNext = laberintos.generaCodigos(ady, coord,numNodos);
        	
    	   	return "Executed";
        }      

        @Override
        protected void onPostExecute(String result) {
        	    	   	
            listoNext = true;
            cargaBack.cancel(true);
        }

        @Override
        protected void onPreExecute() {
        	
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        	
        	
        }
    }  
}