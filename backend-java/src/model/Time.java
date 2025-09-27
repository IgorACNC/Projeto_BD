package model;

public class Time {
    private int id_time;
    private String nome;
    private int quant_jogadores;
    private int quant_socios;
    private int fk_tecnico;
    private int fk_presidente;
    private int fk_estadio;

    public Time() {}

    public int getId_time() { return id_time; }
    public void setId_time(int id_time) { this.id_time = id_time; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getQuant_jogadores() { return quant_jogadores; }
    public void setQuant_jogadores(int quant_jogadores) { this.quant_jogadores = quant_jogadores; }

    public int getQuant_socios() { return quant_socios; }
    public void setQuant_socios(int quant_socios) { this.quant_socios = quant_socios; }

    public int getFk_tecnico() { return fk_tecnico; }
    public void setFk_tecnico(int fk_tecnico) { this.fk_tecnico = fk_tecnico; }

    public int getFk_presidente() { return fk_presidente; }
    public void setFk_presidente(int fk_presidente) { this.fk_presidente = fk_presidente; }

    public int getFk_estadio() { return fk_estadio; }
    public void setFk_estadio(int fk_estadio) { this.fk_estadio = fk_estadio; }

    @Override
    public String toString() {
        return "Time [id=" + id_time + ", nome='" + nome + "', socios=" + quant_socios + ", tecnico_id=" + fk_tecnico + "]";
    }
}