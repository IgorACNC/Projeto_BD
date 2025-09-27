package model;

public class Estadio {
    private int id_estadio;
    private String nome;
    private int capacidade;
    private String rua;
    private int numero;
    private String bairro;

    public Estadio() {
    }

    public int getId_estadio() {
        return id_estadio;
    }

    public void setId_estadio(int id_estadio) {
        this.id_estadio = id_estadio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    @Override
    public String toString() {
        return "Estadio [id=" + id_estadio + ", nome='" + nome + "', capacidade=" + capacidade + "]";
    }
}