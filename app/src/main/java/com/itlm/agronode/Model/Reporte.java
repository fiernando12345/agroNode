package com.itlm.agronode.Model;

public class Reporte {
    private String UID;
    private String Titulo;
    private String Descripcion;
    private String Coordenada;
    private String Estado;

    public Reporte() {
    }

    public Reporte(String UID, String titulo, String descripcion, String coordenada, String estado) {
        this.UID = UID;
        Titulo = titulo;
        Descripcion = descripcion;
        Coordenada = coordenada;
        Estado = estado;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getCoordenada() {
        return Coordenada;
    }

    public void setCoordenada(String coordenada) {
        Coordenada = coordenada;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
