import java.util.ArrayList;
import java.util.List;

public class ErroApi extends RuntimeException {

    public final int status;
    public final List<String> detalhes;

    public ErroApi(int status, String mensagem) {
        this(status, mensagem, new ArrayList<>());
    }

    public ErroApi(int status, String mensagem, List<String> detalhes) {
        super(mensagem);
        this.status = status;
        this.detalhes = detalhes;
    }
}
