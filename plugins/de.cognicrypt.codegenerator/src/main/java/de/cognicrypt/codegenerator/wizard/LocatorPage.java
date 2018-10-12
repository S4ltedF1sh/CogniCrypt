package de.cognicrypt.codegenerator.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.misc.ContainerContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

public class LocatorPage extends WizardPage {

	protected LocatorPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {

		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(parent, SWT.WRAP);
		label.setText(
			"Please select the file CogniCrypt should generate code into. You may also select a package or \nproject. In this case, CogniCrypt will generate a new Java source file within the selected resource.");
		label.setFont(this.getFont());

		DrillDownComposite drillDown = new DrillDownComposite(parent, SWT.BORDER);
		GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
		spec.widthHint = 320;
		spec.heightHint = 300;
		drillDown.setLayoutData(spec);

		// Create tree viewer inside drill down.
		TreeViewer treeViewer = new TreeViewer(drillDown, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDown.setChildTree(treeViewer);
		ContainerContentProvider cp = new ContainerContentProvider();
		cp.showClosedProjects(true);
		
		Composite selResGroup = new Composite(parent, SWT.BOTTOM);
		
		GridLayout selReslayout = new GridLayout();
		selReslayout.marginWidth = 0;
		selResGroup.setLayout(layout);
		selResGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label selResLabel = new Label(selResGroup, SWT.WRAP);
		selResLabel.setText("Selected Resource: ");
		selResLabel.setFont(this.getFont());
		
		Text containerNameField = new Text(selResGroup, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 240;
		containerNameField.setLayoutData(gd);
//		containerNameField.addListener(SWT.Modify, listener);
		containerNameField.setFont(this.getFont());

		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.setUseHashlookup(true);
		treeViewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = event.getStructuredSelection();
			containerSelectionChanged(selection.getFirstElement(), containerNameField); // allow null
		});
		treeViewer.addDoubleClickListener(event -> {
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection) {
				Object item = ((IStructuredSelection) selection).getFirstElement();
				if (item == null) {
					return;
				}
				if (treeViewer.getExpandedState(item)) {
					treeViewer.collapseToLevel(item, 1);
				} else {
					treeViewer.expandToLevel(item, 1);
				}
			}
		});

		// This has to be done after the viewer has been laid out
		treeViewer.setInput(ResourcesPlugin.getWorkspace());

		setControl(parent);

	}

	public void containerSelectionChanged(Object object, Text containerNameField) {
		//		selectedContainer = container;
		String text = "";
		if (object instanceof IContainer) {
			text = TextProcessor.process(((IContainer)object).getFullPath().makeRelative().toString());
		} else if (object instanceof IFile) {
			text = ((IFile) object).getFullPath().makeRelative().toString();
		}
		containerNameField.setText(text);
		containerNameField.setToolTipText(text);
	}

}
