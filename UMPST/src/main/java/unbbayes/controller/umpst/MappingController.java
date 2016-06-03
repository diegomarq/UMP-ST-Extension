package unbbayes.controller.umpst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import unbbayes.io.umpst.FileSave;
import unbbayes.io.umpst.intermediatemtheory.FileBuildIntermediateMTheory;
import unbbayes.model.umpst.implementation.algorithm.DefineMapping;
import unbbayes.model.umpst.implementation.algorithm.FirstCriterionOfSelection;
import unbbayes.model.umpst.implementation.algorithm.MFragModel;
import unbbayes.model.umpst.implementation.algorithm.MTheoryModel;
import unbbayes.model.umpst.implementation.algorithm.SecondCriterionOfSelection;
import unbbayes.model.umpst.implementation.node.NodeContextModel;
import unbbayes.model.umpst.implementation.node.NodeInputModel;
import unbbayes.model.umpst.implementation.node.NodeObjectModel;
import unbbayes.model.umpst.implementation.node.NodeResidentModel;
import unbbayes.model.umpst.project.UMPSTProject;

/**
 * @author Diego Marques
 *
 */
public class MappingController {
	
	private UMPSTProject umsptProject;
	private MTheoryModel mtheory;
	
	// Run Mode	
	private DefineMapping defineMapping;
	private FirstCriterionOfSelection firstCriterion;
	private SecondCriterionOfSelection secondCriterion;
	
	private Controller controller; 
	private ResourceBundle resource = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.gui.umpst.resources.Resources.class.getName());
	
	public MappingController (UMPSTProject umpstProject) {
		
		this.umsptProject = umpstProject;
		
		mtheory = new MTheoryModel(umpstProject.getModelName(), umpstProject.getAuthorModel());
		
		defineMapping = new DefineMapping(umpstProject, this);
		firstCriterion = new FirstCriterionOfSelection(umpstProject, this);
//		secondCriterion = new SecondCriterionOfSelection(umpstProject, this);
//		
//		umpstProject.setMtheory(mtheory);
		
//		testMTheory();
	}
	
	/**
	 * MTHEORY DEBBUG METHOD
	 */
	public void testMTheory() {
		File newFile = null;
		FileBuildIntermediateMTheory file = new FileBuildIntermediateMTheory();

		JFileChooser fc =  new JFileChooser(); 
		fc.setCurrentDirectory (new File ("."));

		int res = fc.showSaveDialog(null);

		if(res == JFileChooser.APPROVE_OPTION){
			newFile = fc.getSelectedFile();
		}

		if (newFile!=null)	{
			try {
				controller = Controller.getInstance(null); 
				
				file.buildIntermediateMTheory(newFile, umsptProject);
				controller.showSucessMessageDialog(resource.getString("msSaveSuccessfull"));
			} catch (FileNotFoundException e1) {
				controller.showErrorMessageDialog(resource.getString("erFileNotFound")); 
				e1.printStackTrace();
			} catch (IOException e2) {
				controller.showErrorMessageDialog(resource.getString("erSaveFatal")); 
				e2.printStackTrace();
			}
		}
		else {
			controller.showErrorMessageDialog(resource.getString("erSaveFatal")); 
		}
	}
	
	public void updateResidentNodeInMFrag(String idMFrag, NodeResidentModel node) {
		mtheory.updateResidentNodeInMFrag(idMFrag, node);
	}
	
	public void updateInputNodeInMFrag(String idMFrag, NodeInputModel node) {
		mtheory.updateInputNodeInMFrag(idMFrag, node);
	}
	
	/**
	 * Add node input in a specific MFrag.
	 * @param idMFrag
	 * @param node
	 */
	public void addInputNodeInMFrag(String idMFrag, NodeInputModel node) {
		mtheory.addInputNodeInMFrag(idMFrag, node);
	}
	
	/**
	 * Add node context in a specific MFrag.
	 * @param idMFrag
	 * @param node
	 */
	public void addContextNodeInMFrag(String idMFrag, NodeContextModel node) {
		mtheory.addContextNodeInMFrag(idMFrag, node);
	}
	
	/**
	 * Add node resident in a specific MFrag.
	 * @param idMFrag
	 * @param node
	 */
	public void addResidentNodeInMFrag(String idMFrag, NodeResidentModel node) {
		mtheory.addResidentNodeInMFrag(idMFrag, node);
	}
	
	/**
	 * Add node not defined in a specific MFrag.
	 * @param idMfrag
	 * @param node
	 */
	public void addNotDefinedNodeInMFrag(String idMFrag, NodeObjectModel node) {
		mtheory.addNotDefinedNodeInMFrag(idMFrag, node);
	}

	/**
	 * @return the mtheory
	 */
	public MTheoryModel getMTheory() {
		return mtheory;
	}

	/**
	 * @param mtheory the mtheory to set
	 */
	public void setMTheory(MTheoryModel mtheory) {
		this.mtheory = mtheory;
	}

	/**
	 * @return the defineMapping
	 */
	public DefineMapping getDefineMapping() {
		return defineMapping;
	}

	/**
	 * @param defineMapping the defineMapping to set
	 */
	public void setDefineMapping(DefineMapping defineMapping) {
		this.defineMapping = defineMapping;
	}	

}
