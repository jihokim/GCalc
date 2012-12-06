/**
 * Juu Programming Language 
 * Copyright (C) 2010 Jiho Kim
 * 
 * This file is part of Juu.
 * 
 * Juu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Juu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Juu. If not, see <http://www.gnu.org/licenses/>.
 */

package net.gcalc.juu.demo;

import java.io.OutputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class TextComponentOutputStream extends OutputStream
{
	private byte[] buffer = new byte[1000];
	private int size = 0;

	private JTextComponent component;
	private Document doc;
	private int textLengthThreshhold = 100000;



	public TextComponentOutputStream(JTextComponent c) {
		this.component = c;
		this.doc = component.getDocument();
	}

	public synchronized void write(int b) {
		buffer[size] = (byte) b;
		size++;
		if (size==buffer.length || b=='\n') {
			flush();
		}
	}

	public synchronized void flush() {
		if (size>0) {
			String string = new String(buffer, 0, size);
			size = 0;

			int len = doc.getLength();

			if (len>textLengthThreshhold) { 
				String text = "";
				try {
					text = component.getText(len-textLengthThreshhold/2, textLengthThreshhold/2);
				}
				catch (BadLocationException e) {}

				component.setText(text);
			}


			component.select(len,len);
			component.replaceSelection(string);
		}
	}
}

