package xml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import dto.dtoConectarSalon;

public class xmlConectarSalon {
	
	public String exito = null;
	public String idJugador = null;

    public dtoConectarSalon lanza(String xml) throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xml ) );
        int eventType = xpp.getEventType();
    	
        dtoConectarSalon values = new dtoConectarSalon();        
        
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	if(eventType == XmlPullParser.START_TAG) {
        		        	
        		if (xpp.getName().equals("exito")) {
        			eventType = xpp.next();
        			exito = xpp.getText();
        			values.exito = exito;
        		}
        		else if (xpp.getName().equals("idJugador")) {
        			eventType = xpp.next();
        			idJugador = xpp.getText();
        			values.idJugador = idJugador;
        		}
             }         
         
         eventType = xpp.next();
        }
        return values;
    }
}
