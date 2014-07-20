package xml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import dto.dtoEsperaJugar;

public class xmlEsperaJugar {
	
	public String conectado = null;
	public String listos = null;
	public String idSitio = null;
	public String codigos = null;
	public String posiciones = null;

    public dtoEsperaJugar lanza(String xml) throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xml ) );
        int eventType = xpp.getEventType();
    	
        dtoEsperaJugar values = new dtoEsperaJugar();        
        
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	if(eventType == XmlPullParser.START_TAG) {
        		        	
        		if (xpp.getName().equals("conectado")) {
        			eventType = xpp.next();
        			conectado = xpp.getText();
        			values.conectado = conectado;
        		}
        		else if (xpp.getName().equals("listos")) {
        			eventType = xpp.next();
        			listos = xpp.getText();
        			values.listos = listos;
        		}
        		else if (xpp.getName().equals("idSitio")) {
        			eventType = xpp.next();
        			idSitio = xpp.getText();
        			values.idSitio = idSitio;
        		}
        		else if (xpp.getName().equals("codigos")) {
        			eventType = xpp.next();
        			codigos = xpp.getText();
        			values.codigos = codigos;
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
