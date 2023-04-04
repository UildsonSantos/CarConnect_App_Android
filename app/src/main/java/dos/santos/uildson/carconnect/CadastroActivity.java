package dos.santos.uildson.carconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

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


    private TextInputEditText texInputEditTextNome,texInputEditTextValor;

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
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permissão concedida
                    openGallery();
                } else {
                    // Permissão negada
                    Toast.makeText(this, R.string.permissao_negada, Toast.LENGTH_SHORT).show();
                }
            });

    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida
            openGallery();
        } else {
            // Permissão não concedida, solicite-a
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @SuppressWarnings("deprecation")
    public static void novoCarro(AppCompatActivity activity) {

        Intent intent = new Intent(activity, CadastroActivity.class);
        intent.putExtra(MODO, NOVO);
        activity.startActivityForResult(intent, NOVO);
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
            }
        }
        texInputEditTextNome.requestFocus();

    }

    @SuppressWarnings("deprecation")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    public void popularSpinnerCarroceria() {
        ArrayList<String> lista = new ArrayList<>();

        lista.add(getString(R.string.hatch));
        lista.add(getString(R.string.sedan));
        lista.add(getString(R.string.suv));
        lista.add(getString(R.string.crossover));
        lista.add(getString(R.string.minivan));
        lista.add(getString(R.string.picape));
        lista.add(getString(R.string.wagon));
        lista.add(getString(R.string.conversivel));
        lista.add(getString(R.string.cupe));
        lista.add(getString(R.string.luxo));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                lista);

        spinnerCarroceria.setAdapter(adapter);
    }

    public void limparCampos(View view) {
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

    public void salvarDados(View view) {

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

        Carro addCarro = new Carro(nome, carroceria);

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
            Toast.makeText(getApplicationContext(), getString(R.string.combustivel_invalido), Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        if (portaId == -1) {
            Toast.makeText(this, getString(R.string.porta_nao_selecionada), Toast.LENGTH_SHORT).show();
            return;
        } else {
            RadioButton radioButtonSelecionado = findViewById(portaId);

            addCarro.setPortas(Integer.parseInt(radioButtonSelecionado.getTag().toString()));
        }


        addCarro.setAr_condicionado(checkBoxArCondicionado.isChecked());

        addCarro.setBlindagem(checkBoxBlindado.isChecked());

        // Converter a imagem em um array de bytes
        Bitmap bitmap = ((BitmapDrawable) imagemCarro).getBitmap();

        Bitmap resizedImage = resizeImage(bitmap, 512, 512);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        Intent intent = new Intent();


        intent.putExtra(IMAGEM, byteArray);
        intent.putExtra(NOME, addCarro.getNome());
        intent.putExtra(VALOR, addCarro.getValor());

        intent.putExtra(CARROCERIA, addCarro.getCarroceria());


        Bundle bundle = new Bundle();
        bundle.putSerializable(COMBUSTIVEL, addCarro.getCombustivel());

        intent.putExtras(bundle);
        intent.putExtra(PORTAS, addCarro.getPortas());
        intent.putExtra(BLINDAGEM, addCarro.isBlindagem());
        intent.putExtra(AR_CONDICIONADO, addCarro.isAr_condicionado());

        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, agora podemos acessar a galeria
                openGallery();
            } else {
                // Permissão negada, informe o usuário e solicite novamente
                Toast.makeText(this, R.string.permissao_negada_novamente, Toast.LENGTH_SHORT).show();
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

    public Bitmap resizeImage(Bitmap image, int maxWidth, int maxHeight) {
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
