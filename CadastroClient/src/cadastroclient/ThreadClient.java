package cadastroclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.Produto;

/**
 *
 * @author Cami
 */
public class ThreadClient extends Thread {

    ObjectInputStream entrada;
    JTextArea textArea;
    
    boolean isClosed;
    
    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
        this.isClosed = false;
    }

    @Override
    public void run() {
        try {
            while (!isClosed) {
                Object obj = entrada.readObject();
                SwingUtilities.invokeLater(() -> {
                    if (obj instanceof String) {
                        textArea.append(obj + "\n");
                    } else if (obj instanceof List) {
                        List<Produto> produtos = (List<Produto>) obj;
                        produtos.forEach(p -> textArea.append("\nNome: " + p.getNome() + "\nQuantidade: " + p.getQuantidade() + "\n================="));
                    }
                });
            }
         
        } catch (IOException | ClassNotFoundException ex) {
           if (!(ex instanceof java.io.EOFException)) {
                System.out.println("======== Thread Finalizada ========");
            }
        }
    }
    
    public void close(){
        isClosed = true;
    }
    
}
