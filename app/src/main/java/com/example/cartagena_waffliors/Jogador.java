package com.example.cartagena_waffliors;

import java.util.ArrayList;

public class Jogador {
	private String nome;
	private String senha;
	private Long id;
	private String erro;	
	private String cor;
	private ArrayList<Carta> cartas = new ArrayList<Carta>();

	public  ArrayList<Carta> getCartas() { return cartas; }
	public void setCartas(Carta carta) { this.cartas.add(carta); }
	public String getCor() {
		return cor;
	}
	public void setCor(String cor) {
		this.cor = cor;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
}
