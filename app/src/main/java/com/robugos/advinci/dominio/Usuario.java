package com.robugos.advinci.dominio;

/**
 * Created by Robson on 26/05/2017.
 */

public class Usuario {

    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String facebookid;
    private String googleid;

    public Usuario(String nome, String sobrenome, String email, String senha, String facebookid, String googleid){
        setNome(nome);
        setSobrenome(sobrenome);
        setEmail(email);
        setSenha(senha);
        setFacebookid(facebookid);
        setGoogleid(googleid);
    }

    public Usuario(String email, String senha){
        setEmail(email);
        setSenha(senha);
    }

    public String getNome(){
        return this.nome;
    }

    public String getSobrenome(){
        return this.sobrenome;
    }

    public String getEmail(){
        return this.email;
    }

    public String getSenha(){
        return this.senha;
    }

    public void setSenha(String senha){
        /*String md5 = null;
        if (senha.equals(null))return;
        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(senha.getBytes(), 0, senha.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String salt = "Random##SaltValue#WithSpecialCharacters12@$@4&#%^$*"; //remover salta antes de verificar md5 no php
        this.senha = md5+salt;*/
        this.senha = senha;
    }

    public String getFacebookid(){
        return  facebookid;
    }

    public String getGoogleid(){
        return  googleid;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setSobrenome(String sobrenome){
        this.sobrenome = sobrenome;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setFacebookid(String facebookid){
        this.facebookid = facebookid;
    }

    public void setGoogleid(String googleid){
        this.googleid = googleid;
    }

}
