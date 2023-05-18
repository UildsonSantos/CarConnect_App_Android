package dos.santos.uildson.carconnect.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dos.santos.uildson.carconnect.modelo.Carroceria;

@Dao
public interface CarroceriaDao {
    @Insert
    long insert(Carroceria Carroceria);

    @Delete
    void delete(Carroceria Carroceria);

    @Update
    void update(Carroceria Carroceria);

    @Query("SELECT * FROM carrocerias WHERE id = :id")
    Carroceria getCarroceriaById(long id);

    @Query("SELECT * FROM carrocerias ORDER BY descricao ASC")
    List<Carroceria> queryAll();

    @Query("SELECT * FROM carrocerias WHERE descricao = :descricao ORDER BY descricao ASC")
    List<Carroceria> queryForDescricao(String descricao);

    @Query("SELECT count(*) FROM carrocerias")
    int total();
}
