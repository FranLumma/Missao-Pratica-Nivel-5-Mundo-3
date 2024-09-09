package cadastroclient;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Cami
 */
public class SaidaFrame extends JDialog {
    public JTextArea texto;
    
    public SaidaFrame(){
        setBounds(100, 100, 400, 300);
        setModal(false);
        texto = new JTextArea(20, 50);
        add(new JScrollPane(texto));
    }
}
