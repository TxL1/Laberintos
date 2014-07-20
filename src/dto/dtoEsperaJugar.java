package dto;

public class dtoEsperaJugar {
	
	public String conectado;
	public String listos;
	public String idSitio;
	public String codigos;
	public String posiciones;
	

    
    public dtoEsperaJugar(String p_conectado, String p_listos, String p_idSitio, String p_codigos, String p_posiciones) {
    	
    	this.conectado = p_conectado;
    	this.listos = p_listos;
    	this.idSitio = p_idSitio;
    	this.codigos = p_codigos;
    	this.posiciones = p_posiciones;
    }
    
    public dtoEsperaJugar() {
    	
    }
}