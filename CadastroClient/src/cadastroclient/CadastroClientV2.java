package cadastroclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Produto;

/**
 *
 * @author Cami
 */
public class CadastroClientV2 {

    public void run() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 4321);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String cmd;

        String idPessoa;
        String idProduto;
        String idUsuario;
        String quantidade;
        String valor_unitario;

        out.writeUTF("op1");
        out.writeUTF("op1");
        out.flush();

        SaidaFrame frame = new SaidaFrame();
        frame.setVisible(true);
        ThreadClient thd = new ThreadClient(in, frame.texto);
        thd.start();

        try {
            do {
                System.out.println("Comando\n=========================\nL – Listar\nX – Finalizar\nE – Entrada\nS – Saída\n=========================\nInserir Comando");
                cmd = reader.readLine().toUpperCase();

                if (cmd != null) {
                    switch (cmd) {
                        case "L" -> {
                            out.writeObject("L");
                            System.out.println("---- Lista De Produtos ----");
                            out.flush();
                        }
                        case "X" ->{
                            thd.close();
                        }
                        case "E" -> {
                            out.writeObject("E");
                            System.out.println("---- Insira Dados De Entrada ----");

                            System.out.println("Id Pessoa: ");
                            idPessoa = reader.readLine();
                            out.writeObject(idPessoa);

                            System.out.println("Id Produto: ");
                            idProduto = reader.readLine();
                            out.writeObject(idProduto);

                            System.out.println("Id Usuario: ");
                            idUsuario = reader.readLine();
                            out.writeObject(idUsuario);

                            System.out.println("Quantidade: ");
                            quantidade = reader.readLine();
                            out.writeObject(quantidade);

                            System.out.println("Valor Unitario: ");
                            valor_unitario = reader.readLine();
                            out.writeObject(valor_unitario);
                            out.flush();
                        }
                        case "S" -> {
                            out.writeObject("S");

                            System.out.println("---- Insira Dados De Saida ----");

                            System.out.println("Id Pessoa: ");
                            idPessoa = reader.readLine();
                            out.writeObject(idPessoa);

                            System.out.println("Id Produto: ");
                            idProduto = reader.readLine();
                            out.writeObject(idProduto);

                            System.out.println("Id Usuario: ");
                            idUsuario = reader.readLine();
                            out.writeObject(idUsuario);

                            System.out.println("Quantidade: ");
                            quantidade = reader.readLine();
                            out.writeObject(quantidade);

                            System.out.println("Valor Unitario: ");
                            valor_unitario = reader.readLine();
                            out.writeObject(valor_unitario);
                            out.flush();
                        }
                        default -> {
                            System.out.println("Invalido... ");
                        }
                    }
                }
            } while (!"X".equalsIgnoreCase(cmd));
        } catch (IOException ex) {
            System.out.println("Client error...");
            Logger.getLogger(CadastroClientV2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            frame.dispose();
            out.close();
            in.close();
            socket.close();
            reader.close();

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new CadastroClientV2().run();
    }

}
