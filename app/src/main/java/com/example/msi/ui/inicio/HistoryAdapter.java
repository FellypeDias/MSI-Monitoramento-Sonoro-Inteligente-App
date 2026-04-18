package com.example.msi.ui.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.msi.R;

public final class HistoryAdapter extends ListAdapter <HistoryEvent, HistoryAdapter.VH> {
    public HistoryAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<HistoryEvent> DIFF = new DiffUtil.ItemCallback<HistoryEvent>() {
        @Override
        public boolean areItemsTheSame(@NonNull HistoryEvent oldItem, @NonNull HistoryEvent newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getTime().equals(newItem.getTime());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HistoryEvent oldItem, @NonNull HistoryEvent newItem) {
            return oldItem.getLeftDotDrawable() == newItem.getLeftDotDrawable()
                    && oldItem.getRightDotDrawable() == newItem.getRightDotDrawable()
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getTime().equals(newItem.getTime());
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    static final class VH extends RecyclerView.ViewHolder {
        private final View dotLeft;
        private final View dotRight;
        private final TextView txtTitulo;
        private final TextView txtHora;

        VH(@NonNull View itemView) {
            super(itemView);
            dotLeft = itemView.findViewById(R.id.dotLeft);
            dotRight = itemView.findViewById(R.id.dotRight);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtHora = itemView.findViewById(R.id.txtHora);
        }

        void bind(HistoryEvent e) {
            txtTitulo.setText(e.getTitle());
            txtHora.setText(e.getTime());
            dotLeft.setBackgroundResource(e.getLeftDotDrawable());
            dotRight.setBackgroundResource(e.getRightDotDrawable());
        }
    }
}

