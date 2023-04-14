package dos.santos.uildson.carconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dos.santos.uildson.carconnect.utils.RecyclerItemClickListener;

public class ListagemActivity extends AppCompatActivity {

    private static final String ARQUIVO = "dos.santos.uildson.carconnect.MODO_LAYOUT";
    static final String IS_GRID = "IS_GRID";
    private boolean isGrid = false;

    private ArrayList<Carro> carros = new ArrayList<>();
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

//            listViewCarros.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);

        recyclerViewCarros = findViewById(R.id.recyclerViewCarros);

        recyclerViewCarros.addOnItemTouchListener(

                new RecyclerItemClickListener(getApplicationContext(),
                        recyclerViewCarros,
                        new RecyclerItemClickListener.OnItemClickListener() {

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
                        }
                ));

        lerPreferenciaLayout();

        if (!shared.contains(IS_GRID)) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean(IS_GRID, isGrid);
            editor.apply();
        }

        popularListaAnterior();
    }

    private void lerPreferenciaLayout() {

        shared = getSharedPreferences(ARQUIVO,
                Context.MODE_PRIVATE);

        isGrid = shared.getBoolean(IS_GRID, isGrid);

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

                item.setIcon(isGrid ? R.drawable.ic_baseline_format_list_bulleted_24 : R.drawable.ic_baseline_grid_view_24);
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

        // Obter o Drawable da imagem do objeto Carro selecionado
        Drawable drawable = carroSelecionado.getImage();

        // Obter o nome do carro selecionado
        String nomeCarro = carroSelecionado.getNome();

        // Criar uma nova View para o Toast
        View toastView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_layout, null);
        ImageView toastImageView = toastView.findViewById(R.id.toastImageView);
        TextView toastTextView = toastView.findViewById(R.id.toastTextView);

        // Definir a imagem e o nome do carro na View do Toast
        toastImageView.setImageDrawable(drawable);
        toastTextView.setText(nomeCarro);

        // Criar e exibir o Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    private void popularListaAnterior() {
        TypedArray imagens = getResources().obtainTypedArray(R.array.image);
        String[] nomes = getResources().getStringArray(R.array.nome);
        String[] valores = getResources().getStringArray(R.array.valor);
        int[] combustiveis_pos = getResources().getIntArray(R.array.combustivel);
        int[] portas = getResources().getIntArray(R.array.portas);
        int[] blindagens = getResources().getIntArray(R.array.blindagem);
        int[] ar_condicionados = getResources().getIntArray(R.array.ar_condicionado);
        String[] carrocerias = getResources().getStringArray(R.array.carroceria);

        carros = new ArrayList<>();

        Carro carro;
        float preco;

        Combustivel[] combustiveis = Combustivel.values();

        for (int cont = 0; cont < nomes.length; cont++) {

            carro = new Carro(nomes[cont], carrocerias[cont]);

            carro.setImage(imagens.getDrawable(cont));

            preco = Float.parseFloat(valores[cont]);
            carro.setValor(preco);

            carro.setCombustivel(combustiveis[combustiveis_pos[cont]]);

            carro.setPortas(portas[cont]);

            carro.setBlindagem(blindagens[cont] != 0);
            carro.setAr_condicionado(ar_condicionados[cont] != 0);
            carros.add(carro);
        }
        carrosAdapter = new CarrosAdapter(this, carros, shared);
        recyclerViewCarros.setAdapter(carrosAdapter);
        setLayoutManager(isGrid);
    }

    private void update() {
        Carro update = carros.get(posicaoSelecionada);
        CadastroActivity.update(this, update);
    }

    private void excluir() {
        carros.remove(posicaoSelecionada);
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
            Combustivel combustivel = (Combustivel) bundle.getSerializable(CadastroActivity.COMBUSTIVEL);

            byte[] byteArray = bundle.getByteArray(CadastroActivity.IMAGEM);
            Drawable imagem = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));

            if (requestCode == CadastroActivity.ALTERAR) {
                Carro update = carros.get(posicaoSelecionada);

                update.setImage(imagem);
                update.setNome(nome);
                update.setCarroceria(carroceria);
                update.setCombustivel(combustivel);
                update.setPortas(portas);
                update.setValor(valor);
                update.setBlindagem(blindagem);
                update.setAr_condicionado(ar_condicionado);
                posicaoSelecionada = -1;
            } else {
                Carro newCarItem = new Carro(nome, carroceria);

                newCarItem.setImage(imagem);
                newCarItem.setPortas(portas);
                newCarItem.setBlindagem(blindagem);
                newCarItem.setAr_condicionado(ar_condicionado);
                newCarItem.setCombustivel(combustivel);
                newCarItem.setValor(valor);

                carros.add(newCarItem);
            }
            carrosAdapter.notifyDataSetChanged();
        }
    }
}