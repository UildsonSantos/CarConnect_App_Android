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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CadastroActivity extends AppCompatActivity {

    public static final String MODO = "MODO";

    public static final String IMAGEM = "IMAGEM";
    public static final String NOME = "NOME";
    public static final String ANO = "ANO";
    public static final String VALOR = "VALOR";
    public static final String COR = "COR";
    public static final String CARROCERIA = "CARROCERIA";
    public static final String COMBUSTIVEL = "COMBUSTIVEL";
    public static final String PORTAS = "PORTAS";
    public static final String KM = "KM";
    public static final String BLINDAGEM = "BLINDAGEM";
    public static final String AR_CONDICIONADO = "AR_CONDICIONADO";
    public static final String CAMBIO = "CAMBIO";
    public static final String ACELERACAO = "ACELERACAO";
    public static final String VELOCIDADE_MAX = "VELOCIDADE_MAX";
    public static final String MOTOR = "MOTOR";

    public static final int NOVO = 1;
    public static final int ALTERAR = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 5;


    private TextInputEditText texInputEditTextNome,
            texInputEditTextValor;
//            texInputEditTextAno,
//            texInputEditTextCor,
//            textInputEditTextKm,
//            textInputEditTextCambio,
//            textInputEditTextAceleracao,
//            textInputEditTextVelocidade,
//            textInputEditTextMotor;

    private CheckBox checkBoxAlcool,
            checkBoxGasolina,
            checkBoxDiesel,
            checkBoxEletrico,
            checkBoxArCondicionado,
            checkBoxBlindado;

    private ImageView imageViewCarro;
    private RadioGroup radioGroupPortas;
    private Spinner spinnerCarroceria;
    private LinearLayout linearLayoutGaleria;

    private int modo;

    private ActivityResultLauncher<String> requestPermissionLauncher =
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
//        texInputEditTextAno = findViewById(R.id.textInputEditTextAno);
//        texInputEditTextCor = findViewById(R.id.textInputEditTextCor);
//        textInputEditTextKm = findViewById(R.id.textInputEditTextKm);
//        textInputEditTextCambio = findViewById(R.id.textInputEditTextCambio);
//        textInputEditTextAceleracao = findViewById(R.id.textInputEditTextAceleracao);
//        textInputEditTextVelocidade = findViewById(R.id.textInputEditTextVelocidade);
//        textInputEditTextMotor = findViewById(R.id.textInputEditTextMotor);

        checkBoxAlcool = findViewById(R.id.checkBoxAlcool);
        checkBoxGasolina = findViewById(R.id.checkBoxGasolina);
        checkBoxDiesel = findViewById(R.id.checkBoxDiesel);
        checkBoxEletrico = findViewById(R.id.checkBoxEletrico);
        checkBoxArCondicionado = findViewById(R.id.checkBoxArCondicionado);
        checkBoxBlindado = findViewById(R.id.checkBoxBlindado);

        radioGroupPortas = findViewById(R.id.radioGroupPortas);

        spinnerCarroceria = findViewById(R.id.spinnerCarroceria);

        linearLayoutGaleria = findViewById(R.id.linearLayoutGaleria);


        popularSpinnerCarroceria();

        linearLayoutGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReadExternalStoragePermission();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO) {
                setTitle(getString(R.string.add_carro));
            }
        }
        texInputEditTextNome.requestFocus();

    }

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
//        texInputEditTextAno.setText(null);
//        texInputEditTextCor.setText(null);
//        textInputEditTextKm.setText(null);
//        textInputEditTextCambio.setText(null);
//        textInputEditTextAceleracao.setText(null);
//        textInputEditTextVelocidade.setText(null);
//        textInputEditTextMotor.setText(null);

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

        String nome = texInputEditTextNome.getText().toString();
        String valorString = texInputEditTextValor.getText().toString();
        String carroceria = (String) spinnerCarroceria.getSelectedItem();
        int portaId = radioGroupPortas.getCheckedRadioButtonId();
//        String cor = texInputEditTextCor.getText().toString();
//        String cambio = textInputEditTextCambio.getText().toString();
//        String motor = textInputEditTextMotor.getText().toString();
//
//        String ano = texInputEditTextAno.getText().toString();
//        String velocidadeString = textInputEditTextVelocidade.getText().toString();
//
//        String kmString = textInputEditTextKm.getText().toString();
//        String aceleracaoString = textInputEditTextAceleracao.getText().toString();


        if (nome == null || nome.trim().isEmpty()) {
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
//
//        if (cor == null || nome.trim().isEmpty()) {
//            texInputEditTextCor.requestFocus();
//            return;
//        }

//        Carro addCarro = new Carro(nome, cor, carroceria, cambio, motor);


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

//        try {
//            int velocidade = Integer.parseInt(velocidadeString);
//            addCarro.setVelocidade_max(velocidade);
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, getString(R.string.velocidade_invalida), Toast.LENGTH_SHORT).show();
//            textInputEditTextVelocidade.requestFocus();
//            return;
//        }

        if (portaId == -1) {
            Toast.makeText(this, getString(R.string.porta_nao_selecionada), Toast.LENGTH_SHORT).show();
            return;
        } else {
            RadioButton radioButtonSelecionado = findViewById(portaId);

            addCarro.setPortas(Integer.parseInt(radioButtonSelecionado.getTag().toString()));
        }

//        if (ano == null || nome.trim().isEmpty() || ano.length() != 4) {
//            texInputEditTextAno.setError(getString(R.string.ano_invalido));
//            texInputEditTextAno.requestFocus();
//            return;
//        } else {
//            texInputEditTextAno.setError(null);
//        }
//        addCarro.setAno(Integer.parseInt(ano));


//        try {
//            float km = Float.parseFloat(kmString);
//            addCarro.setKm(km);
//        } catch (NumberFormatException e) {
//            Toast.makeText(getApplicationContext(), getString(R.string.km_invalido), Toast.LENGTH_SHORT).show();
//            textInputEditTextKm.requestFocus();
//            return;
//        }

//        try {
//            float aceleracao = Float.parseFloat(aceleracaoString);
//            addCarro.setAceleracao(aceleracao);
//        } catch (NumberFormatException e) {
//            Toast.makeText(getApplicationContext(), getString(R.string.aceleracao_invalida), Toast.LENGTH_SHORT).show();
//            textInputEditTextAceleracao.requestFocus();
//            return;
//        }

        if (checkBoxArCondicionado.isChecked()) {
            addCarro.setAr_condicionado(true);
        } else {
            addCarro.setAr_condicionado(false);
        }

        if (checkBoxBlindado.isChecked()) {
            addCarro.setBlindagem(true);
        } else {
            addCarro.setBlindagem(false);
        }

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

//        intent.putExtra(ANO, addCarro.getAno());
//        intent.putExtra(COR, addCarro.getCor());
//        intent.putExtra(KM, addCarro.getKm());
//        intent.putExtra(CAMBIO, addCarro.getCambio());
//        intent.putExtra(ACELERACAO, addCarro.getAceleracao());
//        intent.putExtra(VELOCIDADE_MAX, addCarro.getVelocidade_max());
//        intent.putExtra(MOTOR, addCarro.getMotor());


        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }
}
