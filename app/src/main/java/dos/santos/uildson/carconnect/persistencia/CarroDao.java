package dos.santos.uildson.carconnect.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dos.santos.uildson.carconnect.modelo.Carro;

@Dao
public interface CarroDao {

    @Insert
    long insert(Carro carro);

    @Delete
    void delete(Carro carro);

    @Update
    void update(Carro carro);

    @Query("SELECT * FROM carros WHERE id = :id")
    Carro getCarroPorId(int id);

    @Query("SELECT * FROM carros")
    List<Carro> findAll();

    @Query("SELECT count(*) FROM carros WHERE carroceriaId = :id LIMIT 1")
    int carroceriaEmUsoPorId(long id);
}
