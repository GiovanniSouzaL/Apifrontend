import java.util.List;
import java.util.Map;

public class Validador {

    public static String texto(Map<String, String> campos, String nome, int minimo, int maximo, List<String> erros) {
        String valor = campos.get(nome);
        valor = valor == null ? "" : valor.trim();
        if (valor.length() < minimo || valor.length() > maximo) {
            if (minimo == 0) {
                erros.add(nome + ": deve ter no máximo " + maximo + " caracteres");
            } else {
                erros.add(nome + ": deve ter entre " + minimo + " e " + maximo + " caracteres");
            }
        }
        return valor;
    }

    public static String email(Map<String, String> campos, String nome, List<String> erros) {
        String valor = texto(campos, nome, 5, 100, erros);
        if (!valor.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            erros.add(nome + ": e-mail inválido");
        }
        return valor;
    }

    public static String opcao(Map<String, String> campos, String nome, List<String> permitidas, List<String> erros) {
        String valor = campos.get(nome);
        if (valor == null || !permitidas.contains(valor)) {
            erros.add(nome + ": use um destes valores: " + String.join(", ", permitidas));
            return "";
        }
        return valor;
    }
}
