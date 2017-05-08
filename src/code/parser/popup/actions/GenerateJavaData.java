package code.parser.popup.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import code.parser.dialog.FileSaveDialog;
import code.parser.util.ASTUtil;
import code.parser.util.UIUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * GenerateJavaData
 * 
 * @author dinghn
 *
 */
public class GenerateJavaData implements IObjectActionDelegate {

	private Shell shell;
	private Object[] selects;

	/**
	 * Constructor for GenerateJavaData.
	 */
	public GenerateJavaData() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selects == null) {
			return;
		}
		try {
			if (!(selects[0] instanceof IFile))
				return;
			IProject project = ((IFile) selects[0]).getProject();
			String path = new FileSaveDialog(shell,
					"please select the file to save",
					"code.parser.result.json", project.getLocation()
							.toOSString(), new String[] { "*.json" },
					new String[] { "JSON文件 (*.json)" }, true).open();
			if (StringUtils.isEmpty(path))
				return;

			JSONArray array = new JSONArray();
			for (Object object : selects) {
				if (!(object instanceof IFile)) {
					continue;
				}
				IFile ifile = (IFile) object;
				JSONObject parse = ASTUtil.parse(FileUtils
						.readFileToString(ifile.getLocation().toFile()));
				array.add(parse);
			}

			File outFile = new File(path);
			outFile.createNewFile();
			FileUtils.writeStringToFile(outFile, array.toJSONString());
			UIUtil.refresh(project, outFile);
			MessageDialog.openInformation(shell, "Parser",
					"Generate java data was executed.");
		} catch (Exception e) {
			MessageDialog.openError(shell, "Parser",
					"Generate java data write Failed.\n" + e.toString());
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection ss = (IStructuredSelection) selection;
		selects = ss.toArray();
	}

}
