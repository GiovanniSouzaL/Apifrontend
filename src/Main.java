import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        String portaEnv = System.getenv("PORT");
        int porta = portaEnv != null ? Integer.parseInt(portaEnv) : 8080;

        Repositorio repositorio = new Repositorio();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/ocorrencias", new OcorrenciaHandler(repositorio));
        servidor.createContext("/contatos", new ContatoHandler(repositorio));
        servidor.createContext("/", new RaizHandler());
        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();

        System.out.println("API no ar na porta " + porta);
    }
}
