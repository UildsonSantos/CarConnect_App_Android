package dos.santos.uildson.carconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dos.santos.uildson.carconnect.modelo.Carro;
import dos.santos.uildson.carconnect.modelo.Combustivel;
import dos.santos.uildson.carconnect.persistencia.CarrosDatabase;
import dos.santos.uildson.carconnect.utils.MyItemTouchHelperCallback;
import dos.santos.uildson.carconnect.utils.RecyclerItemClickListener;

public class ListagemActivity extends AppCompatActivity {

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

    CarrosDatabase database;

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
        setContentView(R.layout.activity_listagem);

        recyclerViewCarros = findViewById(R.id.recyclerViewCarros);
        database = CarrosDatabase.getDatabase(this);
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

        lerPreferenciaLayout();
        popularLista();
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.listagem_opcoes, menu);
        MenuItem item = menu.findItem(R.id.menuItemToggle);
        item.setIcon(isGrid ? R.drawable.ic_baseline_format_list_bulleted_24 : R.drawable.ic_baseline_grid_view_24);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                CadastroActivity.novoCarro(this);
                return true;
            case R.id.menuItemSobre:
                AutoriaDoApp.sobre(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        carros = database.carroDao().getAll();
        carrosAdapter = new CarrosAdapter(this, carros, shared);
        recyclerViewCarros.setAdapter(carrosAdapter);
        setLayoutManager(isGrid);

        MyItemTouchHelperCallback callback =
                new MyItemTouchHelperCallback(carrosAdapter, recyclerViewCarros);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewCarros);
    }

    private void update() {
        Carro update = carros.get(posicaoSelecionada);
        CadastroActivity.update(this, update);
    }

    private void excluir() {
        Carro carroSelecionado = carros.get(posicaoSelecionada);
        database.carroDao().delete(carroSelecionado);
        carros.clear();
        carros.addAll(database.carroDao().getAll());
        carrosAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();

            String nome = bundle.getString(CadastroActivity.NOME);
            String carroceria = bundle.getString(CadastroActivity.CARROCERIA);
            int portas = bundle.getInt(CadastroActivity.PORTAS);
            float valor = bundle.getFloat(CadastroActivity.VALOR);
            boolean blindagem = bundle.getBoolean(CadastroActivity.BLINDAGEM);
            boolean ar_condicionado = bundle.getBoolean(CadastroActivity.AR_CONDICIONADO);
            Combustivel combustivel = (Combustivel) bundle
                    .getSerializable(CadastroActivity.COMBUSTIVEL);

            byte[] byteArray = bundle.getByteArray(CadastroActivity.IMAGEM);
            if (requestCode == CadastroActivity.ALTERAR) {
                Carro selecionado = carros.get(posicaoSelecionada);
                int idDoCarro = selecionado.getId();
                Carro carroModificado = database.carroDao().getCarroPorId(idDoCarro);

                carroModificado.setImageBytes(byteArray);
                carroModificado.setNome(nome);
                carroModificado.setCarroceria(carroceria);
                carroModificado.setCombustivel(combustivel);
                carroModificado.setPortas(portas);
                carroModificado.setValor(valor);
                carroModificado.setBlindagem(blindagem);
                carroModificado.setArCondicionado(ar_condicionado);

                database.carroDao().update(carroModificado);
                carros.clear();
                carros.addAll(database.carroDao().getAll());
                carrosAdapter.notifyDataSetChanged();

                posicaoSelecionada = -1;
            } else {
                Carro newCarItem = new Carro();

                newCarItem.setNome(nome);
                newCarItem.setCarroceria(carroceria);
                newCarItem.setImageBytes(byteArray);
                newCarItem.setPortas(portas);
                newCarItem.setBlindagem(blindagem);
                newCarItem.setArCondicionado(ar_condicionado);
                newCarItem.setCombustivel(combustivel);
                newCarItem.setValor(valor);

                database.carroDao().insert(newCarItem);
                carros.clear();
                carros.addAll(database.carroDao().getAll());
                carrosAdapter.notifyDataSetChanged();
            }
        }
    }
}
