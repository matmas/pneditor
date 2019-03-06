package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.PNEditor;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.Marking;
import org.pneditor.util.GraphicsTools;
import com.sun.glass.events.KeyEvent;
import org.pneditor.editor.commands.FireTransitionCommand;
import org.pneditor.petrinet.Transition;

/**
 * FireAction controller
 * @author goudiaby
 *
 */
@SuppressWarnings("serial")
public class FireAction extends AbstractAction {

	private Root root;

	private int _i=1;
	public FireAction(Root root2, int i) {
		// TODO Auto-generated constructor stub
		this.root = root2;

		switch(i)
		{
		
		case 0:
			
				String nameO = "click here to activate fire N token(s)";
				putValue(NAME , nameO);
				putValue(SMALL_ICON , GraphicsTools.getIcon("pneditor/play.gif"));
				putValue(SHORT_DESCRIPTION , nameO);
				putValue(MNEMONIC_KEY, KeyEvent.VK_T);
				setEnabled(false);
				_i=i;
				break;
		}	
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
        Marking initialMarking = PNEditor.getRoot().getCurrentMarking();
        root.getClipboard().setContents(root.getSelectedElementsWithClickedElement() , root.getDocument().petriNet);
        
        if (root.getClickedElement() != null) {
        	if (root.getClickedElement() instanceof Transition ) {
        		Transition transition = (Transition) root.getClickedElement();
       		String newLabel = JOptionPane.showInputDialog(root.getParentFrame(), "insert a positive number:", _i);
        		try {
       		    int n  = Integer.parseInt(newLabel);
         		if(n>0) {
	 	            root.getUndoManager().executeCommand(new FireTransitionCommand(transition , initialMarking, n)) ;}
				else if(n<0){
					Object[] options = {"OK"};
					int show = JOptionPane.showOptionDialog(null,"You can only insert positive numbers", "Title", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				
				  }
        		}catch(NumberFormatException eee) {
        			JOptionPane.showMessageDialog(null,"Characters are not allow.\n Please insert a positive integer number ");
        		}
        	}
 //       	
         }

		
	}
	
	

}
