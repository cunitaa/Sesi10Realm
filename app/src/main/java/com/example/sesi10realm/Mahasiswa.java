package com.example.sesi10realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Mahasiswa extends RealmObject {
    @PrimaryKey
    private int id;
    
    @Required // Menandakan field ini tidak boleh bernilai null
    private String nama;
    
    @Required
    private String nim;

    // Konstruktor kosong wajib ada agar Realm bisa melakukan instansiasi reflektif
    public Mahasiswa() {
    }

    // Prosedur Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
}