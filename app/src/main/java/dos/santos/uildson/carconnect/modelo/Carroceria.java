package dos.santos.uildson.carconnect.modelo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "carrocerias",
        indices = @Index(value = {"descricao"}, unique = true))
public class Carroceria {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String descricao;

    public Carroceria(String descricao){
        setDescricao(descricao);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString(){
        return getDescricao();
    }
}
