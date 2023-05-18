package dos.santos.uildson.carconnect.modelo;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "carros",
        foreignKeys = @ForeignKey(entity = Carroceria.class,
                parentColumns = "id",
                childColumns = "carroceriaId",
                onDelete = ForeignKey.RESTRICT))
public class Carro {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] imageBytes;

    @ColumnInfo(index = true)
    private int carroceriaId;

    @ColumnInfo(name = "ar_condicionado")
    private boolean arCondicionado;

    private String nome;
    private Combustivel combustivel;
    private float valor;
    private int portas;
    private boolean blindagem;
    private Date dataCompra;


    public Carro() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getImageDrawable() {
        return new BitmapDrawable(Resources.getSystem(), BitmapFactory
                .decodeByteArray(this.imageBytes, 0, imageBytes.length));
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getCarroceriaId() {
        return carroceriaId;
    }

    public void setCarroceriaId(int carroceriaId) {
        this.carroceriaId = carroceriaId;
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

    public boolean getBlindagem() {
        return blindagem;
    }

    public void setBlindagem(boolean blindagem) {
        this.blindagem = blindagem;
    }

    public boolean getArCondicionado() {
        return arCondicionado;
    }

    public void setArCondicionado(boolean arCondicionado) {
        this.arCondicionado = arCondicionado;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Date dataCompra) {
        this.dataCompra = dataCompra;
    }
}
