package net;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class SimpleXmlPullApp
{

    public ArrayList<UserRecord> lanza(String xml) throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xml ) );
        int eventType = xpp.getEventType();
    	
        ArrayList<UserRecord> values = new ArrayList<UserRecord>();
        UserRecord rec = null;
        int cont = 0;
    
        while (eventType != XmlPullParser.END_DOCUMENT) {

        	if(eventType == XmlPullParser.START_TAG) {

        	 if (xpp.getName().equals("nombre")) {
        		 eventType = xpp.next();
        		 cont++;
        		 rec = new UserRecord(xpp.getText(), "" + cont, "", "","","","");
        	 }
        	 else if (xpp.getName().equals("fecha")) {
        		 eventType = xpp.next();
        		 rec.fecha =xpp.getText();	 
        	 }
        	 else if (xpp.getName().equals("facil")) {
        		 eventType = xpp.next();
        		 rec.facil = xpp.getText();	 
        	 }
        	 else if (xpp.getName().equals("normal")) {
        		 eventType = xpp.next();
        		 rec.normal = xpp.getText();	 
        	 }
        	 else if (xpp.getName().equals("dificil")) {
        		 eventType = xpp.next();
        		 rec.dificil = xpp.getText();	 
        	 }
        	 else if (xpp.getName().equals("insano")) {
        		 eventType = xpp.next();
        		 rec.insano = xpp.getText();	 
        		 values.add(rec);
        	 }
         } 

         eventType = xpp.next();
        }
        return values;
    }
}
