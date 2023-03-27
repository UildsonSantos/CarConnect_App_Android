package dos.santos.uildson.carconnect;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListagemActivity extends AppCompatActivity {

    private ListView listViewCarros;
    private ArrayList<Carro> carros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem);

        listViewCarros = findViewById(R.id.listViewCarros);

        listViewCarros.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Carro carro = (Carro) listViewCarros.getItemAtPosition(i);

                        Toast.makeText(getApplicationContext(), "Esse é " + carro.getNome(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        popularLista();
    }

    private void popularLista() {

        TypedArray imagens = getResources().obtainTypedArray(R.array.image);
        String[] nomes = getResources().getStringArray(R.array.nome);
        int[] ano = getResources().getIntArray(R.array.ano);
        String[] valores = getResources().getStringArray(R.array.valor);
        String[] cores = getResources().getStringArray(R.array.cor);
        String[] carrocerias = getResources().getStringArray(R.array.carroceria);
        int[] combustiveis_pos = getResources().getIntArray(R.array.combustivel);
        String[] cambios = getResources().getStringArray(R.array.cambio);
        int[] portas = getResources().getIntArray(R.array.portas);
        String[] kilometragens = getResources().getStringArray(R.array.km);
        int[] blindagens = getResources().getIntArray(R.array.blindagem);
        int[] ar_condicionados = getResources().getIntArray(R.array.ar_condicionado);
        String[] aceleracoes = getResources().getStringArray(R.array.aceleracao);
        int[] velocidades_max = getResources().getIntArray(R.array.velocidade_max);
        String[] motores = getResources().getStringArray(R.array.motor);

        carros = new ArrayList<>();

        Carro carro;
        float preco, km, acele;

        Combustivel[] combustiveis = Combustivel.values();

        for (int cont = 0; cont < nomes.length; cont++) {

            carro = new Carro(imagens.getDrawable(cont),
                    nomes[cont],
                    cores[cont],
                    carrocerias[cont],
                    cambios[cont],
                    motores[cont]);

            preco = Float.parseFloat(valores[cont]);
            carro.setValor(preco);

            km = Float.parseFloat(kilometragens[cont]);
            carro.setKm(km);

            acele = Float.parseFloat(aceleracoes[cont]);
            carro.setAceleracao(acele);

            carro.setCombustivel(combustiveis[combustiveis_pos[cont]]);

            carro.setAno(ano[cont]);
            carro.setPortas(portas[cont]);
            carro.setVelocidade_max(velocidades_max[cont]);

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
}