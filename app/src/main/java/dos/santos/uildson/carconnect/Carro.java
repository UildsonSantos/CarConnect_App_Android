package dos.santos.uildson.carconnect;

import android.graphics.drawable.Drawable;

public class Carro {
    private Drawable image;
    private String nome;
    private int ano;
    private float valor;
    private String cor;
    private String carroceria;
    private Combustivel combustivel;
    private int portas;
    private float km;
    private boolean blindagem;
    private boolean ar_condicionado;
    private String cambio;
    private float aceleracao;
    private int velocidade_max;
    private String motor;

    public Carro(
            Drawable image,
            String nome,
            String cor,
            String carroceria,
            String cambio,
            String motor) {
        this.image = image;
        this.nome = nome;
        this.cor = cor;
        this.carroceria = carroceria;
        this.cambio = cambio;
        this.motor = motor;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getCarroceria() {
        return carroceria;
    }

    public void setCarroceria(String carroceria) {
        this.carroceria = carroceria;
    }

    public Combustivel getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(Combustivel combustivel) {
        this.combustivel = combustivel;
    }

    public int getPortas() {
        return portas;
    }

    public void setPortas(int portas) {
        this.portas = portas;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }

    public boolean isBlindagem() {
        return blindagem;
    }

    public void setBlindagem(boolean blindagem) {
        this.blindagem = blindagem;
    }

    public boolean isAr_condicionado() {
        return ar_condicionado;
    }

    public void setAr_condicionado(boolean ar_condicionado) {
        this.ar_condicionado = ar_condicionado;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public float getAceleracao() {
        return aceleracao;
    }

    public void setAceleracao(float aceleracao) {
        this.aceleracao = aceleracao;
    }

    public int getVelocidade_max() {
        return velocidade_max;
    }

    public void setVelocidade_max(int velocidade_max) {
        this.velocidade_max = velocidade_max;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }
}
