package dto;

public class dtoMovimientoJugador {
	
	public String conectado;
	public String exito;
	public String posiciones;

    
    public dtoMovimientoJugador(String p_conectado, String p_exito, String p_posiciones) {
    	
    	this.conectado = p_conectado;
    	this.exito = p_exito;
    	this.posiciones = p_posiciones;
    }
    
    public dtoMovimientoJugador() {
    	
    }
}