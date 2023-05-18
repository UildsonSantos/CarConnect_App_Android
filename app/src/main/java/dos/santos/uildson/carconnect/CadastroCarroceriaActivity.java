package dos.santos.uildson.carconnect;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import dos.santos.uildson.carconnect.modelo.Carroceria;
import dos.santos.uildson.carconnect.persistencia.AppDatabase;

public class CadastroCarroceriaActivity extends AppCompatActivity {

    public static final String MODO = "MODO";
    public static final String ID = "ID";
    public static final int NOVO = 1;
    public static final int DEFAULT_VALUE = NOVO;
    public static final int ALTERAR = 2;

    private TextInputEditText editTexDescricao;

    private int modo;
    private Carroceria carroceria;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, CadastroCarroceriaActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void update(Activity activity, Carroceria carroceria) {

        Intent intent = new Intent(activity, CadastroCarroceriaActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, carroceria.getId());

        activity.startActivityForResult(intent, ALTERAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_carroceria);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        editTexDescricao = findViewById(R.id.texInputEditTextCarroceria);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        if (bundle != null) {
            modo = bundle.getInt(MODO, DEFAULT_VALUE);
        }

        if (modo == ALTERAR) {

            setTitle(R.string.alterar_descricao_carroceria);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    int id = bundle.getInt(ID);

                    AppDatabase database = AppDatabase.getDatabase(CadastroCarroceriaActivity.this);

                    carroceria = database.carroceriaDao().getCarroceriaById(id);

                    CadastroCarroceriaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTexDescricao.setText(carroceria.getDescricao());
                        }
                    });
                }
            });

        } else {
            setTitle(R.string.nova_descricao_carroceria);
            carroceria = new Carroceria("");
        }
    }

    private void limpar() {
        editTexDescricao.setText(null);
        editTexDescricao.requestFocus();
    }

    private void salvar() {

        String descricao = Objects.requireNonNull(editTexDescricao.getText()).toString();
        if (descricao.trim().isEmpty()) {
            editTexDescricao.setError(getString(R.string.carroceria_invalida));
            editTexDescricao.requestFocus();
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase database = AppDatabase.getDatabase(CadastroCarroceriaActivity.this);

                List<Carroceria> lista = database.carroceriaDao().queryForDescricao(descricao);

                if (modo == NOVO) {

                    if (lista.size() > 0) {

                        CadastroCarroceriaActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View view = findViewById(R.id.activity_carroceria);
                                Snackbar snackbar = Snackbar.make(view,
                                        getText(R.string.carroceria_cadastrada),
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                }).show();
                            }
                        });

                        return;
                    }

                    carroceria.setDescricao(descricao);

                    database.carroceriaDao().insert(carroceria);

                } else {

                    if (!descricao.equals(carroceria.getDescricao())) {

                        if (lista.size() > 0) {

                            CadastroCarroceriaActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View view = findViewById(R.id.activity_carroceria);
                                    Snackbar snackbar = Snackbar.make(view,
                                            getText(R.string.carroceria_cadastrada),
                                            Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                        }
                                    }).show();
                                }
                            });

                            return;
                        }

                        carroceria.setDescricao(descricao);

                        database.carroceriaDao().update(carroceria);
                    }
                }

                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    public void cancelar() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.carroceria_edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.carroceriaMenuItemSalvar:
                salvar();
                return true;
            case R.id.carroceriaMenuItemLimpar:
                limpar();
                return true;
            case android.R.id.home:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
