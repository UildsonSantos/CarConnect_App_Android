package dos.santos.uildson.carconnect;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import java.util.List;

import dos.santos.uildson.carconnect.modelo.Carroceria;
import dos.santos.uildson.carconnect.persistencia.AppDatabase;

public class ListagemCarroceriasActivity extends AppCompatActivity {

    private ListView listViewCarroceria;
    private ArrayAdapter<Carroceria> listaAdapter;
    private List<Carroceria> lista;

    public static void abrir(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, ListagemCarroceriasActivity.class);

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_carrocerias);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_home_48);
        }

        listViewCarroceria = findViewById(R.id.listViewItens);

        listViewCarroceria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Carroceria carroceria = (Carroceria) parent.getItemAtPosition(position);

                CadastroCarroceriaActivity.update(ListagemCarroceriasActivity.this, carroceria);
            }
        });

        carregaCarrocerias();

        registerForContextMenu(listViewCarroceria);

        setTitle(R.string.carroceria);
    }

    private void carregaCarrocerias() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase database = AppDatabase.getDatabase(ListagemCarroceriasActivity.this);

                lista = database.carroceriaDao().queryAll();

                ListagemCarroceriasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listaAdapter = new ArrayAdapter<>(ListagemCarroceriasActivity.this,
                                android.R.layout.simple_list_item_1,
                                lista);

                        listViewCarroceria.setAdapter(listaAdapter);
                    }
                });
            }
        });
    }

    private void verificaUsoCarroceria(final Carroceria carroceria) {
        final int[] total = {0};
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(ListagemCarroceriasActivity.this);

                total[0] = database.carroDao().carroceriaEmUsoPorId(carroceria.getId());

                if (total[0] > 0) {
                    ListagemCarroceriasActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            new AlertDialog.Builder(ListagemCarroceriasActivity.this)
                                    .setTitle(getString(R.string.carroceria_em_uso))
                                    .setMessage(getString(R.string.nao_pode_ser_excluida))
                                    .setPositiveButton(R.string.ok, null)
                                    .create()
                                    .show();
                        }
                    });

                    return;

                }
                ListagemCarroceriasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excluirCarroceria(carroceria);
                    }
                });
            }
        });
    }

    private void excluirCarroceria(final Carroceria carroceria) {

        String mensagem = getString(R.string.tem_certeza) + "\n" + carroceria.getDescricao();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmacao))
                .setMessage(mensagem)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase database =
                                        AppDatabase.getDatabase(ListagemCarroceriasActivity.this);

                                database.carroceriaDao().delete(carroceria);

                                ListagemCarroceriasActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listaAdapter.remove(carroceria);
                                        listaAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.nao), null)
                .create()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CadastroCarroceriaActivity.NOVO || requestCode == CadastroCarroceriaActivity.ALTERAR)
                && resultCode == Activity.RESULT_OK) {

            carregaCarrocerias();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_carrocerias, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuItemNovo:
                CadastroCarroceriaActivity.novo(this, CadastroCarroceriaActivity.NOVO);
                return true;
            case R.id.menuItemDone:
                doneListCarroceria();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Carroceria carroceria = (Carroceria) listViewCarroceria.getItemAtPosition(info.position);

        switch (item.getItemId()) {

            case R.id.menuItemAbrir:
                CadastroCarroceriaActivity.update(this, carroceria);
                return true;

            case R.id.menuItemApagar:
                verificaUsoCarroceria(carroceria);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void doneListCarroceria() {
        setResult(Activity.RESULT_OK);
        finish();
    }



    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
        finish();
    }
}
