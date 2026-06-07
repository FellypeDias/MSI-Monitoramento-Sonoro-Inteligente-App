package com.example.msi.ui.inicio;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.msi.databinding.ItemHistoricoBinding;
import java.util.ArrayList;
import java.util.List;

public final class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryEvent> items = new ArrayList<>();

    public void submitList(List<HistoryEvent> events) {
        items.clear();
        if (events != null) {
            items.addAll(events);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoricoBinding binding = ItemHistoricoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoricoBinding binding;

        HistoryViewHolder(ItemHistoricoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HistoryEvent event) {
            binding.txtTitulo.setText(event.getTitle());
            binding.txtHora.setText(event.getTime());
            binding.dotLeft.setBackgroundResource(event.getLeftDotResId());
            binding.dotRight.setBackgroundResource(event.getRightDotResId());
        }
    }
}

