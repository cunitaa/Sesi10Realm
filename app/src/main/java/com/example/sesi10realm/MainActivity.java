package com.example.sesi10realm;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private EditText etNIM, etNama, etSearch;
    private Button btnTambah, btnUpdate, btnHapus, btnSort;
    private MahasiswaAdapter adapter;
    private int selectedId = -1;
    private boolean isAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI
        etNIM = findViewById(R.id.etNIM);
        etNama = findViewById(R.id.etNama);
        etSearch = findViewById(R.id.etSearch);
        btnTambah = findViewById(R.id.btnTambah);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHapus = findViewById(R.id.btnHapus);
        btnSort = findViewById(R.id.btnSort);
        RecyclerView rvMahasiswa = findViewById(R.id.rvMahasiswa);

        // Open Realm
        realm = Realm.getDefaultInstance();

        // Setup RecyclerView
        rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MahasiswaAdapter(new ArrayList<>(), this::onMahasiswaClick);
        rvMahasiswa.setAdapter(adapter);

        // Listeners
        btnTambah.setOnClickListener(v -> {
            String nim = etNIM.getText().toString();
            String nama = etNama.getText().toString();
            if (!nim.isEmpty() && !nama.isEmpty()) {
                tambahData(nama, nim);
                clearForm();
            } else {
                Toast.makeText(this, R.string.msg_empty_fields, Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if (selectedId != -1) {
                String namaBaru = etNama.getText().toString();
                String nimBaru = etNIM.getText().toString();
                updateDataMahasiswa(selectedId, namaBaru, nimBaru);
                clearForm();
            }
        });

        btnHapus.setOnClickListener(v -> {
            if (selectedId != -1) {
                hapusDataMahasiswa(selectedId);
                clearForm();
            }
        });

        btnSort.setOnClickListener(v -> {
            isAscending = !isAscending;
            btnSort.setText(getString(R.string.sort_prefix, isAscending ? "ASC" : "DESC"));
            ambilDanTampilkanData();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ambilDanTampilkanData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initial load
        ambilDanTampilkanData();
    }

    private void onMahasiswaClick(Mahasiswa mhs) {
        selectedId = mhs.getId();
        etNIM.setText(mhs.getNim());
        etNama.setText(mhs.getNama());
        
        btnTambah.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    private void clearForm() {
        etNIM.setText("");
        etNama.setText("");
        selectedId = -1;
        btnTambah.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    private void tambahData(final String nama, final String nim) {
        realm.executeTransactionAsync(bgRealm -> {
            Number maxId = bgRealm.where(Mahasiswa.class).max("id");
            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
            
            Mahasiswa mhs = bgRealm.createObject(Mahasiswa.class, nextId);
            mhs.setNama(nama);
            mhs.setNim(nim);
        }, () -> {
            Toast.makeText(MainActivity.this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
            ambilDanTampilkanData();
        }, error -> {
            error.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.msg_save_fail, Toast.LENGTH_SHORT).show();
        });
    }

    private void ambilDanTampilkanData() {
        String query = etSearch.getText().toString();
        RealmResults<Mahasiswa> results;
        
        if (query.isEmpty()) {
            results = realm.where(Mahasiswa.class).findAll();
        } else {
            results = realm.where(Mahasiswa.class)
                    .contains("nama", query, Case.INSENSITIVE)
                    .or()
                    .contains("nim", query, Case.INSENSITIVE)
                    .findAll();
        }

        results = results.sort("nama", isAscending ? Sort.ASCENDING : Sort.DESCENDING);
        
        List<Mahasiswa> list = realm.copyFromRealm(results);
        adapter.updateData(list);
    }

    private void updateDataMahasiswa(int idMahasiswa, String namaBaru, String nimBaru) {
        realm.executeTransactionAsync(r -> {
            Mahasiswa mhs = r.where(Mahasiswa.class).equalTo("id", idMahasiswa).findFirst();
            if (mhs != null) {
                mhs.setNama(namaBaru);
                mhs.setNim(nimBaru);
            }
        }, () -> {
            Toast.makeText(this, R.string.msg_update_success, Toast.LENGTH_SHORT).show();
            ambilDanTampilkanData();
        });
    }

    private void hapusDataMahasiswa(int idMahasiswa) {
        realm.executeTransactionAsync(r -> {
            Mahasiswa mhs = r.where(Mahasiswa.class).equalTo("id", idMahasiswa).findFirst();
            if (mhs != null) {
                mhs.deleteFromRealm();
            }
        }, () -> {
            Toast.makeText(this, R.string.msg_delete_success, Toast.LENGTH_SHORT).show();
            ambilDanTampilkanData();
        });
    }
}