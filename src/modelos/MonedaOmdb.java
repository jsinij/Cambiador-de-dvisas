package modelos;

public record MonedaOmdb(String base_code,
                         String target_code,
                         double conversion_rate,
                         double conversion_result){}
