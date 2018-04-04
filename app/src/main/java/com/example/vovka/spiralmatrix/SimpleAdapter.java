package com.example.vovka.spiralmatrix;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.MyViewHolder> {

    private static final String TAG = "SimpleAdapter";
    private List<String> mSpiralMatrixList;

    public SimpleAdapter(List<String> spiralMatrixList) {
        this.mSpiralMatrixList = spiralMatrixList;
        Log.d(TAG, "SimpleAdapter: list first time in adapter: " + spiralMatrixList);
    }

    /**Called when RecyclerView need a new RecyclerView.MyViewHolder of the given type to represent
     * an item.
     * @return inflated view
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.item_number, container, false);
        return new MyViewHolder(root);
    }

    /**Called by RecyclerView to display the data at specified position.
     * @param itemHolder object of MyViewHolder inner class
     * @param position in the data set held by the adapter
     */
    @Override
    public void onBindViewHolder(MyViewHolder itemHolder, int position) {
        itemHolder.textView.setText(mSpiralMatrixList.get(position));
    }

    /**
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return mSpiralMatrixList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view_item_matrix);
        }
    }
}
