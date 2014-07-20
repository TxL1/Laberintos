package util;

public class Cadenas {

	public static String objetoEnCadena(int pos, int total, String Cadena) {

		String aux = Cadena.substring(Cadena.indexOf("[") + 1), res = "";
		int cont = 0;
				
			while (cont < total) {
				
				if ((cont == pos) && (pos < total - 1)) {
					
					res = aux.substring(0,aux.indexOf(","));
				}
				else if (cont == pos) {
					
					res = aux.substring(0,aux.indexOf("]"));
				}
				else {
					
					aux = aux.substring(aux.indexOf(",") + 1);
				}
				
				cont++;
			}			
		
		return res;
	}
	
	
}
