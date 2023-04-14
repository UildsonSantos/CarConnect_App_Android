package dos.santos.uildson.carconnect;

import static dos.santos.uildson.carconnect.ListagemActivity.IS_GRID;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarrosAdapter extends RecyclerView.Adapter<CarrosAdapter.CarroHolder> {

    public static final int LAYOUT_GRID = 1;
    public static final int LAYOUT_LIST = 2;
    private boolean isGridView = false;
    private Context context;
    List<Carro> carros;
    private NumberFormat numberFormat;
    private SharedPreferences sharedPreferences;


    public static class CarroHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textViewNomeCarro;
        public TextView textViewPrecoItem;
        public TextView textViewCombustivelItem;
        public TextView textViewArCondicionadoItem;
        public TextView textViewBlindadoItem;
        public TextView textViewPortasItem;
        public TextView textViewCarroceriaItem;

        public CarroHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textViewNomeCarro = itemView.findViewById(R.id.textViewNomeCarro);
            textViewPrecoItem = itemView.findViewById(R.id.textViewPrecoItem);
            textViewCombustivelItem = itemView.findViewById(R.id.textViewCombustivelItem);
            textViewArCondicionadoItem = itemView.findViewById(R.id.textViewArCondicionadoItem);
            textViewBlindadoItem = itemView.findViewById(R.id.textViewBlindadoItem);
            textViewPortasItem = itemView.findViewById(R.id.textViewPortasItem);
            textViewCarroceriaItem = itemView.findViewById(R.id.textViewCarroceriaItem);
        }
    }

    public CarrosAdapter( Context context, List<Carro> carros,SharedPreferences sharedPreferences) {
        this.carros = carros;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.isGridView = sharedPreferences.getBoolean(ListagemActivity.IS_GRID, isGridView);

        numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    @NonNull
    @Override
    public CarroHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == LAYOUT_GRID ? R.layout.item_grid : R.layout.linha_lista_carros;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new CarroHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return isGridView ? LAYOUT_GRID : LAYOUT_LIST;
    }

    @Override
    public void onBindViewHolder(@NonNull CarroHolder holder, int position) {

        Carro carro = carros.get(position);
        Context context = holder.itemView.getContext();

        holder.imageView.setImageDrawable(carros.get(position).getImage());
        holder.textViewNomeCarro.setText(carros.get(position).getNome());

        if (carros.get(position).isAr_condicionado()) {
            holder.textViewArCondicionadoItem
                    .setText(context.getResources().getString(R.string.sim));
        } else {
            holder.textViewArCondicionadoItem
                    .setText(context.getResources().getString(R.string.nao));
        }

        switch (carros.get(position).getCombustivel()) {
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

        if (carros.get(position).isBlindagem()) {
            holder.textViewBlindadoItem.setText(context.getResources().getString(R.string.sim));
        } else {
            holder.textViewBlindadoItem.setText(context.getResources().getString(R.string.nao));
        }

        String precoFormatado = numberFormat.format(carros.get(position).getValor());

        holder.textViewPrecoItem.setText(precoFormatado);

        String strPort = Integer.toString(carros.get(position).getPortas());
        holder.textViewPortasItem.setText(strPort);
        holder.textViewCarroceriaItem.setText(carros.get(position).getCarroceria());
    }

    @Override
    public int getItemCount() {
        return carros.size();
    }

    public void setGridMode(boolean isGrid) {
        isGridView = isGrid;
        notifyDataSetChanged();
    }
}
