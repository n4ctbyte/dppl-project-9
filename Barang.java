public class Barang {
    private String kode;
    private String nama;
    private int stok;
    private double harga;
    private String satuan;

    public Barang() 
    {
        
    }
    public Barang(String kode, String nama, int stok, double harga, String satuan) 
    {
        this.kode = kode;
        this.nama = nama;
        this.stok = stok;
        this.harga = harga;
        this.satuan = satuan;
    }
    public String getKode() 
    {
        return this.kode;
    }
    public String getNama() 
    {
        return this.nama;
    }
    public int getStok() 
    {
        return this.stok;
    }
    public double getHarga() 
    {
        return this.harga;
    }
    public String getSatuan() 
    {
        return this.satuan;
    }
    public void setKode(String kode) 
    {
        this.kode = kode;
    }
    public void setNama(String nama) 
    {
        this.nama = nama;
    }
    public void setStok(int stok) 
    {
        if (stok >= 0) {
            this.stok = stok;
        } else {
            System.out.println("Error: Stok tidak boleh negatif.");
        }
    }
    public void setHarga(double harga) 
    {
        if (harga >= 0) 
        {
            this.harga = harga;
        } else {
            System.out.println("Error: Harga tidak boleh negatif.");
        }
    }
    public void setSatuan(String satuan) 
    {
        this.satuan = satuan;
    }

    @Override
    public String toString() 
    {
        return "Barang{" +
                "kode='" + kode + '\'' +
                ", nama='" + nama + '\'' +
                ", stok=" + stok +
                ", harga=" + harga +
                ", satuan='" + satuan + '\'' +
                '}';
    }
}