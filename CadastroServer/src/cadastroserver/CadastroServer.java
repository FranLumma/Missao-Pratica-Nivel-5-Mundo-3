package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Cami
 */
public class CadastroServer {

    public static void main(String args[]) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);
        
        try (ServerSocket serverSocket = new ServerSocket(4321)) {
            System.out.println("Server conectado.");
            while (true) {
                System.out.println("Tentando conectar com cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado.");
                Thread thd = new Thread(new CadastroThreadV2(ctrl, ctrlUsu, ctrlMov, ctrlPessoa, socket));
                thd.start();
            }
        } catch (IOException e) {
            System.out.println("Serv" + e);
        }
    }
}
