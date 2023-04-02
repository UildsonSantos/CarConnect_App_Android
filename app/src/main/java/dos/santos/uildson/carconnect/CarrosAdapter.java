package dos.santos.uildson.carconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarrosAdapter extends BaseAdapter {
    Context context;
    List<Carro> carros;
    private NumberFormat numberFormat;


    private static class CarroHolder {
        public ImageView imageView;
        public TextView textViewNomeCarro;
        public TextView textViewPrecoItem;
        public TextView textViewCombustivelItem;
        public TextView textViewArCondicionadoItem;
        public TextView textViewBlindadoItem;
        public TextView textViewPortasItem;
        public TextView textViewCarroceriaItem;
//        public TextView textViewAnoItem;
//        public TextView textViewCorItem;
//        public TextView textViewKmItem;
//        public TextView textViewCambioItem;
//        public TextView textViewAceleracaoItem;
//        public TextView textViewVelocidadeItem;
//        public TextView textViewMotor;

    }

    public CarrosAdapter(Context context, List<Carro> carros) {
        this.context = context;
        this.carros = carros;

        numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    @Override
    public int getCount() {
        return carros.size();
    }

    @Override
    public Object getItem(int i) {
        return carros.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CarroHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linha_lista_carros, viewGroup, false);

            holder = new CarroHolder();

            holder.imageView = view.findViewById(R.id.imageView);
            holder.textViewNomeCarro = view.findViewById(R.id.textViewNomeCarro);
            holder.textViewArCondicionadoItem = view.findViewById(R.id.textViewArCondicionadoItem);
            holder.textViewCombustivelItem = view.findViewById(R.id.textViewCombustivelItem);
            holder.textViewBlindadoItem = view.findViewById(R.id.textViewBlindadoItem);
            holder.textViewPrecoItem = view.findViewById(R.id.textViewPrecoItem);
            holder.textViewPortasItem = view.findViewById(R.id.textViewPortasItem);
            holder.textViewCarroceriaItem = view.findViewById(R.id.textViewCarroceriaItem);
//            holder.textViewAceleracaoItem = view.findViewById(R.id.textViewAceleracaoItem);
//            holder.textViewVelocidadeItem = view.findViewById(R.id.textViewVelocidadeItem);
//            holder.textViewKmItem = view.findViewById(R.id.textViewKmItem);
//            holder.textViewCorItem = view.findViewById(R.id.textViewCorItem);
//            holder.textViewAnoItem = view.findViewById(R.id.textViewAnoItem);
//            holder.textViewCambioItem = view.findViewById(R.id.textViewCambioItem);
//            holder.textViewMotor = view.findViewById(R.id.textViewMotor);

            view.setTag(holder);
        } else {
            holder = (CarroHolder) view.getTag();
        }

        holder.imageView.setImageDrawable(carros.get(i).getImage());
        holder.textViewNomeCarro.setText(carros.get(i).getNome());

//        String aceleração = Float.toString(carros.get(i).getAceleracao());
//        holder.textViewAceleracaoItem.setText(aceleração);
//
//        String strVel = Integer.toString(carros.get(i).getVelocidade_max());
//        holder.textViewVelocidadeItem.setText(strVel);

        if (carros.get(i).isAr_condicionado()) {
            holder.textViewArCondicionadoItem.setText(context.getResources().getString(R.string.sim));
        } else {
            holder.textViewArCondicionadoItem.setText(context.getResources().getString(R.string.nao));
        }

        switch (carros.get(i).getCombustivel()) {
            case Alcool:
                holder.textViewCombustivelItem.setText(R.string.alcool);
                break;
            case Gasolina:
                holder.textViewCombustivelItem.setText(R.string.gasolina);
                break;
            case Diesel:
                holder.textViewCombustivelItem.setText(R.string.diesel);
                break;
            case Eletrico:
                holder.textViewCombustivelItem.setText(R.string.eletrico);
                break;
        }

        if (carros.get(i).isBlindagem()) {
            holder.textViewBlindadoItem.setText(context.getResources().getString(R.string.sim));
        } else {
            holder.textViewBlindadoItem.setText(context.getResources().getString(R.string.nao));
        }

        String precoFormatado = numberFormat.format(carros.get(i).getValor());

        holder.textViewPrecoItem.setText(precoFormatado);

        String strPort = Integer.toString(carros.get(i).getPortas());
        holder.textViewPortasItem.setText(strPort);
        holder.textViewCarroceriaItem.setText(carros.get(i).getCarroceria());

//        holder.textViewKmItem.setText(Float.toString(carros.get(i).getKm()));
//        holder.textViewCorItem.setText(carros.get(i).getCor());
//
//        String strAno = Integer.toString(carros.get(i).getAno());
//        holder.textViewAnoItem.setText(strAno);
//        holder.textViewCambioItem.setText(carros.get(i).getCambio());
//        holder.textViewMotor.setText(carros.get(i).getMotor());

        return view;
    }

}
