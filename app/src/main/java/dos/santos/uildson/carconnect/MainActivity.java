package dos.santos.uildson.carconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNome, editTextAno, editTextValor, editTextCor;
    private CheckBox checkBoxAlcool, checkBoxGasolina, checkBoxDiesel, checkBoxEletrico;
    private RadioGroup radioGroupPortas;
    private Spinner spinnerCarroceria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNome = findViewById(R.id.editTextNome);
        editTextAno = findViewById(R.id.editTextAno);
        editTextValor = findViewById(R.id.editTextValor);
        editTextCor = findViewById(R.id.editTextCor);

        checkBoxAlcool = findViewById(R.id.checkBoxAlcool);
        checkBoxGasolina = findViewById(R.id.checkBoxGasolina);
        checkBoxDiesel = findViewById(R.id.checkBoxDiesel);
        checkBoxEletrico = findViewById(R.id.checkBoxEletrico);

        radioGroupPortas = findViewById(R.id.radioGroupPortas);


        spinnerCarroceria = findViewById(R.id.spinnerCarroceria);

        popularSpinnerCarroceria();


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
        editTextNome.setText(null);
        editTextAno.setText(null);
        editTextValor.setText(null);
        editTextCor.setText(null);

        checkBoxAlcool.setChecked(false);
        checkBoxGasolina.setChecked(false);
        checkBoxDiesel.setChecked(false);
        checkBoxEletrico.setChecked(false);

        radioGroupPortas.clearCheck();

        editTextNome.requestFocus();


        Toast.makeText(this, R.string.clean_form, Toast.LENGTH_LONG).show();
    }

    public void salvarDados(View view) {
        String nome = editTextNome.getText().toString();
        String ano = editTextAno.getText().toString();
        String valor = editTextValor.getText().toString();
        String cor = editTextCor.getText().toString();

        String message = "";

        if (!(checkBoxAlcool.isChecked()
                || checkBoxGasolina.isChecked()
                || checkBoxDiesel.isChecked()
                || checkBoxEletrico.isChecked())) {
            message += getString(R.string.combustivel_invalido) + "\n";
        }
        if (nome == null || nome.trim().isEmpty()) {
            message += getString(R.string.nome) + "\n";
            editTextNome.requestFocus();
        }
        if (ano == null || nome.trim().isEmpty()) {
            message += getString(R.string.ano) + "\n";
            editTextAno.requestFocus();
        }
        if (valor == null || nome.trim().isEmpty()) {
            message += getString(R.string.valor) + "\n";
            editTextValor.requestFocus();
        }
        if (cor == null || nome.trim().isEmpty()) {
            message += getString(R.string.cor) + "\n";
            editTextCor.requestFocus();
        }

        switch (radioGroupPortas.getCheckedRadioButtonId()) {
            case R.id.radioButton2Portas:
                break;
            case R.id.radioButton4Portas:
                break;
            default:
                message += getString(R.string.portas_invalida) + "\n";
        }

        String carroceria = (String) spinnerCarroceria.getSelectedItem();

        if (carroceria == null) {
            message += getString(R.string.carroceria_invalida) + "\n";
        }

        if (message.equals("")) {
            Toast.makeText(this, R.string.dados_salvos, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }


    }
}