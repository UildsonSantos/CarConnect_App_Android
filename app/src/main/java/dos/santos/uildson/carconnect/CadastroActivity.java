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
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import dos.santos.uildson.carconnect.modelo.Carro;
import dos.santos.uildson.carconnect.modelo.Combustivel;

public class CadastroActivity extends AppCompatActivity {

    public static final String MODO = "MODO";

    public static final String IMAGEM = "IMAGEM";
    public static final String NOME = "NOME";
    public static final String VALOR = "VALOR";
    public static final String CARROCERIA = "CARROCERIA";
    public static final String COMBUSTIVEL = "COMBUSTIVEL";
    public static final String PORTAS = "PORTAS";
    public static final String BLINDAGEM = "BLINDAGEM";
    public static final String AR_CONDICIONADO = "AR_CONDICIONADO";

    public static final int NOVO = 1;
    public static final int ALTERAR = 2;
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

        Intent intent = new Intent(activity, CadastroActivity.class);
        intent.putExtra(MODO, NOVO);
        activity.startActivityForResult(intent, NOVO);
    }

    public static void update(AppCompatActivity activity, Carro update) {
        Intent intent = new Intent(activity, CadastroActivity.class);
        intent.putExtra(MODO, ALTERAR);

        byte[] byteArray = getBytes((BitmapDrawable) update.getImageDrawable());
        intent.putExtra(IMAGEM, byteArray);
        intent.putExtra(NOME, update.getNome());
        intent.putExtra(VALOR, update.getValor());
        intent.putExtra(CARROCERIA, update.getCarroceria());
        intent.putExtra(PORTAS, update.getPortas());
        intent.putExtra(BLINDAGEM, update.getBlindagem());
        intent.putExtra(AR_CONDICIONADO, update.getArCondicionado());

        Bundle bundle = new Bundle();
        bundle.putSerializable(COMBUSTIVEL, update.getCombustivel());
        intent.putExtras(bundle);
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
            case R.id.menuItemSalvar:
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

        popularSpinnerCarroceria();

        linearLayoutGaleria.setOnClickListener(v -> requestReadExternalStoragePermission());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            int modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO) {
                setTitle(getString(R.string.add_carro));
            } else {

                setTitle(getString(R.string.alterar_carro));

                String nome = bundle.getString(CadastroActivity.NOME);
                texInputEditTextNome.setText(nome);

                float valor = bundle.getFloat(CadastroActivity.VALOR);
                texInputEditTextValor.setText(String.valueOf(valor));

                String carroceria = bundle.getString(CadastroActivity.CARROCERIA);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCarroceria.getAdapter();
                int count = adapter.getCount();
                for (int i = 0; i < count; i++) {
                    String item = adapter.getItem(i);
                    if (item.equals(carroceria)) {
                        spinnerCarroceria.setSelection(i);
                        break;
                    }
                }


                int portas = bundle.getInt(CadastroActivity.PORTAS);

                if (portas == 2) {
                    radioGroupPortas.check(R.id.radioButton2Portas);
                } else {
                    radioGroupPortas.check(R.id.radioButton4Portas);
                }

                boolean blindagem = bundle.getBoolean(CadastroActivity.BLINDAGEM);
                checkBoxBlindado.setChecked(blindagem);
                boolean ar_condicionado = bundle.getBoolean(CadastroActivity.AR_CONDICIONADO);
                checkBoxArCondicionado.setChecked(ar_condicionado);


                Combustivel combustivel = (Combustivel) bundle
                        .getSerializable(CadastroActivity.COMBUSTIVEL);

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

                byte[] byteArray = bundle.getByteArray(CadastroActivity.IMAGEM);
                Drawable imagem = new BitmapDrawable(getResources(),
                        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                imageViewCarro.setImageDrawable(imagem);
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

    public void popularSpinnerCarroceria() {
        ArrayList<String> carrocerias = new ArrayList<>();

        carrocerias.add(getString(R.string.hatch));
        carrocerias.add(getString(R.string.sedan));
        carrocerias.add(getString(R.string.suv));
        carrocerias.add(getString(R.string.crossover));
        carrocerias.add(getString(R.string.minivan));
        carrocerias.add(getString(R.string.picape));
        carrocerias.add(getString(R.string.wagon));
        carrocerias.add(getString(R.string.conversivel));
        carrocerias.add(getString(R.string.cupe));
        carrocerias.add(getString(R.string.luxo));

        Collections.sort(carrocerias);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                carrocerias);

        spinnerCarroceria.setAdapter(adapter);
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
        String nome = Objects.requireNonNull(texInputEditTextNome.getText()).toString();
        String valorString = Objects.requireNonNull(texInputEditTextValor.getText()).toString();
        String carroceria = (String) spinnerCarroceria.getSelectedItem();
        int portaId = radioGroupPortas.getCheckedRadioButtonId();

        if (nome.trim().isEmpty()) {
            texInputEditTextNome.setError(getString(R.string.nome_invalido));
            texInputEditTextNome.requestFocus();
            return;
        }

        Carro addCarro = new Carro();
        addCarro.setNome(nome);
        addCarro.setCarroceria(carroceria);

        try {
            float valor = Float.parseFloat(valorString);
            addCarro.setValor(valor);
        } catch (NumberFormatException e) {
            texInputEditTextValor.setError(getString(R.string.preco_invalido));
            texInputEditTextValor.requestFocus();
            return;
        }

        if (checkBoxGasolina.isChecked()) {
            addCarro.setCombustivel(Combustivel.Gasolina);
        } else if (checkBoxAlcool.isChecked()) {
            addCarro.setCombustivel(Combustivel.Alcool);
        } else if (checkBoxDiesel.isChecked()) {
            addCarro.setCombustivel(Combustivel.Diesel);
        } else if (checkBoxEletrico.isChecked()) {
            addCarro.setCombustivel(Combustivel.Eletrico);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.combustivel_invalido),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (portaId == -1) {
            Toast.makeText(this,
                    getString(R.string.porta_nao_selecionada),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            RadioButton radioButtonSelecionado = findViewById(portaId);
            addCarro.setPortas(Integer.parseInt(radioButtonSelecionado.getTag().toString()));
        }

        addCarro.setArCondicionado(checkBoxArCondicionado.isChecked());
        addCarro.setBlindagem(checkBoxBlindado.isChecked());
        byte[] byteArray = getBytes((BitmapDrawable) imagemCarro);

        Intent intent = new Intent();
        intent.putExtra(IMAGEM, byteArray);
        intent.putExtra(NOME, addCarro.getNome());
        intent.putExtra(VALOR, addCarro.getValor());
        intent.putExtra(CARROCERIA, addCarro.getCarroceria());
        intent.putExtra(PORTAS, addCarro.getPortas());
        intent.putExtra(BLINDAGEM, addCarro.getBlindagem());
        intent.putExtra(AR_CONDICIONADO, addCarro.getArCondicionado());

        Bundle bundle = new Bundle();
        bundle.putSerializable(COMBUSTIVEL, addCarro.getCombustivel());
        intent.putExtras(bundle);

        setResult(Activity.RESULT_OK, intent);
        finish();
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
}
