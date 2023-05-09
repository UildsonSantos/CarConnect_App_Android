package dos.santos.uildson.carconnect.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import dos.santos.uildson.carconnect.modelo.Carro;

@Database(entities = {Carro.class}, version = 1, exportSchema = false)
public abstract class CarrosDatabase extends RoomDatabase {

    public abstract CarroDao carroDao();

    private static CarrosDatabase instance;

    public static CarrosDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (CarrosDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, CarrosDatabase.class, "carros.db")
                            .allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}


