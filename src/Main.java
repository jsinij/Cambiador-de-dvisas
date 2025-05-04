import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modelos.CodigoMonea;
import modelos.Disponible;
import modelos.Moneda;
import modelos.MonedaOmdb;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String API_KEY = "2fda3a2efcbb7811f6b6206b";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static HttpRequest conversionRequest(String origen, String destino, String monto) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/"
                        + origen + "/" + destino + "/" + monto))
                .GET()
                .build();
    }

    private static HttpRequest codigosDisponiblesRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/" + API_KEY + "/codes"))
                .GET()
                .build();
    }

    public static List<CodigoMonea> obtenerCodigosDisponibles() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = codigosDisponiblesRequest();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Disponible datos = gson.fromJson(response.body(), Disponible.class);

            List<CodigoMonea> lista = new ArrayList<>();
            for (List<String> par : datos.supported_codes()) {
                lista.add(new CodigoMonea(par.get(0), par.get(1)));
            }

            return lista;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // lista vacía en caso de error
        }
    }
    public static String buscarNombreMoneda(String codigo, List<CodigoMonea> lista) {
        return lista.stream()
                .filter(m -> m.codigo().equalsIgnoreCase(codigo))
                .map(CodigoMonea::nombre)
                .findFirst()
                .orElse("Nombre desconocido");
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner entrada = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();
        List<CodigoMonea> codigos = obtenerCodigosDisponibles();

        String descripcion = """
            ------------------------------------------------------------
            Bienvenido a este programa cambiador de divisas, selecciona:
            1. Realizar cambio de moneda
            2. Para ver las divisas disponibles
            3. Para salir
            ------------------------------------------------------------""";

        while (true) {
            System.out.println(descripcion);
            var eleccion = entrada.nextLine();

            switch (eleccion) {
                case "1":
                    System.out.println("Ingresa la moneda base en código:");
                    var base = entrada.nextLine().toUpperCase();
                    System.out.println("Ingresa la moneda destino en código:");
                    var destino = entrada.nextLine().toUpperCase();
                    System.out.println("Ingresa el monto que deseas convertir:");
                    var monto = entrada.nextLine();

                    HttpResponse<String> response2 = client.send(
                            conversionRequest(base, destino, monto),
                            HttpResponse.BodyHandlers.ofString());

                    MonedaOmdb miMonedaOmdb = gson.fromJson(response2.body(), MonedaOmdb.class);
                    Moneda cambio = new Moneda(miMonedaOmdb);

                    String nombreBase = buscarNombreMoneda(base, codigos);
                    String nombreDestino = buscarNombreMoneda(destino, codigos);

                    System.out.printf("El resultado de cambiar %s (%s) a %s (%s) es: %.2f%n",
                            base, nombreBase, destino, nombreDestino, cambio.getResultado());
                    break;

                case "2":
                    codigos.forEach(c -> System.out.println(c.codigo() + " - " + c.nombre()));
                    break;

                case "3":
                    System.out.println("Gracias por usar el programa. ¡Hasta luego!");
                    return;

                default:
                    System.out.println("Opción no válida. Por favor, intenta de nuevo.");
            }
        }
    }

}
