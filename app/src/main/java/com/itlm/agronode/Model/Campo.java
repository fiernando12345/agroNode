package com.itlm.agronode.Model;

import java.io.Serializable;

public class Campo implements Serializable {
    private String uid;
    private String coordenada;
    private String nombre;

    public Campo(){
    }

    public Campo(String uid, String coordenada, String nombre) {
        this.uid = uid;
        this.coordenada = coordenada;
        this.nombre = nombre;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
