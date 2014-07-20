package log;

import java.util.ArrayList;
import java.util.List;

public class Laberintos {

	private static int INF = 100;
	
	
	///
	/// Metodos generacion laberinto
	///
	
    public List<String> generaCodigos(List<List<Integer>> ady, int[][] coord, int numNodos) {
		
		List<String> cod = new ArrayList<String>();
		
		for (int i = 0; i < numNodos;i++ ) {
			
			int der = 0, izq = 0, top = 0, bot = 0;
			
			for (int j = 0; j < numNodos; j++) 
				if (i != j) {
							
					if ((ady.get(i).get(j) != INF) && (ady.get(i).get(j) != -1)) { // hay arista, hay camino
										
						// borrar contraria!!
						//ady.get(j).set(i, -1);		
						
						int res = posicionDestino(coord[i],coord[j]);
						
						if (res == 1) der = 1;
						else if (res == 2) izq = 1;
						else if (res == 3) top = 1;
						else if (res == 4) bot = 1;
					}

				}
			
			cod.add(der + "" + izq + "" + top + "" + bot);
		}
		
		return cod;
	}
	
	public static int posicionDestino(int[] origen, int[] destino) {
		
		// X --> 1
		// Y | 0
		int res = 0;
		
		if (origen[1] < destino[1]) // der
			res = 1;
		else if (origen[1] > destino[1]) // izq
			res = 2;
		else if (origen[0] > destino[0]) // top
			res = 3;
		else if (origen[0] < destino[0]) res = 4; // down
		
		return res;
	}
    
	public int[][] cargaMatriz(int n, List<List<Integer>> ady, int numNodos) {
		
		List<Integer> list;
		for (int j = 0; j < numNodos; j++) {
			list = new ArrayList<Integer>();
			for (int i = 0; i < numNodos; i++) {    		
				list.add(INF);
			}
			
			ady.add(list);
		}
		
		int[][] coord = new int[numNodos][n];
		int cont = 0;
		
		for (int j = 0; j < n; j++)
			for (int i = 0; i < n; i++) {
				
				//System.out.println(j + "," + i);
				coord[cont][0] = j;
				coord[cont][1] = i;
				
				cont++;
		}
		
		for (int i = 0; i < cont; i++) {
			
			/*
			// x , der
			if (coord[i][1] + 1 < n) System.out.println("DER " + i);
			// x , izq
			if (coord[i][1] - 1 >= 0) System.out.println("IZQ " + i);
			
			// y , down
			if (coord[i][0] + 1 < n) System.out.println("DOWN " + i);
			// x , top
			if (coord[i][0] - 1 >= 0) System.out.println("TOP " + i);
			*/
			
			// x , der
			if (coord[i][1] + 1 < n) {
				//System.out.println("DER " + i + " " + (i+1));
				ady.get(i).set(i + 1,aleatorio(9, 1));
			}
			// x , izq
			if (coord[i][1] - 1 >= 0) {
				//System.out.println("IZQ " + i + " " + (i-1));
				ady.get(i).set(i - 1,aleatorio(9, 1));
			}
			// y , down
			if (coord[i][0] + 1 < n) {
				//System.out.println("DOWN " + i + " " + (i + n));
				ady.get(i).set(i + n,aleatorio(9, 1));
			}
			// x , top
			if (coord[i][0] - 1 >= 0) {
				//System.out.println("TOP " + i + " " + (i - n));
				ady.get(i).set(i - n,aleatorio(9, 1));
			}
		}
		
		return coord;
	}
	
	// dada una matriz de adyacencia ponderada, devuelve el arbol de recubrimiento minimo, Kruskal
	public List<List<Integer>> Grafo(List<List<Integer>> ady, int numNodos) {
		
		List<List<Integer>> adyacencias = ady;
		List<List<Integer>> arbol = new ArrayList<List<Integer>>(numNodos);
		List<Integer> pertenece = new ArrayList<Integer>(numNodos);
		
		for (int i = 0; i < numNodos; i++) {
			List<Integer> aux = new ArrayList<Integer>(numNodos);
			for (int j = 0; j < numNodos; j++) aux.add(INF);
			arbol.add(aux);
			pertenece.add(i);
		}

		int nodoA = 0, nodoB = 0, arcos = 1;
		
		while (arcos < numNodos) {
			
			int min = INF;
			for (int i = 0; i < numNodos; i++)
				for (int j = 0; j < numNodos; j++)
					if ((min > adyacencias.get(i).get(j)) && 
						(pertenece.get(i) != pertenece.get(j))){
					
						min = adyacencias.get(i).get(j);
						nodoA = i;
						nodoB = j;
					}
			
			if (pertenece.get(nodoA) != pertenece.get(nodoB)) {
				
				List<Integer> aux = arbol.get(nodoA);
				aux.set(nodoB,min);
				arbol.set(nodoA,aux);
				
				aux = arbol.get(nodoB);
				aux.set(nodoA,min);
				arbol.set(nodoB,aux);
				
				int temp = pertenece.get(nodoB);
				pertenece.set(nodoB, pertenece.get(nodoA));
				for (int k = 0; k < numNodos; k++)
					if (pertenece.get(k) == temp)
						pertenece.set(k, pertenece.get(nodoA));
				
				arcos++;
			}
		}
		
		return arbol;
	}
	
	public static int aleatorio(int max,int min){
		return (int)(Math.random()*(max-min))+min;		
	}

}
