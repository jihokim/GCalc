/*  
GCalc 2.0 - Graphing calculator applet
Copyright (C) 2001 Jiho Kim

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Email: jiho@gcalcul.us
Web: http://gcalcul.us

*/


import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;


/**
 * Dialog for manipulating a PostfixList
 */
public class PostfixListDialog extends JiDialog implements ActionListener, ItemListener
{
    private PostfixList pfl;
    private List list;
    private Button remove, modify, done;

    private boolean hasFunction;

    /**
     * Dialog Constructor
     */
    public PostfixListDialog(Frame f, PostfixList pfl)
    {
	super(f, "Expression List", true);
	this.pfl = pfl;

	list = new List();
	list.setBackground(Color.white);
	list.setFont(new Font("Monospaced", Font.PLAIN, 12));

	this.add("Center", list);

	Panel south = new Panel(new GridLayout(1,3));

	remove = new Button("Remove");
	modify= new Button("Modify");
	done = new Button("Done");

	remove.addActionListener(this);
	modify.addActionListener(this);
	done.addActionListener(this);
	list.addActionListener(this);
	list.addItemListener(this);

	south.add(remove);
	south.add(modify);
	south.add(done);

	this.add("South", south);

	updateAWTList();

	remove.setEnabled(false);
	modify.setEnabled(false);
	done.setEnabled(true);

	pack();
	//	setResizable(false);
	this.addWindowListener(this);
	setVisible(true);
    }

    /**
     * Updates the list given the current state of the PostfixList
     */
    private void updateAWTList()
    {
	list.removeAll();
	int k = pfl.List.size();
	int c = 0;

	Postfix pf;
	for (int i=0; i<k; i++) {
	    pf = pfl.get(i);
	    if (pf.infixString().length()>0) {
		list.add(""+i+". "+pf.infixString(), i);
		c++;
	    }

	}

	hasFunction = (c>0);

	if (! hasFunction) {
	    list.add("No Functions Graphed",980980);
	}

	pack();
    }

    /**
     * Handles ActionEvents on buttons in dialog.
     */
    public void actionPerformed(ActionEvent event)
    {

	Object src = event.getSource();

	if (src==done) {
	    dispose();
	}
	else if (src==remove) {
	    int p = list.getSelectedIndex();

	    if (p!=-1) {
		StringTokenizer st = new StringTokenizer(list.getSelectedItem(), ". ");
		int idx = Integer.parseInt(st.nextToken());

		pfl.remove(idx);

		updateAWTList();

	    }
	}
	else if (src==modify) {
	    handleModify();
	}
	else if (src==list) {
	    handleModify();
	}

	remove.setEnabled(false);
	modify.setEnabled(false);

    }

    private void handleModify()
    {
	int p = list.getSelectedIndex();
	if (p!=-1) {
	    StringTokenizer st = new StringTokenizer(list.getSelectedItem(), ". ");
	    int idx = Integer.parseInt(st.nextToken());
	    new FunctionModifyDialog(this, pfl, idx);
	}
	updateAWTList();
    }

    /**
     * Handles ItemEvent on the List in dialog.
     */
    public void itemStateChanged(ItemEvent event)
    {
	Object src = event.getSource();

	if (src==list) {
	    boolean b = (event.getStateChange()==ItemEvent.SELECTED && hasFunction);
	    remove.setEnabled(b);
	    modify.setEnabled(b);

	}
    }
}

