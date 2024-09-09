package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Produto;

/**
 *
 * @author Cami
 */
public class CadastroThread extends Thread {

    private final ProdutoJpaController ctrl;
    private final UsuarioJpaController ctrlUsu;
    private final Socket s1;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream()); ObjectInputStream in = new ObjectInputStream(s1.getInputStream())) {

            String username = (String) in.readUTF();
            String password = (String) in.readUTF();

            boolean usuarioValido = (ctrlUsu.validateUser(username, password) != null);

            if (usuarioValido) {
                System.out.println("---- Usuario Valido ----");
                
                while (true) {
                    System.out.println("---- Inserir Comando ----");
                    String cmd = (String) in.readUTF();
                    if (cmd != null) {
                        switch (cmd) {
                            case "L":
                                System.out.println("---- Comando inserido: " + cmd + " ----");
                                List<Produto> lista = ctrl.findProdutoEntities();
                                
                                out.writeObject(lista);
                                
                                ArrayList<String> produtoName = new ArrayList<>();
                                ArrayList<Integer> produtoQuantidade = new ArrayList<>();
                                
                                /**for (Produto p : lista) {
                                    produtoName.add(p.getNome());
                                    produtoQuantidade.add(p.getQuantidade());
                                }

                                out.writeObject(produtoName);
                                out.writeObject(produtoQuantidade);
                                **/
                                break;

                            default:
                                break;
                        }
                    }
                break;
                }

            } else {
                System.out.println("---- Usuario Invalido ----");
                return;
            }
            out.flush();

        } catch (IOException ex) {
            System.out.println("Server error...");
            Logger.getLogger(CadastroThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
