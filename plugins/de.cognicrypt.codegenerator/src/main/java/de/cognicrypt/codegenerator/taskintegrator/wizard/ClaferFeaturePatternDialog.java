package de.cognicrypt.codegenerator.taskintegrator.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositePatternEnum;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositePatternOrderedEnum;

public class ClaferFeaturePatternDialog extends Dialog {

	private Composite compositePatternDetails;
	private Combo comboPattern;
	
	private String patternName;
	private ClaferModel resultModel;

	public ClaferFeaturePatternDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);

		patternName = "";
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		// restrict resizing the dialog below a minimum
		getShell().setMinimumSize(520, 600);

		Label lblPattern = new Label(container, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblPattern.setText("Pattern");

		comboPattern = new Combo(container, SWT.NONE);
		comboPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboPattern.add("Enumeration");
		comboPattern.add("Ordered Enumeration");
		comboPattern.select(0);

		comboPattern.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updatePatternDetailsComposite();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblName.setText("Name");

		Text txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				patternName = txtName.getText();
				
			}
		});

		compositePatternDetails = new Composite(container, SWT.NONE);
		compositePatternDetails.setLayout(new GridLayout(1, false));
		compositePatternDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		updatePatternDetailsComposite();

		return container;
	}

	private void updatePatternDetailsComposite() {
		String selectedPattern = comboPattern.getText();

		// TODO consider outsourcing patterns into a simple Map<String, PatternComposite>,
		// where PatternComposite is an interface providing the appropriate getResult method
		if (compositePatternDetails.getChildren().length > 0) {
			compositePatternDetails.getChildren()[0].dispose();
		}
		if (selectedPattern.equals("Enumeration")) {
			CompositePatternEnum compositePatternEnum = new CompositePatternEnum(compositePatternDetails);
			compositePatternEnum.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		} else if (selectedPattern.equals("Ordered Enumeration")) {
			CompositePatternOrderedEnum compositePatternOrderedEnum = new CompositePatternOrderedEnum(compositePatternDetails);
			compositePatternOrderedEnum.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		compositePatternDetails.layout();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 700);
	}

	@Override
	protected void okPressed() {
		saveResultModel();
		super.okPressed();
	}

	private void saveResultModel() {
		resultModel = new ClaferModel();

		if (compositePatternDetails.getChildren().length > 0 && compositePatternDetails.getChildren()[0] instanceof CompositePatternEnum) {
			CompositePatternEnum compositePatternEnum = ((CompositePatternEnum) compositePatternDetails.getChildren()[0]);

			resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, patternName, "Enum"));
			for (StringBuilder sb : compositePatternEnum.getElements()) {
				resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, sb.toString(), patternName));
			}
		} else if (compositePatternDetails.getChildren().length > 0 && compositePatternDetails.getChildren()[0] instanceof CompositePatternOrderedEnum) {
			CompositePatternOrderedEnum compositePatternOrderedEnum = ((CompositePatternOrderedEnum) compositePatternDetails.getChildren()[0]);
			resultModel.add(new ClaferFeature(Constants.FeatureType.ABSTRACT, patternName, "Enum -> integer"));
			for (int i = 0; i < compositePatternOrderedEnum.getElements().size(); i++) {
				String str = compositePatternOrderedEnum.getElements().get(i);
				resultModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, str.toString(), patternName + " = " + (i + 1)));
			}
		}
	}

	public ClaferModel getResultModel() {
		return resultModel;
	}

}
