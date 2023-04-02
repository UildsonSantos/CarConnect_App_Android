package dos.santos.uildson.carconnect;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListagemActivity extends AppCompatActivity {

    private static final int CADASTRO_REQUEST_CODE = 11;
    private static final int REQUEST_CODE = 12;

    private ListView listViewCarros;
    private ArrayList<Carro> carros = new ArrayList<>();
    CarrosAdapter carrosAdapter;
    private Button buttonSobre, buttonAdicionar;

    private int posicaoSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);

        listViewCarros = findViewById(R.id.listViewCarros);

        buttonSobre = findViewById(R.id.buttonSobre);
        buttonAdicionar = findViewById(R.id.buttonAdicionar);

        listViewCarros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Carro carroSelecionado = (Carro) listViewCarros.getItemAtPosition(i);

                toastCarro(carroSelecionado);
            }
        });

        listViewCarros.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastFirstVisibleItem = 0;
            private boolean isButtonVisible = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Verifica a direção do scroll
                if (lastFirstVisibleItem < firstVisibleItem) {
                    // Scroll para baixo
                    if (isButtonVisible) {
                        // Esconde o botão
                        buttonSobre.setVisibility(View.INVISIBLE);
                        buttonAdicionar.setVisibility(View.INVISIBLE);
                        isButtonVisible = false;
                    }
                } else if (lastFirstVisibleItem > firstVisibleItem) {
                    // Scroll para cima
                    if (!isButtonVisible) {
                        // Mostra o botão
                        buttonSobre.setVisibility(View.VISIBLE);
                        buttonAdicionar.setVisibility(View.VISIBLE);
                        isButtonVisible = true;
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });

        popularLista();
//        popularListaAnterior();

    }

    public void adicionarCarro(View view) {
        CadastroActivity.novoCarro(this);
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

    private void popularLista() {

        carrosAdapter = new CarrosAdapter(this, carros);
        listViewCarros.setAdapter(carrosAdapter);
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

//        int[] ano = getResources().getIntArray(R.array.ano);
//        String[] cores = getResources().getStringArray(R.array.cor);
//        String[] cambios = getResources().getStringArray(R.array.cambio);
//        String[] kilometragens = getResources().getStringArray(R.array.km);
//        String[] aceleracoes = getResources().getStringArray(R.array.aceleracao);
//        int[] velocidades_max = getResources().getIntArray(R.array.velocidade_max);
//        String[] motores = getResources().getStringArray(R.array.motor);

        carros = new ArrayList<>();

        Carro carro;
        float preco, km, acele;

        Combustivel[] combustiveis = Combustivel.values();

        for (int cont = 0; cont < nomes.length; cont++) {

//            carro = new Carro(nomes[cont], cores[cont],
//                    carrocerias[cont], cambios[cont], motores[cont]);

            carro = new Carro(nomes[cont], carrocerias[cont]);

            carro.setImage(imagens.getDrawable(cont));

            preco = Float.parseFloat(valores[cont]);
            carro.setValor(preco);

//            km = Float.parseFloat(kilometragens[cont]);
//            carro.setKm(km);

//            acele = Float.parseFloat(aceleracoes[cont]);
//            carro.setAceleracao(acele);

            carro.setCombustivel(combustiveis[combustiveis_pos[cont]]);

            carro.setPortas(portas[cont]);
//            carro.setAno(ano[cont]);
//            carro.setVelocidade_max(velocidades_max[cont]);

            if (blindagens[cont] == 0) {
                carro.setBlindagem(false);
            } else {
                carro.setBlindagem(true);

            }
            if (ar_condicionados[cont] == 0) {
                carro.setAr_condicionado(false);
            } else {
                carro.setAr_condicionado(true);

            }

            carros.add(carro);
        }
        CarrosAdapter carrosAdapter = new CarrosAdapter(this, carros);
        listViewCarros.setAdapter(carrosAdapter);
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

//            String cor = bundle.getString(CadastroActivity.COR);
//            String cambio = bundle.getString(CadastroActivity.CAMBIO);
//            String motor = bundle.getString(CadastroActivity.MOTOR);
//
//            int ano = bundle.getInt(CadastroActivity.ANO);
//            int velocidade_max = bundle.getInt(CadastroActivity.VELOCIDADE_MAX);
//            float aceleracao = bundle.getFloat(CadastroActivity.ACELERACAO);
//            float km = bundle.getFloat(CadastroActivity.KM);


            if (requestCode == CadastroActivity.ALTERAR) {
                // TODO: implementar update
            } else {
//                Carro newCarItem = new Carro(nome, cor, carroceria, cambio, motor);
                Carro newCarItem = new Carro(nome, carroceria);
                newCarItem.setImage(imagem);
                newCarItem.setPortas(portas);
                newCarItem.setBlindagem(blindagem);
                newCarItem.setAr_condicionado(ar_condicionado);
                newCarItem.setCombustivel(combustivel);
                newCarItem.setValor(valor);
                carros.add(newCarItem);
//                newCarItem.setVelocidade_max(velocidade_max);
//                newCarItem.setAno(ano);
//                newCarItem.setAceleracao(aceleracao);
//                newCarItem.setKm(km);
            }
            carrosAdapter.notifyDataSetChanged();
        }
    }

    public void sobre(View view) {
        AutoriaDoApp.sobre(this);
    }
}