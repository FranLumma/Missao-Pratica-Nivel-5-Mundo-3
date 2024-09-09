package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Pessoa;
import model.Produto;
import model.Usuario;

/**
 *
 * @author Cami
 */
public class CadastroThreadV2 extends Thread {

    private final ProdutoJpaController ctrl;
    private final UsuarioJpaController ctrlUsu;
    private final MovimentoJpaController ctrlMov;
    private final PessoaJpaController ctrlPessoa;
    private final Socket s1;

    public CadastroThreadV2(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
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
                    Movimento movimento;

                    Integer idPessoa;
                    Integer idProduto;
                    Integer idUsuario;
                    Integer quantidade;
                    Long valor_unitario;

                    Pessoa pessoa;
                    Produto produto;
                    Usuario usuario;

                    System.out.println("---- Aguardando Comando ----");
                    String cmd = (String) in.readObject();
                    if (cmd != null) {
                        System.out.println("---- Comando inserido: " + cmd + " ----");
                        switch (cmd) {
                            case "L" -> {
                                List<Produto> lista = ctrl.findProdutoEntities();

                                out.writeObject(lista);
                                out.flush();
                            }

                            case "E" -> {
                                idPessoa = Integer.valueOf((String) in.readObject());
                                idProduto = Integer.valueOf((String) in.readObject());
                                idUsuario = Integer.valueOf((String) in.readObject());
                                quantidade = Integer.valueOf((String) in.readObject());
                                valor_unitario = Long.valueOf((String) in.readObject());

                                pessoa = ctrlPessoa.findPessoa(idPessoa);
                                produto = ctrl.findProduto(idProduto);
                                usuario = ctrlUsu.findUsuario(idUsuario);

                                if (produto == null) {
                                    System.out.println("Produto não cadastrado...");
                                    continue;
                                }

                                movimento = new Movimento();
                                movimento.setPessoaidPessoa(pessoa);
                                movimento.setProdutoidProduto(produto);
                                movimento.setUsuarioidUsuario(usuario);
                                movimento.setQuantidade(quantidade);
                                movimento.setTipo('E');
                                movimento.setValorUnitario(valor_unitario);

                                produto.setQuantidade(produto.getQuantidade() + quantidade);

                                ctrl.edit(produto);
                                ctrlMov.create(movimento);
                            }

                            case "S" -> {
                                idPessoa = Integer.valueOf((String) in.readObject());
                                idProduto = Integer.valueOf((String) in.readObject());
                                idUsuario = Integer.valueOf((String) in.readObject());
                                quantidade = Integer.valueOf((String) in.readObject());
                                valor_unitario = Long.valueOf((String) in.readObject());

                                pessoa = ctrlPessoa.findPessoa(idPessoa);
                                produto = ctrl.findProduto(idProduto);
                                usuario = ctrlUsu.findUsuario(idUsuario);

                                movimento = new Movimento();
                                movimento.setPessoaidPessoa(pessoa);
                                movimento.setProdutoidProduto(produto);
                                movimento.setUsuarioidUsuario(usuario);
                                movimento.setQuantidade(quantidade);
                                movimento.setTipo('S');
                                movimento.setValorUnitario(valor_unitario);

                                produto.setQuantidade(produto.getQuantidade() - quantidade);

                                ctrl.edit(produto);
                                ctrlMov.create(movimento);
                            }

                            default -> {
                                System.out.println("-XXX Comando Invalido XXX-");
                            }
                        }
                    }
                }
            } else {
                System.out.println("---- Usuario Invalido ----");
            }
        } catch (IOException ex) {
            System.out.println("Client disconectado.");
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
