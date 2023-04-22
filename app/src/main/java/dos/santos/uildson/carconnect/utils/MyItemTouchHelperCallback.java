package dos.santos.uildson.carconnect.utils;

import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import dos.santos.uildson.carconnect.CarrosAdapter;
import dos.santos.uildson.carconnect.R;

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final  CarrosAdapter mAdapter;
    private final RecyclerView paraRecyclerViewCarros;
    public MyItemTouchHelperCallback(CarrosAdapter adapter, RecyclerView recyclerView) {
        mAdapter = adapter;
        this.paraRecyclerViewCarros = recyclerView;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.removeItem(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(paraRecyclerViewCarros, R.string.item_excluido, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.desfazer, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.undoLastRemoval();
            }
        });
        snackbar.show();
    }
}
