package modelos;

public class Moneda {
    String monedaBase;
    String monedaDestino;
    double conversionRate;
    double conversionResult;

    public Moneda(MonedaOmdb monedaOmdb){
        this.monedaBase = monedaOmdb.base_code();
        this.monedaDestino = monedaOmdb.target_code();
        this.conversionRate = monedaOmdb.conversion_rate();
        this.conversionResult = monedaOmdb.conversion_result();
    }

    @Override
    public String toString() {
        return monedaBase + " " + monedaDestino + " " + conversionRate + " " + conversionResult;
    }

    public double getResultado() {
        return conversionResult;
    }
}
