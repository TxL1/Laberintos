package dto;

public class dtoConectarMesa {
	
	public String conectado;
	public String exito;
	public String idMesa;

    
    public dtoConectarMesa(String p_conectado, String p_exito, String p_idMesa) {
    	
    	this.conectado = p_conectado;
    	this.exito = p_exito;
    	this.idMesa = p_idMesa;
    }
    
    public dtoConectarMesa() {
    	
    }
}