package dos.santos.uildson.carconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import dos.santos.uildson.carconnect.modelo.Carro;
import dos.santos.uildson.carconnect.modelo.Carroceria;
import dos.santos.uildson.carconnect.modelo.Combustivel;
import dos.santos.uildson.carconnect.persistencia.AppDatabase;

public class CadastroCarroActivity extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String MODO = "MODO";


    public static final int NOVO = 1;
    public static final int DEFAULT_VALUE = NOVO;
    public static final int ALTERAR = 2;
    public static final int CODE_CADASTRO_CATEGORIA = 3;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 5;

    private TextInputEditText texInputEditTextNome, texInputEditTextValor;

    private CheckBox checkBoxAlcool,
            checkBoxGasolina,
            checkBoxDiesel,
            checkBoxEletrico,
            checkBoxArCondicionado,
            checkBoxBlindado;

    private ImageView imageViewCarro;
    private RadioGroup radioGroupPortas;
    private Spinner spinnerCarroceria;
    private List<Carroceria> carroceriasDisponiveis;
    private Carro carro;
    private int modo;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            // Permissão concedida
                            openGallery();
                        } else {
                            // Permissão negada
                            Toast.makeText(this,
                                    R.string.permissao_negada,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @SuppressWarnings("deprecation")
    public static void novoCarro(AppCompatActivity activity) {
        Intent intent = new Intent(activity, CadastroCarroActivity.class);
        intent.putExtra(MODO, NOVO);
        activity.startActivityForResult(intent, NOVO);
    }

    @SuppressWarnings("deprecation")
    public static void update(AppCompatActivity activity, Carro updateCarro) {
        Intent intent = new Intent(activity, CadastroCarroActivity.class);
        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, updateCarro.getId());
        activity.startActivityForResult(intent, ALTERAR);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.cadastro_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.carroceriaMenuItemSalvar:
                salvarDados();
                return true;
            case R.id.menuItemLimpar:
                limparCampos();
                return true;
            case android.R.id.home:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        imageViewCarro = findViewById(R.id.imageViewCarro);

        texInputEditTextNome = findViewById(R.id.texInputEditTextNome);
        texInputEditTextValor = findViewById(R.id.textInputEditTextValor);

        checkBoxAlcool = findViewById(R.id.checkBoxAlcool);
        checkBoxGasolina = findViewById(R.id.checkBoxGasolina);
        checkBoxDiesel = findViewById(R.id.checkBoxDiesel);
        checkBoxEletrico = findViewById(R.id.checkBoxEletrico);
        checkBoxArCondicionado = findViewById(R.id.checkBoxArCondicionado);
        checkBoxBlindado = findViewById(R.id.checkBoxBlindado);

        radioGroupPortas = findViewById(R.id.radioGroupPortas);

        spinnerCarroceria = findViewById(R.id.spinnerCarroceria);

        LinearLayout linearLayoutGaleria = findViewById(R.id.linearLayoutGaleria);

        carregaCarroceria();

        linearLayoutGaleria.setOnClickListener(v -> requestReadExternalStoragePermission());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            modo = bundle.getInt(MODO, DEFAULT_VALUE);

            if (modo == NOVO) {
                setTitle(getString(R.string.add_carro));
                carro = new Carro();
            } else {

                setTitle(getString(R.string.alterar_carro));

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        int id = bundle.getInt(ID);

                        AppDatabase database = AppDatabase.getDatabase(CadastroCarroActivity.this);

                        carro = database.carroDao().getCarroPorId(id);

                        CadastroCarroActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                texInputEditTextNome.setText(carro.getNome());
                                texInputEditTextValor.setText(String.valueOf(carro.getValor()));

                                int posicao = posicaoCarroceria(carro.getCarroceriaId());
                                spinnerCarroceria.setSelection(posicao);

                                int portas = carro.getPortas();
                                if (portas == 2) {
                                    radioGroupPortas.check(R.id.radioButton2Portas);
                                } else {
                                    radioGroupPortas.check(R.id.radioButton4Portas);
                                }

                                boolean blindagem = carro.getBlindagem();
                                checkBoxBlindado.setChecked(blindagem);

                                boolean ar_condicionado = carro.getArCondicionado();
                                checkBoxArCondicionado.setChecked(ar_condicionado);

                                Combustivel combustivel = carro.getCombustivel();
                                switch (combustivel) {
                                    case Alcool:
                                        checkBoxAlcool.setChecked(true);
                                        break;
                                    case Gasolina:
                                        checkBoxGasolina.setChecked(true);
                                        break;
                                    case Diesel:
                                        checkBoxDiesel.setChecked(true);
                                        break;
                                    case Eletrico:
                                        checkBoxEletrico.setChecked(true);
                                        break;
                                }

                                byte[] byteArray = carro.getImageBytes();
                                Drawable imagem = new BitmapDrawable(getResources(),
                                        BitmapFactory
                                                .decodeByteArray(byteArray, 0, byteArray.length));
                                imageViewCarro.setImageDrawable(imagem);

                            }
                        });
                    }
                });
            }
        }
        texInputEditTextNome.requestFocus();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    public void limparCampos() {
        texInputEditTextNome.setText(null);
        texInputEditTextValor.setText(null);
        checkBoxAlcool.setChecked(false);
        checkBoxGasolina.setChecked(false);
        checkBoxDiesel.setChecked(false);
        checkBoxEletrico.setChecked(false);
        checkBoxBlindado.setChecked(false);
        checkBoxArCondicionado.setChecked(false);
        radioGroupPortas.clearCheck();
        texInputEditTextNome.requestFocus();
        Toast.makeText(this, R.string.clean_form, Toast.LENGTH_LONG).show();
    }

    public void salvarDados() {
        Drawable imagemCarro = imageViewCarro.getDrawable();
        byte[] byteArray = getBytes((BitmapDrawable) imagemCarro);
        carro.setImageBytes(byteArray);

        int portaId = radioGroupPortas.getCheckedRadioButtonId();
        if (portaId == -1) {
            Toast.makeText(this,
                    getString(R.string.porta_nao_selecionada),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            RadioButton radioButtonSelecionado = findViewById(portaId);
            carro.setPortas(Integer.parseInt(radioButtonSelecionado.getTag().toString()));
        }

        String nome = Objects.requireNonNull(texInputEditTextNome.getText()).toString();
        if (nome.trim().isEmpty()) {
            texInputEditTextNome.setError(getString(R.string.nome_invalido));
            texInputEditTextNome.requestFocus();
            return;
        }
        carro.setNome(nome);

        Carroceria carroceriaEscolhida = (Carroceria) spinnerCarroceria.getSelectedItem();
        if (carroceriaEscolhida != null){
            carro.setCarroceriaId(carroceriaEscolhida.getId());
        }

        String valorString = Objects.requireNonNull(texInputEditTextValor.getText()).toString();
        try {
            float valor = Float.parseFloat(valorString);
            carro.setValor(valor);
        } catch (NumberFormatException e) {
            texInputEditTextValor.setError(getString(R.string.preco_invalido));
            texInputEditTextValor.requestFocus();
            return;
        }

        if (checkBoxGasolina.isChecked()) {
            carro.setCombustivel(Combustivel.Gasolina);
        } else if (checkBoxAlcool.isChecked()) {
            carro.setCombustivel(Combustivel.Alcool);
        } else if (checkBoxDiesel.isChecked()) {
            carro.setCombustivel(Combustivel.Diesel);
        } else if (checkBoxEletrico.isChecked()) {
            carro.setCombustivel(Combustivel.Eletrico);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.combustivel_invalido),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        carro.setArCondicionado(checkBoxArCondicionado.isChecked());
        carro.setBlindagem(checkBoxBlindado.isChecked());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(CadastroCarroActivity.this);

                if (modo == NOVO) {
                    database.carroDao().insert(carro);
                } else {
                    database.carroDao().update(carro);
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
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this,
                        R.string.permissao_negada_novamente,
                        Toast.LENGTH_SHORT).show();
                requestReadExternalStoragePermission();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageViewCarro.setImageURI(imageUri);
        }
        if (requestCode == CODE_CADASTRO_CATEGORIA &&
                (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)){
            carregaCarroceria();
        }
    }

    @NonNull
    private static byte[] getBytes(BitmapDrawable imagemCarro) {
        Bitmap bitmap = imagemCarro.getBitmap();
        Bitmap resizedImage = resizeImage(bitmap, 512, 512);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap resizeImage(Bitmap image, int maxWidth, int maxHeight) {
        if (maxWidth > 0 && maxHeight > 0 && image != null) {
            int width = image.getWidth();
            int height = image.getHeight();

            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;

            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }

            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        } else {
            return null;
        }
    }

    private int posicaoCarroceria(int carroceriaId){

        for (int pos = 0; pos < carroceriasDisponiveis.size(); pos++){
            Carroceria carroceria = carroceriasDisponiveis.get(pos);

            if (carroceria.getId() == carroceriaId){
                return pos;
            }
        }

        return -1;
    }

    private void carregaCarroceria(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getDatabase(CadastroCarroActivity.this);

                carroceriasDisponiveis = database.carroceriaDao().queryAll();

                CadastroCarroActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Carroceria> spinnerAdapter =
                                new ArrayAdapter<>(CadastroCarroActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        carroceriasDisponiveis);

                        spinnerCarroceria.setAdapter(spinnerAdapter);
                    }
                });
            }
        });
    }

    public void addNewCarroceria(View view) {
        ListagemCarroceriasActivity.abrir(this, CODE_CADASTRO_CATEGORIA);
    }
}
