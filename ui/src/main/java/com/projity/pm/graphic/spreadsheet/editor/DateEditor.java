/*
The contents of this file are subject to the Common Public Attribution License 
Version 1.0 (the "License"); you may not use this file except in compliance with 
the License. You may obtain a copy of the License at 
http://www.projity.com/license . The License is based on the Mozilla Public 
License Version 1.1 but Sections 14 and 15 have been added to cover use of 
software over a computer network and provide for limited attribution for the 
Original Developer. In addition, Exhibit A has been modified to be consistent 
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
specific language governing rights and limitations under the License. The 
Original Code is OpenProj. The Original Developer is the Initial Developer and 
is Projity, Inc. All portions of the code written by Projity are Copyright (c) 
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the 
Projity End-User License Agreeement (the Projity License), in which case the 
provisions of the Projity License are applicable instead of those above. If you 
wish to allow use of your version of this file only under the terms of the 
Projity License and not to allow others to use your version of this file under 
the CPAL, indicate your decision by deleting the provisions above and replace 
them with the notice and other provisions required by the Projity  License. If 
you do not delete the provisions above, a recipient may use your version of this 
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices 
in Exhibits A and B of the license at http://www.projity.com/license. You should 
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007 
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj, 
an open source solution from Projity. Attribution URL: http://www.projity.com 
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with 
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined 
in the CPAL as a work which combines Covered Code or portions thereof with code 
not governed by the terms of the CPAL. However, in addition to the other notice 
obligations, all copies of the Covered Code in Executable and Source Code form 
distributed must, as a form of attribution of the original author, include on 
each user interface screen the "OpenProj" logo visible to all users.  The 
OpenProj logo should be located horizontally aligned with the menu bar and left 
justified on the top left of the screen adjacent to the File menu.  The logo 
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it 
must direct them back to http://www.projity.com.  
*/
package com.projity.pm.graphic.spreadsheet.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.table.DateFieldTableEditor;

import com.projity.field.Field;
import com.projity.field.FieldConverter;
import com.projity.field.FieldParseException;
import com.projity.options.CalendarOption;
import com.projity.options.EditOption;
import com.projity.pm.graphic.frames.GraphicManager;
import com.projity.pm.graphic.spreadsheet.SpreadSheetModel;
import com.projity.strings.Messages;
import com.projity.util.Alert;
import com.projity.util.DateTime;

public class DateEditor extends DateFieldTableEditor {
	protected ExtDateField dateField;
	private Date initialValue = null;
	public DateEditor() {
	}
	public static class ExtDateField extends DateField implements KeyboardFocusable {
		private boolean nullable = false;
		public ExtDateField(DateFormat df) {
			super(df);
			addMouseListener();
		}
		public ExtDateField() {
			addMouseListener();
		}
		private void addMouseListener() {
			getTextField().addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2)
						GraphicManager.getInstance(ExtDateField.this).doInformationDialog(false);
				}
			});
			
		}
		protected boolean processKeyBinding(KeyStroke arg0, KeyEvent arg1, int arg2, boolean arg3) {
			if (Character.isDefined(arg0.getKeyChar())) {
				getTextField().dispatchEvent(arg1);
				return true; // stop routing
			}
			return super.processKeyBinding(arg0, arg1, arg2, arg3);
		}

		public void requestFocus() { // override default needed otherwise key handling is wrong (backspace, arrows
			getTextField().requestFocus();
		}

		JTextField getTextField() { // convenience method
			return (JTextField) getFormattedTextField();
		}
		
		public void selectAll(boolean keyboard) { // convenience method
			if (keyboard) { // if user typed something
				getTextField().selectAll();
			} else { // select later because popup drawing erases selection
				//TODO there is currently a bug in that if the cell is the active cell and you click to edit, the next is not getting selected
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getTextField().selectAll();
					}});
			}
		}
		public String toString() {
			return getTextField().getText();
		}
		public boolean isNullable() {
			return nullable;
		}
		public void setNullable(boolean nullable) {
			this.nullable = nullable;
		}

	}
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int col) {
		Field field = ((SpreadSheetModel)table.getModel()).getFieldInColumn(col+1);
		DateFormat format;
		if (field.isDateOnly())
			format = EditOption.getInstance().getShortDateFormat();
		else
			format = EditOption.getInstance().getDateFormat();

    	dateField = new ExtDateField(format);
        dateField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if (value == null) {
        	long date = DateTime.midnightToday();
    		if (field.isStartValue())
    			date = CalendarOption.getInstance().makeValidStart(date, true);
    		else if (field.isEndValue())
				date = CalendarOption.getInstance().makeValidEnd(date, true);
    		value = new Date(date);
        }
        dateField.setValue(value);
        dateField.getTextField().setSelectedTextColor(Color.WHITE);
        dateField.getTextField().setSelectionColor(Color.BLACK);
        dateField.setNullable(field.isAllowNull());
        
//        Date d = DateTime.gmtDate(new Date(DateTime.midnightToday()));
//        initialValue = (value == null) ? d : (Date) value;
        initialValue = (Date)value;
        return dateField;
    }
	@Override
	public Object getCellEditorValue() {
		return dateField.getValue();
	}
	@Override
	public boolean stopCellEditing() {
		
		String text = dateField.getFormattedTextField().getText();
		Date date;
		if (text.equals("")) { // empty text means Zero time
			if (initialValue == null) {
				cancelCellEditing();
				return true;
			}
			if (dateField.isNullable())
				dateField.setValue(null);

			return super.stopCellEditing();
		} else {
			try {
				date = (Date) FieldConverter.convert(text,Date.class,null);
			} catch (FieldParseException e) {
				cancelCellEditing();
				Alert.warn(Messages.getString("Message.invalidDate"),dateField);
				return true;
			}
		}
		if (date.equals(initialValue)) {
			cancelCellEditing();
			return true;
		}
			
		dateField.setValue(date);
		return super.stopCellEditing();
	}
}
