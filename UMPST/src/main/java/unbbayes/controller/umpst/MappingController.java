package unbbayes.controller.umpst;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultTreeModel;

import org.eclipse.osgi.framework.debug.Debug;

import unbbayes.io.mebn.UbfIO2;
import unbbayes.io.mebn.exceptions.IOMebnException;
import unbbayes.io.mebn.owlapi.OWLAPICompatiblePROWL2IO;
import unbbayes.model.umpst.entity.EntityModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.algorithm.FirstCriterionOfSelection;
import unbbayes.model.umpst.implementation.algorithm.SecondCriterionOfSelection;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.entity.ObjectEntity;
import unbbayes.prs.mebn.entity.ObjectEntityContainer;
import unbbayes.prs.mebn.entity.Type;
import unbbayes.prs.mebn.entity.TypeContainer;
import unbbayes.prs.mebn.entity.exception.TypeException;

/**
 * @author Diego Marques
 */
public class MappingController {
	
	private UMPSTProject umsptProject;
	 
	private FirstCriterionOfSelection firstCriterion;
	private SecondCriterionOfSelection secondCriterion;
	
	private Controller controller; 
	private ResourceBundle resource = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.gui.umpst.resources.Resources.class.getName());

	private DefaultTreeModel treeModel;
	private ObjectEntityContainer entityContainer;
	private ObjectEntity rootObjectEntity;
	private TypeContainer typeContainer;
	
	private Map<String, MFragExtension> mapMFragExtension;
	
	public MappingController (UMPSTProject umpstProject) {
		
		this.umsptProject = umpstProject;		
		setMapMFragExtension(new HashMap<String, MFragExtension>());
		
		// temporary method to create mtheory
		MultiEntityBayesianNetwork tmpMebn = createMebnInstance(null);
		Debug.println("-----------------");
		Debug.println("Created temporary mtheory: " + tmpMebn.getName());
		
		// create model to define mebn elements
		MultiEntityBayesianNetwork mebn = createMebnInstance(tmpMebn);
		Debug.println("Created working version of mtheory: " + mebn.getName());
		
		//Entities
//		entityContainer = mebn.getObjectEntityContainer();
		rootObjectEntity = mebn.getObjectEntityContainer().getRootObjectEntity();
		typeContainer = mebn.getTypeContainer();
				
		createAllEntities(mebn);
		Debug.println("All entities defined in MEBN");
		
		// MFrags	
		createAllMFrags(mebn);
		Debug.println("MFrags created: " + getMapMFragExtension().size());
		
		// Ordinary Variables
		createAllOrdinaryVariables();
		Debug.println("Create all OVs from the rules");
		
		// Context Nodes
		createAllContextNodes();
		
		// Undefined Nodes
//		createUndefinedNodes(mebn);
//		
//		firstCriterion = new FirstCriterionOfSelection(umpstProject, this);
//		secondCriterion = new SecondCriterionOfSelection(umpstProject, this);
//		
		testMTheory(mebn);
	}
	
	/**
	 * MTHEORY DEBBUG METHOD
	 */
	public void testMTheory(MultiEntityBayesianNetwork mebn) {
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
			UbfIO2 ubf = new UbfIO2().getInstance();
			try {
				controller = Controller.getInstance(null);
				
				ubf.saveMebn(newFile, mebn);
//				file.buildIntermediateMTheory(newFile, umsptProject);
				
				for( Type type : mebn.getTypeContainer().getListOfTypes() ) {
					ObjectEntity objectEntity = mebn.getObjectEntityContainer().getObjectEntityByType(type);
					if (objectEntity == null) {
						System.out.println(" -- MappingController, entity null, type: "+type.getName());
					} else {
						System.out.println(objectEntity.getName() + " -- MappingController, type: "+type.getName());
					}
				}
				
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
	
	/**
	 * Method created from MEBNController and modified to search type related by the name
	 * @param mebn
	 * @return
	 * @author Shou Matsumoto
	 */
	public static Type getType(MultiEntityBayesianNetwork mebn, String name) {
		
		// extract some containers we'll be using to check types
		TypeContainer typeContainer = mebn.getTypeContainer();	// this is the main container of types
		ObjectEntityContainer objectEntityContainer = mebn.getObjectEntityContainer();	// this will be later to check hierarchy of entities
		if (typeContainer == null || objectEntityContainer == null) {
			return TypeContainer.getDefaultType();
		}
		
		// only consider types we know about
		Set<Type> knownTypes = typeContainer.getListOfTypes();
		if (knownTypes == null) {
			return TypeContainer.getDefaultType();
		}
		
		// search for some reasonable type
		for (Type type : knownTypes) {
			
			// ignore invalid types
			if (type == null) {
				continue;
			}
			
			// ignore boolean, type label, and categorical at this point
			if (type.equals(typeContainer.typeBoolean)
					|| type.equals(typeContainer.typeCategoryLabel)
					|| type.equals(typeContainer.typeLabel)) {
				continue;
			}
			
			// check if this is a root type
			// TODO avoid using object entities to check for type hierarchy
			boolean isRoot = false;
			for (Object entity : type.getIsTypeOfList()) {
				if (entity instanceof ObjectEntity) {
					List<ObjectEntity> parents = objectEntityContainer.getParentsOfObjectEntity((ObjectEntity) entity);
					if (parents == null || parents.isEmpty()) {
						isRoot = true;
						break;
					} else if(((ObjectEntity) entity).getName().equals(name)){
						return type;
					}
				}
			}
			
			// do not return root types
			if (isRoot) {
				continue;
			}
		}
				
		// if nothing was found, use the default
		return TypeContainer.getDefaultType();
	}

	public MultiEntityBayesianNetwork createMebnInstance(
			MultiEntityBayesianNetwork tmpMebn) {
		
		if(tmpMebn != null) {
			File ubfFile = new File ("./ubfTmpFile.ubf");		
			UbfIO2 ubf = UbfIO2.getInstance();
			
			try {
				ubf.saveMebn(ubfFile, tmpMebn);
				
				ubf.setProwlIO(OWLAPICompatiblePROWL2IO.newInstance());		
				MultiEntityBayesianNetwork mebn = ubf.loadMebn(ubfFile);
				return mebn;
				
			} catch (IOMebnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			MultiEntityBayesianNetwork mebn = new MultiEntityBayesianNetwork(
					umsptProject.getModelName());
			return mebn;
		}
	}
	
	/**
	 * Keep all entities from UMP-ST and create entities related to Mebn structure
	 * @throws TypeException
	 */
	public void createAllEntities(MultiEntityBayesianNetwork mebn) {
		
		Map<String, EntityModel> mapEntity = umsptProject.getMapEntity();
		Set<String> keys = mapEntity.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);	
		
		for (String key : sortedKeys) {
			EntityModel entity = mapEntity.get(key);
			
			try {
				String name = entity.getName();		
				ObjectEntity objectEntity = mebn.getObjectEntityContainer().
						createObjectEntity(name,rootObjectEntity);
				mebn.getNamesUsed().add(name);
//				typeContainer.createType(name);
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
	public void createAllOrdinaryVariables() {
		Map<String, RuleModel> mapRule = umsptProject.getMapRules();
		Set<String> keys = mapRule.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String key : sortedKeys) {
			RuleModel rule = mapRule.get(key);
			
			if (rule.getFowardtrackingGroupList().size() <  2) {
				List<GroupModel> groupList = rule.getFowardtrackingGroupList();
				
				for (int i = 0; i < groupList.size(); i++) {
					GroupModel group = groupList.get(i);
					
					MFragExtension mfrag = mapMFragExtension.get(group.getId());
					mfrag.addAllOrdinaryVariables(rule);
				}				
				
			} else {
				System.err.println("Rule "+ rule.getId()+" related to more then one group");
			}
		}
		
//		// Add OrdinaryVariable as a context node
//		for (int i = 0; i < getMFragExtensionList().size(); i++) {
//			MFragExtension mfrag = getMFragExtensionList().get(i);
//			GroupModel groupRelated = mfrag.getGroupRelated();
//			List<RuleModel> ruleList = groupRelated.getBacktrackingRules();
//			mfrag.addAllOrdinaryVariables(ruleList);	
//		}
	}
	
	/**
	 * Maps necessary condition elements present in rule
	 * as context nodes.
	 * @param rule
	 * @param group
	 */		
	public void addNecessaryCondition(RuleModel rule, MFragExtension mfragExtension) {
		
		// Add NecessaryCondtion as context node.
//		for (int i = 0; i < rule.getNecessaryConditionList().size(); i++) {
//			
//			// set valid name
//			String name = null;
//			while (name == null){
//				name = resource.getString("contextNodeName") + mebnExtension.getContextNodeNum(); 
//				if(mebnExtension.getNamesUsed().contains(name)){
//					name = null; 
//					mebnExtension.plusContextNodeNul(); 
//				}
//			}
//			
//			NecessaryConditionVariableModel nc = rule.getNecessaryConditionList().get(i);			
//			ContextNodeExtension contextNodeExtension = new ContextNodeExtension(mfragExtension, nc, name);
//			mfragExtension.addContextNode(contextNodeExtension);
//			
////			System.out.println("LALALA - "+node.getName());
//			
//		}
	}	
	
	/**
	 * TODO create a new method to map attributes as UndefinedNodes
	 */
	
//	public void createUndefinedNodes(MultiEntityBayesianNetwork mebn) {
//		
//		for (int i = 0; i < mfragExtensionList.size(); i++) {
//			MFragExtension mfrag = mfragExtensionList.get(i);
//			GroupModel group = mfrag.getGroupRelated();
//			List<RelationshipModel> relationshipList = group.getBacktrackingRelationship();
//			for(RelationshipModel relationship : relationshipList) {
//				
//				String name = relationship.getName();
//				UndefinedNode node = new UndefinedNode(name, mfrag);
//				node.setRelationshipPointer(relationship);
////				mfragExtension.addUndefinedNode(node);
//				
//				
//			}
//		}
//	}
	
	/**
	 * Create all MFrags from set of groups.
	 */
	public void createAllMFrags(MultiEntityBayesianNetwork mebn) {
		Map<String, GroupModel> mapGroup = umsptProject.getMapGroups();
		Set<String> keys = mapGroup.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);	
		
		for (String key : sortedKeys) {			
			GroupModel group = mapGroup.get(key);
			
			String id = group.getId();
			String name = group.getName();
			name = name.replace(" ", "_");			
			
			MFragExtension mfrag = new MFragExtension(name, mebn, group);
			mebn.addDomainMFrag(mfrag);
			getMapMFragExtension().put(id, mfrag);
		}
	}

	/**
	 * @return the mapMFragExtension
	 */
	public Map<String, MFragExtension> getMapMFragExtension() {
		return mapMFragExtension;
	}

	/**
	 * @param mapMFragExtension the mapMFragExtension to set
	 */
	public void setMapMFragExtension(Map<String, MFragExtension> mapMFragExtension) {
		this.mapMFragExtension = mapMFragExtension;
	}	
}
