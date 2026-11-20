import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http {

    public static void liberarCors(HttpExchange troca) {
        Headers cabecalhos = troca.getResponseHeaders();
        cabecalhos.set("Access-Control-Allow-Origin", "*");
        cabecalhos.set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        cabecalhos.set("Access-Control-Allow-Headers", "Content-Type");
        cabecalhos.set("Access-Control-Expose-Headers", "Location");
    }

    public static void responder(HttpExchange troca, int status, String json) throws IOException {
        if (json == null) {
            troca.sendResponseHeaders(status, -1);
            troca.close();
            return;
        }
        byte[] corpo = json.getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        troca.sendResponseHeaders(status, corpo.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(corpo);
        }
    }

    public static void responderErro(HttpExchange troca, ErroApi erro) throws IOException {
        StringBuilder json = new StringBuilder("{");
        json.append("\"status\":").append(erro.status);
        json.append(",\"erro\":").append(Json.texto(erro.getMessage()));
        if (!erro.detalhes.isEmpty()) {
            List<String> itens = new ArrayList<>();
            for (String detalhe : erro.detalhes) {
                itens.add(Json.texto(detalhe));
            }
            json.append(",\"detalhes\":").append(Json.lista(itens));
        }
        json.append("}");
        responder(troca, erro.status, json.toString());
    }

    public static Map<String, String> lerJson(HttpExchange troca) {
        try {
            byte[] bytes = troca.getRequestBody().readNBytes(100_000);
            String texto = new String(bytes, StandardCharsets.UTF_8);
            return new LeitorJson(texto).lerObjeto();
        } catch (IllegalArgumentException | IOException e) {
            throw new ErroApi(400, "JSON inválido");
        }
    }

    public static String[] partesDoCaminho(HttpExchange troca) {
        String caminho = troca.getRequestURI().getPath();
        caminho = caminho.replaceAll("^/+|/+$", "");
        return caminho.split("/");
    }
}
