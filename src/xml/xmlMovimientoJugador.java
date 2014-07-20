package xml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import dto.dtoMovimientoJugador;

public class xmlMovimientoJugador {
	
	public String conectado = null;
	public String exito = null;
	public String posiciones = null;

    public dtoMovimientoJugador lanza(String xml) throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xml ) );
        int eventType = xpp.getEventType();
    	
        dtoMovimientoJugador values = new dtoMovimientoJugador();        
        
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	if(eventType == XmlPullParser.START_TAG) {
        		        	
        		if (xpp.getName().equals("conectado")) {
        			eventType = xpp.next();
        			conectado = xpp.getText();
        			values.conectado = conectado;
        		}
        		else if (xpp.getName().equals("exito")) {
        			eventType = xpp.next();
        			exito = xpp.getText();
        			values.exito = exito;
        		}
        		else if (xpp.getName().equals("posiciones")) {
        			eventType = xpp.next();
        			posiciones = xpp.getText();
        			values.posiciones = posiciones;
        		}
             }         
         
         eventType = xpp.next();
        }
        return values;
    }
}
