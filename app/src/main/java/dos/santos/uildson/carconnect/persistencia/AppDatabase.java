package dos.santos.uildson.carconnect.persistencia;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import dos.santos.uildson.carconnect.R;
import dos.santos.uildson.carconnect.modelo.Carro;
import dos.santos.uildson.carconnect.modelo.Carroceria;


@Database(entities = {Carro.class, Carroceria.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CarroDao carroDao();

    public abstract CarroceriaDao carroceriaDao();

    private static AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (AppDatabase.class) {
                if (instance == null) {
                    RoomDatabase.Builder builder =
                            Room.databaseBuilder(context, AppDatabase.class, "carros.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    carregaCarroceriasIniciais(context);
                                }
                            });
                        }
                    });

                    instance = (AppDatabase) builder.build();
                }
            }
        }

        return instance;
    }

    private static void carregaCarroceriasIniciais(@NonNull final Context context) {

        String[] descricoes = context.getResources().getStringArray(R.array.carrocerias);

        for (String descricao : descricoes) {
            Carroceria carroceria = new Carroceria(descricao);
            instance.carroceriaDao().insert(carroceria);
        }
    }
}
