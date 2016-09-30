/**
 * 
 */
package com.lightBoard.controls;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * @author Moham
 *
 */
public class DocFilter extends DocumentFilter
{
	private Class format;

	public DocFilter(Class inputType)
	{
		format = inputType;
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException 
	{

		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.insert(offset, string);

		if (testInputFOrmat(sb.toString())) {
			super.insertString(fb, offset, string, attr);
		} else {
			// warn the user and don't allow the insert
		}
	}

	private boolean testInputFOrmat(String text) {
		try {
			if (format.equals(Float.class))
			{
				System.out.println(Float.class.getName());
				Float.parseFloat(text);
			}
			else if (format.equals(Double.class))
			{
				System.out.println(Double.class.getName());
				Double.parseDouble(text);
			}
			else if (format.equals(Integer.class))
			{
				System.out.println(Integer.class.getName());
				Integer.parseInt(text);
			}
			else throw new IllegalStateException(format.getName() + " not supported");

			return true;
		} 
		catch (NumberFormatException e) {
			System.out.println(text+ " not supported - Number format exception");
			return false;
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {

		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		if (testInputFOrmat(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
		} else {
			// warn the user and don't allow the insert
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);
		
		if (sb.length() == 0)
			sb.append(0);
		
		if (testInputFOrmat(sb.toString())) {
			super.remove(fb, offset, length);
		} else {
			// warn the user and don't allow the insert
		}
	}
}
