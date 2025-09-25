package model;

public class Tecnico {
    private int id_tecnico;
    private String nome;
    private int idade;
    private int quant_time_treinou;

    public Tecnico() {}

    public int getId_tecnico() { return id_tecnico; }
    public void setId_tecnico(int id_tecnico) { this.id_tecnico = id_tecnico; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public int getQuant_time_treinou() { return quant_time_treinou; }
    public void setQuant_time_treinou(int quant_time_treinou) { this.quant_time_treinou = quant_time_treinou; }

    @Override
    public String toString() {
        return "Tecnico [id=" + id_tecnico + ", nome='" + nome + "', idade=" + idade + "]";
    }
}