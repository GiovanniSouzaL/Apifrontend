import java.util.HashMap;
import java.util.Map;

public class LeitorJson {

    private final String json;
    private int pos = 0;

    public LeitorJson(String json) {
        this.json = json == null ? "" : json;
    }

    public Map<String, String> lerObjeto() {
        Map<String, String> campos = new HashMap<>();
        pularEspacos();
        esperar('{');
        pularEspacos();
        if (atual() == '}') {
            pos++;
            return campos;
        }
        while (true) {
            pularEspacos();
            String chave = lerTexto();
            pularEspacos();
            esperar(':');
            pularEspacos();
            campos.put(chave, lerValor());
            pularEspacos();
            char c = atual();
            pos++;
            if (c == '}') {
                return campos;
            }
            if (c != ',') {
                throw erro();
            }
        }
    }

    private String lerValor() {
        if (atual() == '"') {
            return lerTexto();
        }
        int inicio = pos;
        while (pos < json.length() && ",}".indexOf(json.charAt(pos)) < 0) {
            pos++;
        }
        String bruto = json.substring(inicio, pos).trim();
        if (bruto.isEmpty() || bruto.startsWith("{") || bruto.startsWith("[")) {
            throw erro();
        }
        return bruto.equals("null") ? null : bruto;
    }

    private String lerTexto() {
        esperar('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (pos >= json.length()) {
                throw erro();
            }
            char c = json.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (pos >= json.length()) {
                throw erro();
            }
            char e = json.charAt(pos++);
            switch (e) {
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'u':
                    if (pos + 4 > json.length()) {
                        throw erro();
                    }
                    sb.append((char) Integer.parseInt(json.substring(pos, pos + 4), 16));
                    pos += 4;
                    break;
                default:
                    sb.append(e);
            }
        }
    }

    private char atual() {
        if (pos >= json.length()) {
            throw erro();
        }
        return json.charAt(pos);
    }

    private void esperar(char esperado) {
        if (atual() != esperado) {
            throw erro();
        }
        pos++;
    }

    private void pularEspacos() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    private IllegalArgumentException erro() {
        return new IllegalArgumentException("JSON inválido");
    }
}
