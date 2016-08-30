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

import unbbayes.controller.mebn.MEBNController;
import unbbayes.io.mebn.UbfIO2;
import unbbayes.io.mebn.exceptions.IOMebnException;
import unbbayes.io.mebn.owlapi.OWLAPICompatiblePROWL2IO;
import unbbayes.model.umpst.entity.EntityModel;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.model.umpst.implementation.EffectVariableModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.implementation.algorithm.FirstCriterionOfSelection;
import unbbayes.model.umpst.implementation.algorithm.SecondCriterionOfSelection;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.ResidentNodeExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.Edge;
import unbbayes.prs.INode;
import unbbayes.prs.Node;
import unbbayes.prs.mebn.ContextNode;
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
	
	private UMPSTProject umpstProject;
	 
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
		
		this.umpstProject = umpstProject;		
		setMapMFragExtension(new HashMap<String, MFragExtension>());
		
		// temporary method to create mtheory
		MultiEntityBayesianNetwork tmpMebn = createMebnInstance(null);
		Debug.println("-----------------");
		Debug.println("Created temporary mtheory: " + tmpMebn.getName());
		
		// create model to define mebn elements
		MultiEntityBayesianNetwork mebn = createMebnInstance(tmpMebn);
		Debug.println("Created working version of mtheory: " + mebn.getName());
		
		//Entities
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
//		MEBNController mebnController = new MEBNController(mebn, null);
//		createAllContextNodes(mebnController);
		
		// Undefined Nodes
		createAllUndefinedNodes(mebn);
//		
		firstCriterion = new FirstCriterionOfSelection(umpstProject, this, mebn);
		secondCriterion = new SecondCriterionOfSelection(umpstProject, this, mebn);
		
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
	 * Get {@link UndefinedNode} related to {@link CauseVariableModel} identifying
	 * the same {@link RelationshipModel}
	 * 
	 * @param cause
	 * @param mfragExtension
	 * @return
	 */
	public UndefinedNode getUndefinedNodeRelatedToCause(CauseVariableModel cause,
			MFragExtension mfragExtension) {
		
		List<UndefinedNode> undefinedNodeList = mfragExtension.getUndefinedNodeList();
		for (int i = 0; i < undefinedNodeList.size(); i++) {
			
			UndefinedNode node = undefinedNodeList.get(i);
			if (node.getRelationshipPointer().equals(cause.getRelationshipModel())) {
				 return node;
			}
		}
		return null;
	}
	
	/**
	 * Get {@link UndefinedNode} related to {@link EffectVariableModel} identifying
	 * the same {@link RelationshipModel}. If the return is null, then the event probably it is a {@link ResidentNodeExtension}
	 * 
	 * @param effect
	 * @param mfragExtension
	 * @return
	 */
	public UndefinedNode getUndefinedNodeRelatedToEffect(EffectVariableModel effect,
			MFragExtension mfragExtension) {
		
		List<UndefinedNode> undefinedNodeList = mfragExtension.getUndefinedNodeList();
		for (int i = 0; i < undefinedNodeList.size(); i++) {
			
			UndefinedNode node = undefinedNodeList.get(i);
			if (node.getRelationshipPointer().equals(effect.getRelationshipModel())) {
				 return node;
			}
		}
		return null;
	}
	
	/**
	 * Maps {@link UndefinedNode} to {@link ResidentNodeExtension} without a proper integration between
	 * its arguments. 
	 * @param undefinedNode
	 * @return residentNode
	 */
	public ResidentNodeExtension mapToResidentNode(UndefinedNode undefinedNode, MFragExtension mfrag) {
		
//		List<ResidentNodeExtension> residentNodeList = mfrag.getResidentNodeExtensionList();
//		for (int i = 0; i < residentNodeList.size(); i++) {
//			
//			ResidentNodeExtension existingNode = residentNodeList.get(i); 
//			if (existingNode.getEventRelated().getClass().equals(RelationshipModel.class)) {
//				
//				RelationshipModel eventRelated = (RelationshipModel)existingNode.getEventRelated();
//				if(eventRelated.equals(undefinedNode.getRelationshipPointer())) {
//					return existingNode;
//				}
//			}
//		}
		ResidentNodeExtension residentNode = new ResidentNodeExtension(undefinedNode.getName(), mfrag);
		mfrag.addResidentNodeExtension(residentNode);
		mfrag.removeUndefinedNode(undefinedNode);
		return residentNode;
	}
	
	public UndefinedNode mapToUndefinedNode(RelationshipModel relationship, MFragExtension mfrag) {
		
		UndefinedNode node = new UndefinedNode(relationship.getName(), mfrag);
		node.setRelationshipPointer(relationship);
		return node;
	}
	

	/**
	 * Maps an {@link OrdinaryVariableModel} created in UMP-ST implementation panel to
	 * an {@link OrdinaryVariable} of Mebn structure
	 * @param ordinaryVariableModel, mfragExtension
	 * @return ordinaryVariable
	 */
	public OrdinaryVariable mapOrdinaryVariable(OrdinaryVariableModel
			ordinaryVariableModel, MFragExtension mfrag) {
		
		String typeName = ordinaryVariableModel.getTypeEntity();
		Type type = MappingController.getType(mfrag.getMultiEntityBayesianNetwork(), typeName);
		
		OrdinaryVariable ov = new OrdinaryVariable(
					ordinaryVariableModel.getVariable(), type, mfrag);
		
		return ov;
	}	
	
//	public void mapContextNodeFormula(ContextNode node, NecessaryConditionVariableModel ncModel,
//			MEBNController mebnController) {
//		FormulaTreeController formulaControllerMebn = new FormulaTreeController(mebnController, node, null);
//		
//		NodeFormulaTreeUMP rootFormulaUMP = ncModel.getFormulaTree();
//		DefaultMutableTreeNode rootTreeView = new DefaultMutableTreeNode();
//		
//		for(NodeFormulaTreeUMP child: rootFormulaUMP.getChildrenUMP()){
//			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(child); 
//			nodeTreeFather.add(treeNode);
//			buildChildren(child, treeNode); 
//		}
//		return nodeTreeFather;
//	}
	
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

	/**
	 * Creates an instance of {@link MultiEntityBayesianNetwork} to define a MTheory model. 
	 * @param tmpMebn
	 * @return
	 */
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
					umpstProject.getModelName());
			return mebn;
		}
	}
	
	/**
	 * keep all {@link NecessaryConditionVariableModel} from UMP-ST {@link RuleModel} and create {@link ContextNode}
	 * related to MEBN structure
	 */
	public void createAllContextNodes(MEBNController mebnController) {
		Map<String, RuleModel> mapRule = umpstProject.getMapRules();
		Set<String> keys = mapRule.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String key : sortedKeys) {
			
			RuleModel rule = mapRule.get(key);
			if (rule.getFowardtrackingGroupList().size() <  2) {
				List<GroupModel> groupList = rule.getFowardtrackingGroupList();
				
				for (int i = 0; i < groupList.size(); i++) {
					GroupModel group = groupList.get(i);					
					MFragExtension mfrag = mapMFragExtension.get(group.getId());
					
					List<NecessaryConditionVariableModel> ncModelList = rule.getNecessaryConditionList();
					for (int j = 0; j < ncModelList.size(); j++) {
						NecessaryConditionVariableModel ncModel = ncModelList.get(j);
						ContextNode contextNode = mfrag.addContextNode(ncModel);
//						mfrag.mapContextNodeFormula(contextNode, ncModel, mebnController);
					}
				}
				
			} else {
				System.err.println("Rule "+ rule.getId()+" related to more then one group");
			}
		}
	}
	
	/**
	 * Keep all entities from UMP-ST and create entities related to Mebn structure
	 * @throws TypeException
	 */
	public void createAllEntities(MultiEntityBayesianNetwork mebn) {
		
		Map<String, EntityModel> mapEntity = umpstProject.getMapEntity();
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
		Map<String, RuleModel> mapRule = umpstProject.getMapRules();
		Set<String> keys = mapRule.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String key : sortedKeys) {
			RuleModel rule = mapRule.get(key);
			
			if (rule.getFowardtrackingGroupList().size() <  2) {
				List<GroupModel> groupList = rule.getFowardtrackingGroupList();
				
				for (int i = 0; i < groupList.size(); i++) {
					GroupModel group = groupList.get(i);
					MFragExtension mfrag = mapMFragExtension.get(group.getId());
					
					List<OrdinaryVariableModel> ovModelList = rule.getOrdinaryVariableList();
					for (int j = 0; j < ovModelList.size(); j++) {
						OrdinaryVariableModel ovModel = ovModelList.get(j);
						OrdinaryVariable ov = mapOrdinaryVariable(ovModel, mfrag);
						mfrag.addOrdinaryVariable(ov);
					}
				}				
				
			} else {
				System.err.println("Rule "+ rule.getId()+" related to more then one group");
			}
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
	
	public void createAllUndefinedNodes(MultiEntityBayesianNetwork mebn) {
		Set<String> keys = getMapMFragExtension().keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String groupId : sortedKeys) {
			MFragExtension mfrag = getMapMFragExtension().get(groupId);
			GroupModel group = mfrag.getGroupRelated();
			
			List<RelationshipModel> relationshipList = group.getBacktrackingRelationship();
			for (int i = 0; i < relationshipList.size(); i++) {
				UndefinedNode node = mapToUndefinedNode(relationshipList.get(i), mfrag);
				mfrag.addUndefinedNode(node);
			}
		}
	}
	
	/**
	 * Create all MFrags from set of groups.
	 */
	public void createAllMFrags(MultiEntityBayesianNetwork mebn) {
		Map<String, GroupModel> mapGroup = umpstProject.getMapGroups();
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
	 * Maps {@link GroupModel} id and {@link MFragExtension} as <String, Object> parameters
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
