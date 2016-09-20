package unbbayes.controller.umpst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import unbbayes.model.umpst.implementation.node.InputNodeExtension;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.ResidentNodeExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.Edge;
import unbbayes.prs.exception.InvalidParentException;
import unbbayes.prs.mebn.ContextNode;
import unbbayes.prs.mebn.InputNode;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.ResidentNodePointer;
import unbbayes.prs.mebn.entity.ObjectEntity;
import unbbayes.prs.mebn.entity.ObjectEntityContainer;
import unbbayes.prs.mebn.entity.Type;
import unbbayes.prs.mebn.entity.TypeContainer;
import unbbayes.prs.mebn.entity.exception.TypeException;
import unbbayes.prs.mebn.exception.ArgumentNodeAlreadySetException;
import unbbayes.prs.mebn.exception.CycleFoundException;
import unbbayes.prs.mebn.exception.MEBNConstructionException;
import unbbayes.prs.mebn.exception.OVDontIsOfTypeExpected;
import unbbayes.prs.mebn.exception.OVariableAlreadyExistsInArgumentList;

/**
 * @author Diego Marques
 */
public class MappingController {
	
	private UMPSTProject umpstProject;
	
	private List<UndefinedNode> undefinedNodeList;
	 
	private FirstCriterionOfSelection firstCriterion;
	private SecondCriterionOfSelection secondCriterion;
	
	private Controller controller; 
	private ResourceBundle resourceUmp = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.gui.umpst.resources.Resources.class.getName());
	
	private static ResourceBundle resourceMebn = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.controller.mebn.resources.Resources.class.getName());;

	private DefaultTreeModel treeModel;
	private ObjectEntityContainer entityContainer;
	private ObjectEntity rootObjectEntity;
	private TypeContainer typeContainer;
	
	private Map<String, MFragExtension> mapMFragExtension;
	
	public MappingController (UMPSTProject umpstProject) {
		
		this.umpstProject = umpstProject;		
		setMapMFragExtension(new HashMap<String, MFragExtension>());
		setUndefinedNodeList(new ArrayList<UndefinedNode>());
		
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
//		createAllUndefinedNodes(mebn);
//		
		firstCriterion = new FirstCriterionOfSelection(umpstProject, this, mebn);
		secondCriterion = new SecondCriterionOfSelection(umpstProject, this, mebn);
		
//		testMTheory(mebn);
		printMTheory(mebn);
	}
	
	/**
	 * Print model
	 * @param mebn
	 */
	public void printMTheory(MultiEntityBayesianNetwork mebn) {
		
		List<MFrag> mfragList = mebn.getDomainMFragList();
		for (int i = 0; i < mfragList.size(); i++) {
			
			MFrag mfrag = mfragList.get(i);
			System.out.println("===============");
			System.out.println(mfrag.getName());
			
			// OV
//			System.out.println("== OV");
			for (int j = 0; j < mfrag.getOrdinaryVariableList().size(); j++) {
				
				OrdinaryVariable ov = mfrag.getOrdinaryVariableList().get(j);
				System.out.println(ov.getLabel());
			}
			
			System.out.println("== Resident");
			for (int j = 0; j < mfrag.getResidentNodeList().size(); j++) {
				
				ResidentNode resident = mfrag.getResidentNodeList().get(j);
				System.out.println(resident.getLabel());
			}
			
			System.out.println("== Input");
			for (int j = 0; j < mfrag.getInputNodeList().size(); j++) {
				
				InputNode input = mfrag.getInputNodeList().get(j);
				System.out.println(input.getLabel());
			}			
		}
		
		System.out.println("== UndefinedNodeList");
		for (int i = 0; i < getUndefinedNodeList().size(); i++) {
			
			UndefinedNode un = getUndefinedNodeList().get(i);
			System.out.println(((CauseVariableModel)un.getEventRelated()).getRelationship() + " in " +
					un.getMfragExtension().getName());
		}
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
				
				controller.showSucessMessageDialog(resourceUmp.getString("msSaveSuccessfull"));
			} catch (IOMebnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			controller.showErrorMessageDialog(resourceUmp.getString("erSaveFatal")); 
		}
	}
	
	
	/**
	 * Verify if there is any {@link ResidentNodeExtension} related to the {@link CauseVariableModel} in any {@link MFragExtension} of
	 * the model except the {@link MFragExtension} passed as parameter.
	 * @param event
	 * @param mfragExtension
	 * @return
	 */
	public ResidentNodeExtension getResidentNodeRelatedToAny(Object eventRelated, MFragExtension mfragExtensionRelated) {
		
		Map<String, MFragExtension> mapMFragExtension = getMapMFragExtension();
		Set<String> keys = mapMFragExtension.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String groupId : sortedKeys) {	
			MFragExtension mfragExtensionCompared = mapMFragExtension.get(groupId);
			
			if(!mfragExtensionRelated.equals(mfragExtensionCompared)) {
				
				ResidentNodeExtension residentNodeRelated = getResidentNodeRelatedTo(eventRelated, mfragExtensionCompared);
				if(residentNodeRelated != null) {
					return residentNodeRelated;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Verify if there is any {@link ResidentNodeExtension} related to the {@link RelationshipModel} compared in the
	 * {@link MFragExtension} selected.
	 * @param relationshipCompared
	 * @param mfragExtension
	 * @return
	 */
	public ResidentNodeExtension getResidentNodeRelatedTo(RelationshipModel relationshipCompared, MFragExtension mfragExtension) {
		
		List<ResidentNodeExtension> residentNodeExtensionList = mfragExtension.getResidentNodeExtensionList();
		
		for (int i = 0; i < residentNodeExtensionList.size(); i++) {
			
			// Resident node is random variable related to an attribute or relationship.
			if (residentNodeExtensionList.get(i).getEventRelated().getClass().equals(
					RelationshipModel.class)) {
				
				ResidentNodeExtension residentNode = residentNodeExtensionList.get(i);
				RelationshipModel relationshipRelated = (RelationshipModel)residentNode.getEventRelated();
				
				if (relationshipCompared.equals(relationshipRelated)) {
					return residentNode;
				}
			}
		}
		return null;
	}
	
	/**
	 * Verify if {@link CauseVariableModel} or {@link EffectVariableModel} defined in {@link RuleModel} were mapped as 
	 * {@link ResidentNodeExtension} in {@link MFragExtension} related to {@link RuleModel}. If it is, then return
	 * {@link ResidentNodeExtension} identified.
	 * @param cause or effect
	 * @return residentNode
	 */
	public ResidentNodeExtension getResidentNodeRelatedTo(Object event, MFragExtension mfrag) {
		
		List<ResidentNodeExtension> residentNodeExtensionList = mfrag.getResidentNodeExtensionList();
		
		for (int i = 0; i < residentNodeExtensionList.size(); i++) {
			
			// Resident node is random variable related to an attribute or relationship.
			if (residentNodeExtensionList.get(i).getEventRelated().getClass().equals(
					RelationshipModel.class)) {
				
				ResidentNodeExtension residentNode = residentNodeExtensionList.get(i);
				RelationshipModel relationshipModel = (RelationshipModel)residentNode.getEventRelated();

				// This event can be a cause or effect variable
				if(event.getClass().equals(CauseVariableModel.class)) {
					if (relationshipModel.equals(((CauseVariableModel)event).getRelationshipModel())) {
						return residentNode;
					}
				}
				else { // It is EffectVariableModel
					if (relationshipModel.equals(((EffectVariableModel)event).getRelationshipModel())) {
						return residentNode;
					}
				}
			}
			
			// TODO if the model there is attribute as random variable it is necessary to make other option
		}
		return null;
	}	
	
	/**
	 * Map all {@link EffectVariableModel} to {@link ResidentNodeExtension} and set as father node the node 
	 * mapped in cause relation.
	 * The object node passed in parameter it is a father node that can be a {@link ResidentNodeExtension} or
	 * an {@link InputNodeExtension}.
	 * 
	 * @param residentNode or InputNode
	 * @throws OVariableAlreadyExistsInArgumentList 
	 * @throws ArgumentNodeAlreadySetException 
	 * @throws InvalidParentException 
	 */
	public void mapAllEffectsToResident(Object nodeFather, MFragExtension mfragExtension, RuleModel rule) throws
		ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList, InvalidParentException {
		
		/**
		 * Get all effects from rule
		 */
		for (int l = 0; l < rule.getEffectVariableList().size(); l++) {
			
			EffectVariableModel effect = rule.getEffectVariableList().get(l);
			ResidentNodeExtension residentNode = getResidentNodeRelatedToEffect(effect, mfragExtension);
			
			if(residentNode == null) {
				
				/**
				 * Map the effects to residentNodes and add to mfragExtension
				 */
				residentNode = mapToResidentNode(effect.getRelationshipModel(), mfragExtension, effect);
				if(residentNode == null) {
					System.err.println(this.getClass()+ "Error in create residentNode: "+effect.getRelationship());
				}				
			}
			else if ((residentNode != null) && (residentNode.getOrdinaryVariableList().size() == 0)) {
				
				/**
				 * It is possible to exist nodes that were mapped to resident by the first criteria and
				 * does not have its arguments added to itself.
				 */
				residentNode = mapResidentNodeArgument(effect, residentNode, mfragExtension);
			}

			/**
			 * Create an edge linking the parent node to the child node related to ResidentNode created
			 * from EffectModel
			 */
			if(nodeFather.getClass().equals(ResidentNodeExtension.class)) {
				
				Edge auxEdge = new Edge((ResidentNodeExtension)nodeFather, residentNode);
				
				try {
					mfragExtension.addEdge(auxEdge);
				} catch (MEBNConstructionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CycleFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(nodeFather.getClass().equals(InputNodeExtension.class)) {
				
				Edge auxEdge = new Edge((InputNodeExtension)nodeFather, residentNode);
				
				try {
					mfragExtension.addEdge(auxEdge);
				} catch (MEBNConstructionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CycleFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * This method consider that the {@link ResidentNodeExtension} is not null so it basically add the arguments related
	 * to the {@link CauseVariableModel} or {@link EffectVariableModel} in the {@link ResidentNodeExtension}.
	 * 
	 * @param cause
	 * @param residentNode
	 * @throws ArgumentNodeAlreadySetException
	 * @throws OVariableAlreadyExistsInArgumentList
	 */
	public ResidentNodeExtension mapResidentNodeArgument(Object event, ResidentNodeExtension residentNode, MFragExtension mfragExtension) 
			throws ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList {
		
		// Only add arguments if the ordinaryVariableList of resident has anyone ordinaryVariable
//		if(residentNode.getArgumentList().size() == 0) {
				
			List<OrdinaryVariableModel> ovEventModelList = null;
			
			// Verify if the event it is cause or effect
			if(event.getClass().equals(CauseVariableModel.class)) { 
				ovEventModelList = ((CauseVariableModel)event).getOvArgumentList();
			}
			// the event it is effect
			else {
				ovEventModelList = ((EffectVariableModel)event).getOvArgumentList();
			}
			
			// Add arguments related to the event
			for (int i = 0; i < ovEventModelList.size(); i++) {
				OrdinaryVariableModel ovModel = ovEventModelList.get(i);
				
//				// OrdinaryVariable from MEBN
//				List<OrdinaryVariable> ovList = mfragExtension.getOrdinaryVariableList();
//				for (int j = 0; j < ovList.size(); j++) {
//					
//					// Identify by the name of ordinary variable and its type
//					OrdinaryVariable ov = ovList.get(j);
//					if ((ovModel.getVariable().equals(ov.getName()) &&
//							(ovModel.getTypeEntity().equals(ov.getValueType().toString())))) {
				
				int index = mfragExtension.getOrdinaryVariableIndexOf(ovModel);
				if(index > -1) {
				
					OrdinaryVariable ov = mfragExtension.getOrdinaryVariableList().get(index);
						
						residentNode.addArgument(ov, true);
	//					try {
	//						residentNode.addArgumentRelated(ov);
	//					} catch (ArgumentOVariableAlreadySetException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
//					}
				}
			}
//		}
		if(residentNode.getOrdinaryVariableList().size() == 0) {
			System.err.println(this.getClass() + " - ERROR. NUMBER OF ARGUMENT INVALID - " + residentNode.getName());
		}
		return residentNode;
	}
	
	/**
	 * Get {@link UndefinedNode} related to {@link CauseVariableModel} identifying
	 * the same {@link RelationshipModel}
	 * 
	 * @param cause
	 * @param mfragExtension
	 * @return
	 */
//	public UndefinedNode getUndefinedNodeRelatedToCause(CauseVariableModel cause,
//			MFragExtension mfragExtension) {
//		
//		List<UndefinedNode> undefinedNodeList = mfragExtension.getUndefinedNodeList();
//		for (int i = 0; i < undefinedNodeList.size(); i++) {
//			
//			UndefinedNode node = undefinedNodeList.get(i);
//			if (node.getRelationshipPointer().equals(cause.getRelationshipModel())) {
//				 return node;
//			}
//		}
//		return null;
//	}
	
	/**
	 * Get {@link ResidentNodeExtension} related to {@link EffectVariableModel} identifying
	 * the same {@link RelationshipModel}. If the return is null, then the event {@link ResidentNodeExtension} needs
	 * to be created.
	 * 
	 * @param effect
	 * @param mfragExtension
	 * @return
	 */
	public ResidentNodeExtension getResidentNodeRelatedToEffect(EffectVariableModel effect,
			MFragExtension mfragExtension) {
		
		List<ResidentNodeExtension> residentNodeList = mfragExtension.getResidentNodeExtensionList();
		for (int i = 0; i < residentNodeList.size(); i++) {
			
			ResidentNodeExtension node = residentNodeList.get(i);
			if ((node.getEventRelated().getClass().equals(RelationshipModel.class))) {
				
				RelationshipModel relationshipRelated = (RelationshipModel)node.getEventRelated();
				if(relationshipRelated.equals(effect.getRelationshipModel())) {
					return node;
				}
			}
		}
		return null;
	}
	
	/**
	 * Get {@link MFragExtension} related to {@link GroupModel} passed in parameter.
	 * @param group
	 * @return mfragExtension
	 */
	public MFragExtension getMFragRelatedToGroup(GroupModel group) {		
		MFragExtension mfrag = mapMFragExtension.get(group.getId());
		return mfrag;
	}
	
	/**
	 * Set {@link ResidentNodeExtension} as instance of {@link InputNodeExtension} the {@link ResidentNodeExtension}
	 * passed as parameter.
	 * @param inputNode
	 * @param residentNode
	 */
	public void setInstanceOfInputNode(InputNodeExtension inputNode, ResidentNodeExtension residentNode) {
		try {
			inputNode.setInputInstanceOf(residentNode);
		} catch (OVDontIsOfTypeExpected e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArgumentNodeAlreadySetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Maps the {@link OrdinaryVariableModel} of the {@link CauseVariableModel} to {@link OrdinaryVariable} of the {@link ResidentNodePointer}.
	 * @param cause
	 * @param pointer
	 * @param mfragExtension
	 * @return
	 */
	public ResidentNodePointer mapResidentNodePointerArgument(CauseVariableModel cause, ResidentNodePointer pointer, MFragExtension mfragExtension) {
		
		// Add arguments related to the event
		List<OrdinaryVariableModel> causeOvModelList = cause.getOvArgumentList();
		for (int i = 0; i < causeOvModelList.size(); i++) {
			OrdinaryVariableModel ovModel = causeOvModelList.get(i);
			
			// OrdinaryVariable from MEBN
			List<OrdinaryVariable> ovList = mfragExtension.getOrdinaryVariableList();
			for (int j = 0; j < ovList.size(); j++) {
				
				// Identify by the name of ordinary variable and its type
				OrdinaryVariable ov = ovList.get(j);
				if ((ovModel.getVariable().equals(ov.getName()) &&
						(ovModel.getTypeEntity().equals(ov.getValueType().toString())))) {
					
					try{
						pointer.addOrdinaryVariable(ov, i);
					}
					catch(OVDontIsOfTypeExpected ex){
						ex.printStackTrace(); 
					}
				}
			}
		}
		return pointer;
	}
	
	/**
	 * Maps a {@link CauseVariableModel} to {@link InputNodeExtension}. This {@link CauseVariableModel} needs
	 * to be an effect in other {@link RuleModel} of other {@link GroupModel}.
	 * @param cause
	 * @param mfrag
	 * @return
	 * @throws ArgumentNodeAlreadySetException 
	 * @throws OVDontIsOfTypeExpected 
	 */
	public InputNodeExtension mapToInputNode(CauseVariableModel cause, MFragExtension mfragExtension, ResidentNodeExtension resident)
			throws OVDontIsOfTypeExpected, ArgumentNodeAlreadySetException {
		/**
		 * The object needs to be a causeModel related to RuleModel, not an undefinedNode or effectModel.
		 * The relationship can repeat according to the rule related to the GroupModel. So if there
		 * is a group in which there are more then one rule and two of these rules have the same
		 * relationship as cause and effect the algorithm could not support this case if undefinedNode
		 * was the object parameter.
		 * 
		 * The effectModel has as parent a causeModel which will be mapped to resident or input node.
		 * The input node does not have as parent another node, only if it is a resident node in other mfrag.
		 */
		
		String name = null;
		while (name == null){
			name = resourceMebn.getString("inputNodeName") + mfragExtension.getMultiEntityBayesianNetwork().getGenerativeInputNodeNum(); 
			if(mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().contains(name)){
				name = null; 
				mfragExtension.getMultiEntityBayesianNetwork().plusGenerativeInputNodeNum(); 
			}
		}
		
		InputNodeExtension inputNode = new InputNodeExtension(name, mfragExtension, cause);
		mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().add(name);
		inputNode.setDescription(inputNode.getName());
		mfragExtension.addInputNodeExtension(inputNode);
		
//		InputNodeExtension inputNode = new InputNodeExtension(cause.getRelationship(), mfragExtension);
//		mfragExtension.addInputNodeExtension(inputNode);
		
		inputNode.setInputInstanceOf((ResidentNode)resident);
//		inputNode.updateResidentNodePointer();
		ResidentNodePointer pointer = mapResidentNodePointerArgument((CauseVariableModel)inputNode.getEventRelated(),
				inputNode.getResidentNodePointer(), mfragExtension);
		inputNode.updateLabel();
//		inputNode.updateResidentNodePointer();
		
		return inputNode;
	}
	
	/**
	 * Maps {@link UndefinedNode} to {@link ResidentNodeExtension} without a proper integration between
	 * its arguments. 
	 * @param undefinedNode
	 * @return residentNode
	 */
	public ResidentNodeExtension mapToResidentNode(RelationshipModel relationship, MFragExtension mfragExtension,
			Object event) {
		
		// Set unique name
		String name = null;
//		int residentNodeNum = mfragExtension.getDomainResidentNodeNum();

		while (name == null){
			name = resourceMebn.getString("residentNodeName") +
			                        mfragExtension.getMultiEntityBayesianNetwork().getDomainResidentNodeNum();
			if(mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().contains(name)){
				name = null;
				mfragExtension.getMultiEntityBayesianNetwork().plusDomainResidentNodeNum();
			}
		}
		
		ResidentNodeExtension residentNode = new ResidentNodeExtension(name, mfragExtension, relationship);
		residentNode.setDescription(residentNode.getName());
		
		mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().add(name);
		mfragExtension.addResidentNodeExtension(residentNode);
		
		
		// Rename residentNode
		String relationshipName = relationship.getName();
		
		mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().remove(residentNode.getName()); 
		residentNode.setName(relationshipName);
		mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().add(relationshipName); 
		
//		String relationshipName = relationship.getName();
//		if(!mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().contains(relationshipName)){
			
//			ResidentNodeExtension residentNode = new ResidentNodeExtension(relationship.getName(), mfragExtension,
//					relationship);
//			mfragExtension.getMultiEntityBayesianNetwork().getNamesUsed().add(relationship.getName());
			
			if(event != null) {
				try {
					residentNode = mapResidentNodeArgument(event, residentNode, mfragExtension);
				} catch (ArgumentNodeAlreadySetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OVariableAlreadyExistsInArgumentList e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			
//			residentNode.setDescription(residentNode.getName());
//			mfragExtension.addResidentNodeExtension(residentNode);
			return residentNode;
//		}
//		else {
//			System.err.println(this.getClass() + " - There is another resident node with the same name in mfrag - "
//					+ mfragExtension.getName());
//			return null;
//		}
	}
	
//	public UndefinedNode mapToUndefinedNode(RelationshipModel relationship, MFragExtension mfrag) {
//		
//		UndefinedNode node = new UndefinedNode(relationship.getName(), mfrag);
//		node.setRelationshipPointer(relationship);
//		return node;
//	}
	

	/**
	 * Maps an {@link OrdinaryVariableModel} created in UMP-ST implementation panel to
	 * an {@link OrdinaryVariable} of Mebn structure and add it to {@link MFragExtension}.
	 * @param ordinaryVariableModel, mfragExtension
	 * @return ordinaryVariable
	 */
	public OrdinaryVariable mapToOrdinaryVariable(OrdinaryVariableModel
			ovModel, MFragExtension mfragExtension) {
		
		String name = null;
		while (name == null){
			name = resourceMebn.getString("ordinaryVariableName") + mfragExtension.getOrdinaryVariableNum(); 
			if(mfragExtension.getOrdinaryVariableByName(name) != null){
				name = null; 
				mfragExtension.plusOrdinaryVariableNum(); 
			}
		}
		
		String typeName = ovModel.getTypeEntity();
		Type type = MappingController.getType(mfragExtension.getMultiEntityBayesianNetwork(), typeName);
		
		OrdinaryVariable ov = new OrdinaryVariable(name, type, mfragExtension);
		ov.setDescription(ov.getName());
		
		// Rename ordinary variable
		ov.setName(ovModel.getVariable()); 
		ov.updateLabel();
		
//		OrdinaryVariable ov = new OrdinaryVariable(
//					ordinaryVariableModel.getVariable(), type, mfrag);
		
		// Add ov in the ontology and ovModel in the MFragExtension
		mfragExtension.addOrdinaryVariable(ov);
		mfragExtension.addOrdinaryVariableModel(ovModel);
		
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
	 * Maps {@link OrdinaryVariableModel} present in {@link RuleModel} to {@link OrdinaryVariable}. The {@link GroupModel}
	 * that does not have {@link RuleModel} will not be included in this method. So the {@link GroupModel} mapped to
	 * {@link MFragExtension} that does not have {@link RuleModel} will not have {@link OrdinaryVariable}.
	 * 
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
						
						if(!mfrag.existsAsOrdinaryVariableModel(ovModel)) {
							OrdinaryVariable ov = mapToOrdinaryVariable(ovModel, mfrag); 
						}
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
	
	/**
	 * Create all the {@link UndefinedNode} related to {@link RelationshipModel} of each {@link GroupModel}.
	 * So, it is possible to exist the same {@link UndefinedNode} in two different {@link MFragExtension}
	 * @param mebn
	 */
//	public void createAllUndefinedNodes(MultiEntityBayesianNetwork mebn) {
//		Set<String> keys = getMapMFragExtension().keySet();
//		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
//		
//		for (String groupId : sortedKeys) {
//			MFragExtension mfrag = getMapMFragExtension().get(groupId);
//			GroupModel group = mfrag.getGroupRelated();
//			
//			List<RelationshipModel> relationshipList = group.getBacktrackingRelationship();
//			for (int i = 0; i < relationshipList.size(); i++) {
//				UndefinedNode node = mapToUndefinedNode(relationshipList.get(i), mfrag);
//				mfrag.addUndefinedNode(node);
//			}
//		}
//	}
	
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
	 * Maps {@link GroupModel} id and {@link MFragExtension} as <String, Object> parameters
	 * @param mapMFragExtension the mapMFragExtension to set
	 */
	public void setMapMFragExtension(Map<String, MFragExtension> mapMFragExtension) {
		this.mapMFragExtension = mapMFragExtension;
	}

	/**
	 * @return the undefinedNodeList
	 */
	public List<UndefinedNode> getUndefinedNodeList() {
		return undefinedNodeList;
	}

	/**
	 * @param undefinedNodeList the undefinedNodeList to set
	 */
	public void setUndefinedNodeList(List<UndefinedNode> undefinedNodeList) {
		this.undefinedNodeList = undefinedNodeList;
	}	
}
