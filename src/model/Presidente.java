package model;

public class Presidente {
    private int id_presidente;
    private String nome;
    private int idade;
    private int quant_titulo;
    private int tempo_cargo;

    public Presidente() {
    }

    public int getId_presidente() {
        return id_presidente;
    }

    public void setId_presidente(int id_presidente) {
        this.id_presidente = id_presidente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getQuant_titulo() {
        return quant_titulo;
    }

    public void setQuant_titulo(int quant_titulo) {
        this.quant_titulo = quant_titulo;
    }

    public int getTempo_cargo() {
        return tempo_cargo;
    }

    public void setTempo_cargo(int tempo_cargo) {
        this.tempo_cargo = tempo_cargo;
    }

    @Override
    public String toString() {
        return "Presidente [id=" + id_presidente + ", nome='" + nome + "', idade=" + idade + ", titulos=" + quant_titulo
                + "]";
    }
}
