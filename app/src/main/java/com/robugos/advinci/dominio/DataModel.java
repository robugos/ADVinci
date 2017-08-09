package com.robugos.advinci.dominio;

/**
 * Created by Robson on 09/08/2017.
 */
public class DataModel {

    public int id;
    public String nome;
    public boolean checked;

    public DataModel(int id, String nome, boolean checked) {
        this.id = id;
        this.nome = nome;
        this.checked = checked;

    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return Integer.toString(id);
    }

    public boolean getStatus(){
        return checked;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setStatus(boolean checked) {
        this.checked = checked;
    }
}
