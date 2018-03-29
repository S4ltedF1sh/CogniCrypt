package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;

public class QuestionDialog extends Dialog {

	public Text textQuestion;
	private String questionText;
	private String questionType;
	private Combo combo;
	private Text txtBoxHelptext;
	private Text textBoxTooltip;
	private CompositeToHoldSmallerUIElements compositeToHoldAnswers;
	private Question question;
	private Question questionDetails;
	int counter = 0;
	private String currentQuestionType = null;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public QuestionDialog(Shell parentShell) {
		this(parentShell, null, null, null);
	}

	public QuestionDialog(Shell parentShell, Question question, ClaferModel claferModel, ArrayList<Question> listOfAllQuestions) {
		super(parentShell);
		this.question = question;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		getShell().setMinimumSize(900, 430);
		

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		TabItem tbtmQuestion = new TabItem(tabFolder, SWT.NONE);
		tbtmQuestion.setText("Question");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmQuestion.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label lblQuestion = new Label(composite, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblQuestion.setText("Question");

		textQuestion = new Text(composite, SWT.BORDER);
		textQuestion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Answer type");

		combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.select(-1);
		combo.setItems(Constants.dropDown, Constants.textBox, Constants.radioButton);

		Label lblHelpText = new Label(composite, SWT.NONE);
		lblHelpText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblHelpText.setText("Describe the question");
		lblHelpText.setToolTipText("In order to help the user to understand the question give extra details about the question in the text box");

		txtBoxHelptext = new Text(composite, SWT.BORDER);
		txtBoxHelptext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblToolTip = new Label(composite, SWT.None);
		lblToolTip.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblToolTip.setText("Give tooltip");
		lblToolTip.setToolTipText("Give help text to be displayed when user hover over the text box");
		//visible only if the question type is text
		lblToolTip.setVisible(false);

		textBoxTooltip = new Text(composite, SWT.BORDER);
		textBoxTooltip.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBoxTooltip.setVisible(false);

		Button btnAddAnswer = new Button(composite, SWT.None);
		btnAddAnswer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnAddAnswer.setText("Add Answer");
		//Visibility depends on question type
		btnAddAnswer.setVisible(false);
		boolean showRemoveButton = true;
		compositeToHoldAnswers = new CompositeToHoldSmallerUIElements(composite, SWT.NONE, null, showRemoveButton, null);
		GridData gd_compositeToHoldAnswers = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_compositeToHoldAnswers.heightHint = 135;
		gd_compositeToHoldAnswers.widthHint = 890;
		compositeToHoldAnswers.setLayoutData(gd_compositeToHoldAnswers);
		compositeToHoldAnswers.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeToHoldAnswers.setVisible(false);
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Answer tempAnswer = new Answer();
				compositeToHoldAnswers.getListOfAllAnswer().add(tempAnswer);
				compositeToHoldAnswers.addAnswer(tempAnswer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}

		});
		currentQuestionType = combo.getText();
		
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				switch (combo.getText()) {
					case Constants.textBox:
						lblToolTip.setVisible(true);
						textBoxTooltip.setVisible(true);
						textBoxTooltip.setText("");
						btnAddAnswer.setVisible(false);
						compositeToHoldAnswers.setVisible(false);
						compositeToHoldAnswers.getListOfAllAnswer().clear();
						compositeToHoldAnswers.updateAnswerContainer();
						Answer emptyAnswer=new Answer();
						emptyAnswer.setDefaultAnswer(true);
						emptyAnswer.setValue("");
						compositeToHoldAnswers.getListOfAllAnswer().add(emptyAnswer);
						currentQuestionType = Constants.textBox;
						break;
					case Constants.dropDown:
						boolean comboSelected = combo.getText().equalsIgnoreCase(Constants.dropDown) ? true : false;
						btnAddAnswer.setVisible(comboSelected);
						lblToolTip.setVisible(false);
						textBoxTooltip.setText("");
						textBoxTooltip.setVisible(false);
						if (!currentQuestionType.equalsIgnoreCase(Constants.dropDown)) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = Constants.dropDown;
						}
						break;
					case Constants.radioButton:
						boolean buttonSelected = combo.getText().equalsIgnoreCase(Constants.radioButton) ? true : false;
						btnAddAnswer.setVisible(buttonSelected);
						lblToolTip.setVisible(false);
						textBoxTooltip.setText("");
						textBoxTooltip.setVisible(false);
						if (!currentQuestionType.equalsIgnoreCase(Constants.radioButton)) {
							compositeToHoldAnswers.getListOfAllAnswer().clear();
							compositeToHoldAnswers.updateAnswerContainer();
							compositeToHoldAnswers.setVisible(false);
							currentQuestionType = Constants.radioButton;
						}
						break;
					default:
						break;
				}
			}
		});

		if (question != null) {
			textQuestion.setText(question.getQuestionText());
			if (question.getElement().equals(Constants.GUIElements.combo)) {
				combo.setText(Constants.dropDown);
			} else if (question.getElement().equals(Constants.GUIElements.radio)) {
				combo.setText(Constants.radioButton);
			} else if (question.getElement().equals(Constants.GUIElements.text)) {
				combo.setText(Constants.textBox);

			}
			if (!question.getHelpText().isEmpty()) {
				txtBoxHelptext.setText(question.getHelpText());
			}
			if (!question.getTooltip().isEmpty()) {
				textBoxTooltip.setText(question.getTooltip());
			}

			for (Answer answer : question.getAnswers()) {
				compositeToHoldAnswers.getListOfAllAnswer().add(answer);
				compositeToHoldAnswers.addAnswer(answer, showRemoveButton);
				compositeToHoldAnswers.setVisible(true);
			}
			if (question.getElement().equals(Constants.GUIElements.text)) {
				textBoxTooltip.setText(question.getTooltip());
				compositeToHoldAnswers.setVisible(false);
			}

		}

		return container;
	}

	@Override
	protected void okPressed() {
		setQuestionDetails();
		super.okPressed();
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText() {
		textQuestion.setText(question.getQuestionText());
	}

	public String getquestionType() {
		return questionType;
	}

	public void setQuestionType(String type) {
		combo.setText(type);

	}

	//Saving question details
	public void setQuestionDetails() {
		Question questionDetails = new Question();
		questionDetails.setQuestionText(textQuestion.getText());
		setQuestionElement(questionDetails, combo.getText());
		if (!txtBoxHelptext.getText().isEmpty()) {
			questionDetails.setHelpText(txtBoxHelptext.getText());
		}
		//sets the tooltip for question type text 
		if (combo.getText().equalsIgnoreCase(Constants.textBox)) {
			if (!textBoxTooltip.getText().equalsIgnoreCase("")) {
				questionDetails.setTooltip(textBoxTooltip.getText());
			}
		}

		/**
		 * Executes only if the question type is not text this loop executes to delete empty text boxes in the question dialog
		 */
		if (!questionDetails.getElement().equals(Constants.GUIElements.text)) {
			for (int i = 0; i < compositeToHoldAnswers.getListOfAllAnswer().size(); i++) {
				if (Objects.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), null) || Objects
					.equals(compositeToHoldAnswers.getListOfAllAnswer().get(i).getValue(), "")) {
					compositeToHoldAnswers.deleteAnswer(compositeToHoldAnswers.getListOfAllAnswer().get(i));
					compositeToHoldAnswers.updateAnswerContainer();
					i--;
				}
			}
		}
		questionDetails.setAnswers(compositeToHoldAnswers.getListOfAllAnswer());
		checkQuestionHasDefaultAnswer(questionDetails);
		this.questionDetails = questionDetails;

	}

	/**
	 * sets the question element depending on the question type selected
	 * @param question
	 * @param element the value selected for the question type 
	 */
	private void setQuestionElement(Question question, String element) {
		/**
		 * case 1: if the the question type is selected as drop down then sets the element to combo
		 */
		if (element.equals(Constants.dropDown)) {
			question.setElement(Constants.GUIElements.combo);
		}
		/**
		 * case 2: sets the question element to text if the question type is text box
		 */
		else if (element.equals(Constants.textBox)) {
			question.setElement(Constants.GUIElements.text);
		}
		/**
		 * case 3: sets the question element to text if the question type is radio button
		 */
		else if (element.equals(Constants.radioButton)) {
			question.setElement(Constants.GUIElements.radio);
		}
	}

	/**
	 * checks if for the question default answer is selected or not if no answer is selected as default answer then the function sets the first answer as the default answer of the
	 * particular question
	 */
	public void checkQuestionHasDefaultAnswer(Question question) {
		boolean hasDefaultAnswer = false;
		for (Answer answer : question.getAnswers()) {
			if (answer.isDefaultAnswer()) {
				hasDefaultAnswer = true;
			}
		}
		if (!hasDefaultAnswer) {
			if (question.getAnswers().size() > 0) {
				question.getAnswers().get(0).setDefaultAnswer(true);
			}
		}
	}

	/**
	 * 
	 * @return the question
	 */
	public Question getQuestionDetails() {
		return this.questionDetails;
	}

	/**
	 * Create contents of the button bar.
	 * 
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
		return new Point(559, 351);
	}

}