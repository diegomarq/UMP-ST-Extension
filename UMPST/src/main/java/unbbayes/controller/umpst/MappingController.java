package unbbayes.controller.umpst;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.osgi.framework.debug.Debug;

import unbbayes.io.mebn.MebnIO;
import unbbayes.io.mebn.exceptions.IOMebnException;
import unbbayes.io.mebn.owlapi.OWLAPICompatiblePROWL2IO;
import unbbayes.model.umpst.entity.EntityModel;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.implementation.algorithm.FirstCriterionOfSelection;
import unbbayes.model.umpst.implementation.algorithm.SecondCriterionOfSelection;
import unbbayes.model.umpst.implementation.node.ContextNodeExtension;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.MebnExtension;
import unbbayes.model.umpst.implementation.node.OrdinaryVariableExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.entity.ObjectEntity;
import unbbayes.prs.mebn.entity.ObjectEntityContainer;
import unbbayes.prs.mebn.entity.Type;
import unbbayes.prs.mebn.entity.exception.TypeException;

/**
 * @author Diego Marques
 */
public class MappingController {
	
	private UMPSTProject umsptProject;
		
	private MebnExtension mebnExtension; 
	private FirstCriterionOfSelection firstCriterion;
	private SecondCriterionOfSelection secondCriterion;
	
	private Controller controller; 
	private ResourceBundle resource = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.gui.umpst.resources.Resources.class.getName());

	private DefaultTreeModel treeModel;
	private ObjectEntityContainer entityContainer;
	private ObjectEntity rootObjectEntity;
	
	public MappingController (UMPSTProject umpstProject) {
		
		this.umsptProject = umpstProject;
		
		createMebnExtension();
		Debug.println("-----------------");
		Debug.println("MTheoryExtension created: " + getMebnExtension().getName());
		
		createAllMFragExtension();
		Debug.println("MFrags created: " + getMebnExtension().getMFragExtensionList().size());
		
		//Entity
//		treeModel = mebnExtension.getObjectEntityContainer().getEntityTreeModel();
		entityContainer = mebnExtension.getObjectEntityContainer();
		rootObjectEntity = entityContainer.getRootObjectEntity();
				
		createAllEntityType();
		Debug.println("All entities defined in MEBN");
		System.out.println(entityContainer.getChildsOfObjectEntity(rootObjectEntity).size());
		
		createUndefinedNodes();
		
		firstCriterion = new FirstCriterionOfSelection(umpstProject, this);
		secondCriterion = new SecondCriterionOfSelection(umpstProject, this);
		
		testMTheory();
	}
	
	/**
	 * MTHEORY DEBBUG METHOD
	 */
	public void testMTheory() {
		File newFile = null;
		
		//Save MTheory in another format .model
//		FileBuildIntermediateMTheory file = new FileBuildIntermediateMTheory();

		// set current directory
		JFileChooser fc =  new JFileChooser(); 
		fc.setCurrentDirectory (new File ("."));

		int res = fc.showSaveDialog(null);
		if(res == JFileChooser.APPROVE_OPTION){
			newFile = fc.getSelectedFile();
		}

		if (newFile!=null)	{
			MultiEntityBayesianNetwork mebn = getMebnExtension();
			
			MebnIO saveFile = OWLAPICompatiblePROWL2IO.newInstance();
//			FileBuildPROWL2 saveFile  = new FileBuildPROWL2(newFile, mebn);			
//			UbfIO2 saveFile = new UbfIO2();
			try {
				controller = Controller.getInstance(null);
				
				saveFile.saveMebn(newFile, mebn);
//				file.buildIntermediateMTheory(newFile, umsptProject);
				
				controller.showSucessMessageDialog(resource.getString("msSaveSuccessfull"));
			} catch (IOMebnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			controller.showErrorMessageDialog(resource.getString("erSaveFatal")); 
		}
	}
	
	public ObjectEntity createObjectEntity(String name, 
			ObjectEntity parentObjectEntity) throws TypeException{		
		
		ObjectEntity objectEntity = entityContainer.createObjectEntity(name,parentObjectEntity);
		mebnExtension.getNamesUsed().add(name);
		
		return objectEntity;
	}
	
	
	/**
	 * Keep all entities from UMP-ST and create entities related to Mebn structure
	 * @throws TypeException
	 */
	public void createAllEntityType() {
		
		Map<String, EntityModel> mapEntity = umsptProject.getMapEntity();
		Set<String> keys = mapEntity.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);	
		
		for (String key : sortedKeys) {
			EntityModel entity = mapEntity.get(key);
			
			try {
				ObjectEntity objectEntity = createObjectEntity(entity.getName(), rootObjectEntity);
			} catch (TypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Maps ordinary variables elements present in rule
	 * as context nodes.
	 * @param rule
	 * @param group
	 */
	public void addOrdinaryVariable(RuleModel rule, MFragExtension mfragExtension) {
		
		// Add OrdinaryVariable as context node.
		for (int i = 0; i < rule.getOrdinaryVariableList().size(); i++) {
			OrdinaryVariableModel ovModel = rule.getOrdinaryVariableList().get(i);
			String name = ovModel.getVariable();
			Type type = mebnExtension.getTypeContainer().getDefaultType();
			
			OrdinaryVariableExtension ovExtension = new OrdinaryVariableExtension(
					name, type, mfragExtension, ovModel);
			mfragExtension.addOrdinaryVariableExtension(ovExtension);
		}
	}
	
	/**
	 * Maps necessary condition elements present in rule
	 * as context nodes.
	 * @param rule
	 * @param group
	 */		
	public void addNecessaryCondition(RuleModel rule, MFragExtension mfragExtension) {
		
		// Add NecessaryCondtion as context node.
		for (int i = 0; i < rule.getNecessaryConditionList().size(); i++) {
			
			// set valid name
			String name = null;
			while (name == null){
				name = resource.getString("contextNodeName") + mebnExtension.getContextNodeNum(); 
				if(mebnExtension.getNamesUsed().contains(name)){
					name = null; 
					mebnExtension.plusContextNodeNul(); 
				}
			}
			
			NecessaryConditionVariableModel nc = rule.getNecessaryConditionList().get(i);			
			ContextNodeExtension contextNodeExtension = new ContextNodeExtension(mfragExtension, nc, name);
			mfragExtension.addContextNode(contextNodeExtension);
			
//			System.out.println("LALALA - "+node.getName());
			
		}
	}
	
	public void addGroupPointer(MFragExtension mfragExtension, GroupModel groupPointer) {
		mfragExtension.setGroupPointer(groupPointer);
	}
	
	/**
	 * TODO create a new method to map attributes as UndefinedNodes
	 */
	
	public void createUndefinedNodes() {
		List<MFragExtension> listMFragExtension = getMebnExtension().getMFragExtensionList();
		for (MFragExtension mfragExtension : listMFragExtension) {
			
			GroupModel group = mfragExtension.getGroupPointer();
			List<RelationshipModel> relationshipList = group.getBacktrackingRelationship();
			for(RelationshipModel relationship : relationshipList) {
				
				String name = relationship.getName();
				UndefinedNode node = new UndefinedNode(name, mfragExtension);
				node.setRelationshipPointer(relationship);
				mfragExtension.addUndefinedNode(node);
			}
		}
	}
	
	/**
	 * Create all MFrags from set of groups.
	 */
	public void createAllMFragExtension() {
		Map<String, GroupModel> mapGroup = umsptProject.getMapGroups();
		Set<String> keys = mapGroup.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);	
		
		for (String key : sortedKeys) {			
			GroupModel group = mapGroup.get(key);
			
			String id = group.getId();
			String name = group.getName();
			name = name.replace(" ", "_");
			System.out.println("LALALA - "+name);
			
			MFragExtension mfragExtension = new MFragExtension(name, mebnExtension);
//				MFragModel mfrag = new MFragModel(id, name);
			// Adds MFrags in MTheory			
			addGroupPointer(mfragExtension, group);
			getMebnExtension().addMFragExtension(mfragExtension);
		}
	}
	
	public void createMebnExtension() {
		setMebnExtension(new MebnExtension(umsptProject.getModelName()));
//		setDefineMebnExtension(new DefineMebn(umpstProject));
	}

	/**
	 * @return the mebnExtension
	 */
	public MebnExtension getMebnExtension() {
		return mebnExtension;
	}

	/**
	 * @param mebnExtension the mebnExtension to set
	 */
	public void setMebnExtension(MebnExtension mebnExtension) {
		this.mebnExtension = mebnExtension;
	}	

}
