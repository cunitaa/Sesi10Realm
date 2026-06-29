package com.example.sesi10realm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder> {

    private List<Mahasiswa> listMahasiswa;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mahasiswa mahasiswa);
    }

    public MahasiswaAdapter(List<Mahasiswa> listMahasiswa, OnItemClickListener listener) {
        this.listMahasiswa = listMahasiswa;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MahasiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new MahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaViewHolder holder, int position) {
        Mahasiswa mhs = listMahasiswa.get(position);
        holder.tvItemNama.setText(mhs.getNama());
        holder.tvItemDetail.setText("NIM: " + mhs.getNim() + "   ID: " + mhs.getId());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(mhs);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public void updateData(List<Mahasiswa> newList) {
        this.listMahasiswa = newList;
        notifyDataSetChanged();
    }

    static class MahasiswaViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemNama, tvItemDetail;

        public MahasiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemNama = itemView.findViewById(R.id.tvItemNama);
            tvItemDetail = itemView.findViewById(R.id.tvItemDetail);
        }
    }
}