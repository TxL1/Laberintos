package xml;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import dto.dtoConectarMesa;

public class xmlConectarMesa {
	
	public String conectado = null;
	public String exito = null;
	public String idMesa = null;

    public dtoConectarMesa lanza(String xml) throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xml ) );
        int eventType = xpp.getEventType();
    	
        dtoConectarMesa values = new dtoConectarMesa();        
        
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	if(eventType == XmlPullParser.START_TAG) {
        		        	
        		if (xpp.getName().equals("exito")) {
        			eventType = xpp.next();
        			exito = xpp.getText();
        			values.exito = exito;
        		}
        		else if (xpp.getName().equals("conectado")) {
        			eventType = xpp.next();
        			conectado = xpp.getText();
        			values.conectado = conectado;
        		}
        		else if (xpp.getName().equals("idMesa")) {
        			eventType = xpp.next();
        			idMesa = xpp.getText();
        			values.idMesa = idMesa;
        		}
             }         
         
         eventType = xpp.next();
        }
        return values;
    }
}
