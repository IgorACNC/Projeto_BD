package model;

public class Jogador {
    private int id_jogador;
    private String nome;
    private int idade;
    private float altura;
    private int numero_camisa;
    private int quant_times_jogados;
    private int gols_temporada_jogador;
    private String pe_dominante;
    private int gols_penalti;
    private int gols_cabeca;
    private float peso;
    private String posicao_jogador;
    private int quant_jogos;
    private String nacionalidade;
    private int assistencias;
    private int fk_time;
    
    public Jogador() {}
    
    public int getId_jogador() { return id_jogador; }
    public void setId_jogador(int id_jogador) { this.id_jogador = id_jogador; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public int getFk_time() { return fk_time; }
    public void setFk_time(int fk_time) { this.fk_time = fk_time; }
    
    public String toString() {
        return "Jogador [id=" + id_jogador + ", nome='" + nome + "', posicao='" + posicao_jogador + "', time_id=" + fk_time + "]";
    }

    public int getIdade() {
        return idade;
    }

     
    public void setIdade(int idade) {
        this.idade = idade;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public int getNumero_camisa() {
        return numero_camisa;
    }

    public void setNumero_camisa(int numero_camisa) {
        this.numero_camisa = numero_camisa;
    }

    public int getQuant_times_jogados() {
        return quant_times_jogados;
    }

    public void setQuant_times_jogados(int quant_times_jogados) {
        this.quant_times_jogados = quant_times_jogados;
    }

    public int getGols_temporada_jogador() {
        return gols_temporada_jogador;
    }

    public void setGols_temporada_jogador(int gols_temporada_jogador) {
        this.gols_temporada_jogador = gols_temporada_jogador;
    }

    public String getPe_dominante() {
        return pe_dominante;
    }

    public void setPe_dominante(String pe_dominante) {
        this.pe_dominante = pe_dominante;
    }

    public int getGols_penalti() {
        return gols_penalti;
    }

    public void setGols_penalti(int gols_penalti) {
        this.gols_penalti = gols_penalti;
    }

    public int getGols_cabeca() {
        return gols_cabeca;
    }

    public void setGols_cabeca(int gols_cabeca) {
        this.gols_cabeca = gols_cabeca;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getPosicao_jogador() {
        return posicao_jogador;
    }

    public void setPosicao_jogador(String posicao_jogador) {
        this.posicao_jogador = posicao_jogador;
    }

    public int getQuant_jogos() {
        return quant_jogos;
    }

    public void setQuant_jogos(int quant_jogos) {
        this.quant_jogos = quant_jogos;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public int getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(int assistencias) {
        this.assistencias = assistencias;
    }

}