package dos.santos.uildson.carconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dos.santos.uildson.carconnect.modelo.Carro;
import dos.santos.uildson.carconnect.persistencia.AppDatabase;
import dos.santos.uildson.carconnect.utils.RecyclerItemClickListener;

public class ListagemCarrosActivity extends AppCompatActivity {

    private static final String ARQUIVO = "dos.santos.uildson.carconnect.MODO_LAYOUT";
    static final String IS_GRID = "IS_GRID";
    private boolean isGrid = false;

    private List<Carro> carros;
    private CarrosAdapter carrosAdapter;
    private RecyclerView recyclerViewCarros;
    SharedPreferences shared;

    private ActionMode actionMode;
    private int posicaoSelecionada = -1;
    private View viewSelecionada;

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.listagem_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuItemEditar:
                    update();
                    mode.finish();
                    return true;
                case R.id.menuItemExcluir:
                    excluir();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null) {
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }
            actionMode = null;
            viewSelecionada = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lerPreferenciaLayout();
        setContentView(R.layout.activity_listagem_carros);

        recyclerViewCarros = findViewById(R.id.recyclerViewCarros);
        recyclerViewCarros.addOnItemTouchListener(

                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewCarros, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Carro carroSelecionado = carros.get(position);
                        toastCarro(carroSelecionado);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        if (actionMode != null) {
                            return;
                        }

                        posicaoSelecionada = position;
                        view.setBackgroundColor(Color.LTGRAY);
                        view.setBackgroundColor(Color.LTGRAY);
                        viewSelecionada = view;
                        actionMode = startSupportActionMode(mActionModeCallback);
                    }
                }));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(ListagemCarrosActivity.this);

                carros = database.carroDao().findAll();

                ListagemCarrosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carrosAdapter = new CarrosAdapter(getApplicationContext(), carros, shared);
                        recyclerViewCarros.setAdapter(carrosAdapter);

                        setLayoutManager(isGrid);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.listagem_opcoes, menu);
        MenuItem item = menu.findItem(R.id.menuItemToggle);
        item.setIcon(isGrid ?
                R.drawable.ic_baseline_format_list_bulleted_24
                : R.drawable.ic_baseline_grid_view_24);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // TODO: 09/05/2023 Fazer o menu para abri carroceria
        SharedPreferences.Editor editor = shared.edit();
        switch (item.getItemId()) {
            case R.id.menuItemToggle:
                isGrid = !isGrid;
                item.setIcon(isGrid ?
                        R.drawable.ic_baseline_format_list_bulleted_24 :
                        R.drawable.ic_baseline_grid_view_24);
                setLayoutManager(isGrid);
                editor.putBoolean(IS_GRID, isGrid);
                editor.apply();
                return true;
            case R.id.menuItemAdicionar:
                CadastroCarroActivity.novoCarro(ListagemCarrosActivity.this);
                return true;
            case R.id.menuItemSobre:
                AutoriaDoApp.sobre(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void lerPreferenciaLayout() {
        shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        isGrid = shared.getBoolean(IS_GRID, isGrid);

        if (!shared.contains(IS_GRID)) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean(IS_GRID, isGrid);
            editor.apply();
        }
    }

    private void setLayoutManager(boolean isGridView) {
        RecyclerView.LayoutManager layoutManager;
        if (isGridView) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerViewCarros.setLayoutManager(layoutManager);
        carrosAdapter.setGridMode(isGridView);
        carrosAdapter.notifyDataSetChanged();
    }

    private void toastCarro(Carro carroSelecionado) {
        Drawable drawable = carroSelecionado.getImageDrawable();
        String nomeCarro = carroSelecionado.getNome();
        View toastView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.toast_layout, null);

        ImageView toastImageView = toastView.findViewById(R.id.toastImageView);
        TextView toastTextView = toastView.findViewById(R.id.toastTextView);

        toastImageView.setImageDrawable(drawable);
        toastTextView.setText(nomeCarro);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    private void popularLista() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(ListagemCarrosActivity.this);

                carros = database.carroDao().findAll();

                ListagemCarrosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carrosAdapter = new CarrosAdapter(getApplicationContext(), carros, shared);
                        recyclerViewCarros.setAdapter(carrosAdapter);
                        carrosAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void update() {
        Carro update = carros.get(posicaoSelecionada);
        CadastroCarroActivity.update(this, update);
    }

    private void excluir() {
        Carro carroSelecionado = carros.get(posicaoSelecionada);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(ListagemCarrosActivity.this);

                database.carroDao().delete(carroSelecionado);

                ListagemCarrosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carros.remove(carroSelecionado);
                        carrosAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == CadastroCarroActivity.ALTERAR
                || requestCode == CadastroCarroActivity.NOVO)
                && resultCode == Activity.RESULT_OK) {
            popularLista();
        }
    }
}
