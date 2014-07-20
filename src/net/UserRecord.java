package net;

public class UserRecord {
    public String nombre;
    public String posicion;
    public String fecha;
    public String facil;
    public String normal;
    public String dificil;
    public String insano;
    
    public UserRecord(String nombre, String posicion,String fecha, String facil, String normal, String dificil, String insano) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.fecha = fecha;
        this.facil = facil;
        this.normal = normal;
        this.dificil = dificil;
        this.insano = insano;
    }
}