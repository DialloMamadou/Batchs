package com.example.demo.batch;

public class Evenement {
    public String id;
    public String codeclient;
    public String codesejour;
    public String evenementa;
    public String somme;
    public String dateEvent;

    public Evenement(String id, String codeclient, String codesejour, String event, String somme, String date) {
        this.id = id;
        this.codeclient = codeclient;
        this.codesejour = codesejour;
        this.evenementa = event;
        this.somme = somme;
        this.dateEvent = date;
    }

    public Evenement() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeclient() {
        return codeclient;
    }

    public void setCodeclient(String codeclient) {
        this.codeclient = codeclient;
    }

    public String getCodesejour() {
        return codesejour;
    }

    public void setCodesejour(String codesejour) {
        this.codesejour = codesejour;
    }

    public String getEvenementa() {
        return evenementa;
    }

    public void setEvenementa(String evenementa) {
        this.evenementa = evenementa;
    }

    public String getSomme() {
        return somme;
    }

    public void setSomme(String somme) {
        this.somme = somme;
    }

    public String getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(String date) {
        this.dateEvent = date;
    }
}
