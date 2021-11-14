/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.tools.data.wizard.dialogs;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.quakearts.tools.data.model.Parameter;
import com.quakearts.tools.data.model.WrapperClass;

import org.eclipse.swt.layout.GridLayout;

public class ParameterDialog extends Dialog {

	private WrapperClass wrapperClass;
	private Object[] callParameters;
	private Control[] textControls;
	private static final Pattern PROCPARAMSPATTERN = Pattern.compile("([0-9]+(\\.[0-9]*)?)|(['][^']+[']|[\"][^\"]+[\"])|(true|false|[nN][uU][lL][lL])");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ParameterDialog(Shell parentShell, WrapperClass wrapperClass) {
		super(parentShell);
		this.wrapperClass = wrapperClass;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout_1 = (GridLayout) composite.getLayout();
		gridLayout_1.verticalSpacing = 0;
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		gridLayout_1.horizontalSpacing = 0;
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(composite,SWT.BORDER|SWT.V_SCROLL);
		scrolledComposite.setLayout(new GridLayout(1,false));
		scrolledComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Composite parameterComposite = new Composite(scrolledComposite, SWT.NONE);
		parameterComposite.setLayout(new GridLayout());
		
		GridLayout gridLayout = (GridLayout) parameterComposite.getLayout();
		gridLayout.numColumns = 2;
		
		Label lblEnterParametersFor = new Label(parameterComposite, SWT.NONE);
		lblEnterParametersFor.setText("Enter Parameters for procedure :");
		new Label(parameterComposite, SWT.NONE);

		Collection<Parameter> parameters = wrapperClass.getProcedure().getParameters();
		callParameters = new Object[parameters.size()];
		textControls = new Control[parameters.size()];

		for(Parameter parameter:parameters){
			if(parameter.isOutputEnabled())
				continue;
				
			Label lblParameter = new Label(parameterComposite, SWT.NONE);
			lblParameter.setText(parameter.getVariableName());
			int position = parameter.getCallPosition()-(wrapperClass.getProcedure().isReturnCodeEnabled()?2:1);
			ParameterAdapter listener=new ParameterAdapter(position, parameter.getType());
			
			switch (parameter.getType()) {
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					DateTime parameterDateTime = new DateTime(parameterComposite, SWT.BORDER);
					parameterDateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					parameterDateTime.addFocusListener(listener);
					callParameters[position]=new Date();
					textControls[position] = parameterDateTime;
					break;
				case Types.BIT:
				case Types.BOOLEAN:
					Button btnParameterboolean = new Button(parameterComposite, SWT.CHECK);
					btnParameterboolean.setText("Set true");
					btnParameterboolean.addFocusListener(listener);
					callParameters[position]=Boolean.FALSE;
					textControls[position] = btnParameterboolean;
					break;
				default:
					Text parameterText = new Text(parameterComposite, SWT.BORDER);
					parameterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
					parameterText.addFocusListener(listener);
					textControls[position] = parameterText;
			}
		}
		Label lblPasteProcedure = new Label(parameterComposite, SWT.NONE);
		lblPasteProcedure.setText("Paste Procedure:");

		final Text pastedProcedureText = new Text(parameterComposite, SWT.BORDER|SWT.MULTI);
		GridData gd_pastedProcedureText = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
		gd_pastedProcedureText.heightHint = 100;
		pastedProcedureText.setLayoutData(gd_pastedProcedureText);
		new Label(parameterComposite, SWT.NONE);
		pastedProcedureText.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if(!pastedProcedureText.getText().trim().isEmpty()){
					Matcher matcher = PROCPARAMSPATTERN.matcher(pastedProcedureText.getText());
					ArrayList<String> paramsStrings = new ArrayList<String>();
					while(matcher.find()){
						String value =matcher.group();
						if(!value.isEmpty())
							paramsStrings.add(value);
					}
					if(paramsStrings.size()!=callParameters.length){
						MessageDialog.openError(getShell(),"Invalid parameter string", (paramsStrings.size()>callParameters.length?"More parameters than required":"Less parameters than required")+": "+paramsStrings.size());
						return;
					}
					Collection<Parameter> parameters = wrapperClass.getProcedure().getParameters();
					for(Parameter parameter:parameters){
						int position = parameter.getCallPosition()-(wrapperClass.getProcedure().isReturnCodeEnabled()?2:1);
						String value = paramsStrings.get(position);
						if(value.indexOf("\"")==0 ||value.indexOf("'")==0)
							value = value.substring(1,value.length()-1);
						else if(value.equalsIgnoreCase("NULL"))
							continue; //ignore null parameters
						
						Control control = textControls[position];
						switch (parameter.getType()) {
						case Types.DATE:
						case Types.TIME:
						case Types.TIMESTAMP:
							try {
								GregorianCalendar calendar = new GregorianCalendar();
								calendar.setTime(DATE_FORMAT.parse(value));
								callParameters[position] = calendar.getTime();
								if(control instanceof DateTime){
									DateTime dateTime = ((DateTime)control);
									dateTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
									dateTime.setMonth(calendar.get(Calendar.MONTH));
									dateTime.setYear(calendar.get(Calendar.YEAR));
								}
							} catch (ParseException e2) {
								MessageDialog.openError(getShell(),"Invalid data", "Parameter "+(position+1)+" must be a valid date string of pattern yyyyMMdd");
							}
							break;
						case Types.BIT:
							int bit = Integer.parseInt(value);
							callParameters[position] = Boolean.valueOf(bit==1);
							if(control instanceof Button)
								((Button)control).setSelection(bit==1);
							break;
						case Types.BOOLEAN:
							callParameters[position] = Boolean.valueOf(Boolean.parseBoolean(value));
							if(control instanceof Button)
								((Button)control).setSelection(Boolean.parseBoolean(value));
							break;
						case Types.INTEGER:
						case Types.SMALLINT:
						case Types.TINYINT:
							try{
								callParameters[position] = Integer.valueOf(Integer.parseInt(value));
							} catch (Exception e1) {
								MessageDialog.openError(getShell(),"Invalid data", "Parameter "+(position+1)+" must be a valid number");
								continue;
							}
							if(control instanceof Text)
								((Text)control).setText(value);
							break;
						case Types.BIGINT:
							try{
								callParameters[position] = Long.valueOf(Long.parseLong(value));
							} catch (Exception e1) {
								MessageDialog.openError(getShell(),"Invalid data", "Parameter "+(position+1)+" must be a valid number");
								continue;
							}
							if(control instanceof Text)
								((Text)control).setText(value);
							break;
						case Types.NUMERIC:
						case Types.DECIMAL:
						case Types.FLOAT:
						case Types.REAL:
							try{
								callParameters[position] = Double.valueOf(Double.parseDouble(value));
							} catch (Exception e1) {
								MessageDialog.openError(getShell(),"Invalid data", "Parameter "+(position+1)+" must be a valid number");
								continue;
							}
							if(control instanceof Text)
								((Text)control).setText(value);
							break;
						default:
							callParameters[position] = value;
							if(control instanceof Text)
								((Text)control).setText(value);
							break;
						}
					}
				}
			}
		});
		
		scrolledComposite.setContent(parameterComposite);
		scrolledComposite.setMinSize(parameterComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}
	
	public Object[] getCallParameters() {
		return callParameters;
	}

	private class ParameterAdapter extends FocusAdapter {
		private int parameterPosition;
		private int type;
		private ParameterAdapter(int parameterPosition, int type){
			this.parameterPosition=parameterPosition;
			this.type = type;
		}
		@Override
		public void focusLost(FocusEvent event) {
			Object obj= event.getSource();				
			Text text;
			switch (type) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				if(!(obj instanceof Text))
					return;
				text = (Text) obj;
				if(text.getText().isEmpty())
					return;
				
				try {
					callParameters[parameterPosition] = Integer.valueOf(Integer.parseInt(text.getText().trim()));
				} catch (Exception e) {
					MessageDialog.openError(getShell(),"Invalid data", "Parameter must be a valid integer");
					text.forceFocus();
					return;
				}
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				if(!(obj instanceof DateTime))
					return;
				DateTime dateTime = (DateTime) obj;
				try {
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());
					callParameters[parameterPosition] = calendar.getTime();
				} catch (Exception e) {
					MessageDialog.openError(getShell(),"Invalid data", "Parameter must be a valid integer");
					dateTime.forceFocus();
					return;
				}
				break;
			case Types.BIT:
			case Types.BOOLEAN:
				if(!(obj instanceof Button))
					return;
				Button button = (Button) obj;
				callParameters[parameterPosition] = Boolean.valueOf(button.getSelection());
				break;
			case Types.BIGINT:
				if(!(obj instanceof Text))
					return;
				text = (Text) obj;
				if(text.getText().isEmpty())
					return;

				try {
					callParameters[parameterPosition] = Long.valueOf(Long.parseLong(text.getText().trim()));
				} catch (Exception e) {
					MessageDialog.openError(getShell(),"Invalid data", "Parameter must be a valid integer");
					text.forceFocus();
					return;
				}
				break;
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.FLOAT:
			case Types.REAL:
				if(!(obj instanceof Text))
					return;
				text = (Text) obj;
				if(text.getText().isEmpty())
					return;

				try {
					callParameters[parameterPosition] = Double.valueOf(Double.parseDouble(text.getText().trim()));
				} catch (Exception e) {
					MessageDialog.openError(getShell(),"Invalid data", "Parameter must be a valid integer");
					text.forceFocus();
					return;
				}
				break;
			default:
				if(!(obj instanceof Text))
					return;
				callParameters[parameterPosition] = ((Text) obj).getText();
			}
		}
	}


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
