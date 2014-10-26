package com.ocioz.loterianavidad;

public class Boleto {
	
	private String numero;
	private String apostado;
	
	public Boleto(){
		this("default", "default");
	}
	
	public Boleto(String pNumero){
		this(pNumero, "default");
	}
	
	public Boleto(String pNumero, String pApostado){
		this.numero = pNumero;
		this.apostado = pApostado;
	}
	
	public String getNumero(){
		return this.numero;
	}
	
	public String getApostado(){
		return this.apostado;
	}
	
	public void setNumero(String pNumero){
		this.numero = pNumero;
	}
	
	public void setApostado(String pApostado){
		this.apostado = pApostado;
	}
	
}
