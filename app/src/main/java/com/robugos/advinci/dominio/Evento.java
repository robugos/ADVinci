package com.robugos.advinci.dominio;

import java.io.Serializable;

/**
 * Created by Robson on 04/06/2017.
 */

public class Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String local;
    private String data;
    private String descricao;
    private String urlimg;
    private String adimg;
    private String nota;

    public Evento(String nome, String local, String data, String descricao, String urlimg, String adimg, String nota){
        setNome(nome);
        setLocal(local);
        setData(data);
        setDescricao(descricao);
        setUrlimg(urlimg);
        setAdimg(adimg);
        setNota(nota);
    }
    public Evento(String string){
        String[] evento = string.split(";");
        setNome(evento[0]);
        setLocal(evento[1]);
        setData(evento[2]);
        setDescricao(evento[3]);
        setUrlimg(evento[4]);
        setAdimg(evento[5]);
        setNota(evento[6]);
    }


    @Override
    public String toString() {
        return this.nome+";"+this.local+";"+this.data+";"+this.descricao+";"+this.urlimg+";"+this.adimg+";"+this.nota;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlimg() {
        return urlimg;
    }

    public void setUrlimg(String urlimg) {
        this.urlimg = urlimg;
    }

    public String getAdimg() {
        return adimg;
    }

    public void setAdimg(String adimg) {
        this.adimg = adimg;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}