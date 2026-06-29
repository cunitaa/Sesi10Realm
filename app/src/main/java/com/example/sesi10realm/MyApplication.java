package com.example.sesi10realm;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 1. Inisialisasi Realm Context
        Realm.init(this);
        
        // 2. Setup Konfigurasi Basis Data
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("mahasiswa.realm") // Nama berkas enkripsi/penyimpanan
                .schemaVersion(1) // Versi skema untuk kebutuhan migrasi masa depan
                .allowWritesOnUiThread(true) // Mengizinkan transaksi tulis di UI Thread (untuk demo)
                .build();
        
        // 3. Pasang sebagai konfigurasi global default
        Realm.setDefaultConfiguration(config);
    }
}