package unbbayes.model.umpst.implementation.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.model.umpst.implementation.EffectVariableModel;
import unbbayes.model.umpst.implementation.EventNCPointer;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.NodeFormulaTreeUMP;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.implementation.node.InputNodeExtension;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.NodeInputModel;
import unbbayes.model.umpst.implementation.node.NodeType;
import unbbayes.model.umpst.implementation.node.ResidentNodeExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.Edge;
import unbbayes.prs.exception.InvalidParentException;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.context.EnumType;
import unbbayes.prs.mebn.exception.ArgumentNodeAlreadySetException;
import unbbayes.prs.mebn.exception.CycleFoundException;
import unbbayes.prs.mebn.exception.MEBNConstructionException;
import unbbayes.prs.mebn.exception.OVariableAlreadyExistsInArgumentList;
import unbbayes.util.Debug;

/**
 * This class deals with causal relation related to rule. This rule is in a group.
 * @param rule
 * @param group
 */
public class DefineDependenceRelation {
	
	private RuleModel rule;
	private GroupModel group;
	private EffectVariableModel effectWasCause;
	private Map<String, GroupModel> mapGroup;
	
	private MFragExtension mfragExtensionActive;	
	private MappingController mappingController;
	private SecondCriterionOfSelection secondCriterion;
	
	private UMPSTProject umpstProject;
	
	public DefineDependenceRelation(RuleModel rule, GroupModel group, MFragExtension mfrag, MappingController mappingController,
			UMPSTProject umpstProject, SecondCriterionOfSelection secondCriterion) {
		
		this.rule = rule;
		this.group = group;
		this.mfragExtensionActive = mfrag;
		this.mappingController = mappingController;
		this.umpstProject = umpstProject;
		this.secondCriterion = secondCriterion;
		
		try {
			mapCausalRelation();
		} catch (ArgumentNodeAlreadySetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OVariableAlreadyExistsInArgumentList e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidParentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		mapMissingRelationships();
	}
	
	public void mapMissingRelationships() {
		List<RelationshipModel> listMissingRelationship = searchMissingRelationship();
		
//		for (int i = 0; i < listMissingRelationship.size(); i++) {
//			System.out.println(listMissingRelationship.get(i).getName());
//		}		
	}
	
	/**
	 * Verify if there are relationships that were not defined in rule definition as cause or effect.
	 * If it exist, then it is added in list of missing relationships.
	 * @return List<RelationshipModel>
	 */
	public List<RelationshipModel> searchMissingRelationship() {
		List<RelationshipModel> listMissingRelationship = new ArrayList<RelationshipModel>();
		boolean exist;
		
		for (int i = 0; i < group.getBacktrackingRelationship().size(); i++) {
			RelationshipModel relationshipOfGroup = group.
					getBacktrackingRelationship().get(i);			
			exist = false;
			
			for (int j = 0; j < rule.getCauseVariableList().size(); j++) {
				CauseVariableModel causeVar = rule.getCauseVariableList().get(j);				
				RelationshipModel relationshipOfCause = causeVar.getRelationshipModel();
				
				if (relationshipOfGroup.equals(relationshipOfCause)) {
					exist = true;
					break;
				}
			}
			for (int j = 0; j < rule.getEffectVariableList().size(); j++) {
				EffectVariableModel effectVar = rule.getEffectVariableList().get(j);				
				RelationshipModel relationshipOfEffect = effectVar.getRelationshipModel();
				
				if (relationshipOfGroup.equals(relationshipOfEffect)) {
					exist = true;
					break;
				}
			}
			
			// TODO recognize structures like isProcurementFinished(proc)^(enterprise = hasWinnerOfProcurement(proc))
			exist = existsAsNecessaryCondition(relationshipOfGroup);
				
			
//			for (int j = 0; j < rule.getRelationshipList().size(); j++) {								
//				RelationshipModel relationshipOfRule = rule.getRelationshipList().get(j);
//				
//				if (relationshipOfGroup.equals(relationshipOfRule)) {
//					exist = true;
//					break;
//				}
//			}
			if (!exist) {
//				System.out.println("--"+group.getName()+"--");
//				System.out.println(relationshipOfGroup.getName());
				listMissingRelationship.add(relationshipOfGroup);
			}
		}
		return listMissingRelationship;
	}
	
	// The algorithm maps all relationship as resident node at some MFrag related to group.
	public boolean existsAsNecessaryCondition(RelationshipModel relationship) {
		boolean exists = false;		
		for (int j = 0; j < rule.getNecessaryConditionList().size(); j++) {
			NecessaryConditionVariableModel nc = rule.getNecessaryConditionList().get(j);			
			NodeFormulaTreeUMP formulaTree = nc.getFormulaTree();
			
			exists = compareNodeFormula(formulaTree, relationship);
			if (exists) {
//				System.out.println("--"+group.getName()+"--");
//				System.out.println(relationship.getName());
				return true;
			}
		}
		return false;
	}
	
	public boolean compareNodeFormula(NodeFormulaTreeUMP node, RelationshipModel relationship) {
		if (node.getTypeNode() == EnumType.OPERAND) {
			EventNCPointer event = (EventNCPointer)node.getNodeVariable();
			RelationshipModel ncRelationship = event.getEventVariable().getRelationshipModel();
			if (ncRelationship.equals(relationship)) {
				return true;
			}
			if (node.getChildren().size() > 0) {
				compareNodeFormula(node.getChildrenUMP().get(0), relationship);			
			}
		}
		return false;
	}
	
	
	public void mapCausalRelation() throws ArgumentNodeAlreadySetException,
		OVariableAlreadyExistsInArgumentList, InvalidParentException {
		
		for (int j = 0; j < rule.getCauseVariableList().size(); j++) {				
			CauseVariableModel cause = rule.getCauseVariableList().get(j);

			// Cause was mapped as resident node in first criterion of selection
			Object objectNode = searchResidentNodeRelated(cause);
			
			if (objectNode != null) {
				/**
				 * The First Criterion of Selection does not add the arguments related to resident node
				 * created.
				 */
				mapResidentNodeArgument(cause, (ResidentNodeExtension)objectNode);
				mapAllEffectsAsResident(objectNode);
			}
//			// Cause is Effect in other rule
//			else if(existsAsEffect(cause)) {
//				mapCauseAsInput(cause);
//				mapAllEffectsAsResident();
//			}
//			// Verify if there are nodes that were not mapped. 
//			// Usually, these are nodes that were not mapped in other rule as effect.
//			else if(searchInputNode(cause) == null) {
//				String key = mfragSelected.getId();				
//				mapCauseAsNotDefined(cause);
//				
//				secondCriterion.getMapDoubtNodes().put(key, notDefinedNode);
//			}
		}
	}
	
	public void mapCauseAsNotDefined(CauseVariableModel cause) {
//		String sentence = cause.getRelationship() + "(";
//		for (int k = 0; k < cause.getArgumentList().size(); k++) {				
//			sentence = sentence + cause.getArgumentList().get(k) + ", ";
//		}
//		int index = sentence.lastIndexOf(", ");
//		sentence = sentence.substring(0, index);
//		sentence = sentence + ")";
//		
//		String id = cause.getId();
//		notDefinedNode = new NodeInputModel(id, sentence, NodeType.NOT_DEFINED, cause);
	}
	
	
	/**
	 * Verify if there are rules which have cause relationship as effect and map as input node if it exists. 
	 * @param cause
	 */
	public boolean existsAsEffect(CauseVariableModel cause) {
		
		mapGroup = umpstProject.getMapGroups();
		Set<String> keys = mapGroup.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String key : sortedKeys) {
			GroupModel groupSearched = mapGroup.get(key);
			
			if (!(group.getId().equals(groupSearched.getId()))) {
				
				for (int i = 0; i < groupSearched.getBacktrackingRelationship().size(); i++) {
					RelationshipModel relationship = groupSearched.
							getBacktrackingRelationship().get(i);
					
					if (relationship.getName().equals(cause.getRelationship())) {
						
						RuleModel ruleSelected = selectRuleRelated(groupSearched, cause);
						if (ruleSelected != null) {
							return true;
						}
					}
				}			
			}
		}
		return false;
	}
	
	/**
	 * Select rule that defines as effect the cause passed as argument.
	 * @param groupSearched
	 * @param cause
	 * @return RuleModel
	 */
	public RuleModel selectRuleRelated(GroupModel groupSearched, CauseVariableModel cause) {
		for (int i = 0; i < groupSearched.getBacktrackingRules().size(); i++) {
			RuleModel rule = groupSearched.getBacktrackingRules().get(i);
			EffectVariableModel effect = searchEffect(rule, cause);
			if (effect != null) {
				return rule;
			}		
		}
		return null;
	}
	
	/**
	 * Verify if rule defines cause selected as effect and returns the effect related. If rule defines, then
	 * set as resident node the relationship defined as effect.
	 * @param rule
	 * @param cause
	 * @return effect
	 */
	public EffectVariableModel searchEffect(RuleModel rule, CauseVariableModel cause) {
		for (int i = 0; i < rule.getEffectVariableList().size(); i++) {
			EffectVariableModel effect = rule.getEffectVariableList().get(i);
			if (effect.getRelationshipModel().equals(cause.getRelationshipModel())) {
//				mapEffectAsResident(effect);
				return effect;
			}
		}
		return null;
	}
	
//	public void mapEffectAsResident(EffectVariableModel effect) {
//		String sentence = effect.getRelationship() + "(";
//		for (int k = 0; k < effect.getArgumentList().size(); k++) {				
//			sentence = sentence + effect.getArgumentList().get(k) + ", ";
//		}
//		int index = sentence.lastIndexOf(", ");
//		sentence = sentence.substring(0, index);
//		sentence = sentence + ")";
//		
//		residentNodeFather.setName(sentence);
//		residentNodeFather.setNodeType(NodeType.RESIDENT);
//		residentNodeFather.setEventVariable(effect);
//	}
	
	/**
	 * Map all {@link EffectVariableModel} as {@link ResidentNodeExtension} and set as father node the node 
	 * mapped in cause relation.
	 * @throws OVariableAlreadyExistsInArgumentList 
	 * @throws ArgumentNodeAlreadySetException 
	 * @throws InvalidParentException 
	 */
	public void mapAllEffectsAsResident(Object nodeFather) throws
		ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList, InvalidParentException {
		
		// Get all effects from rule
		for (int l = 0; l < rule.getEffectVariableList().size(); l++) {
			
			EffectVariableModel effect = rule.getEffectVariableList().get(l);
			UndefinedNode undefinedNode = mappingController.getUndefinedNodeRelatedToEffect(
					effect, mfragExtensionActive);
			
			ResidentNodeExtension residentNode = null;
			
			if(undefinedNode != null) { 
				// Map the effects to residentNodes
				residentNode = mappingController.mapToResidentNode(	undefinedNode, mfragExtensionActive);
				
			}
			else {
				// Get resident node if the effect already was mapped as resident
				residentNode = searchResidentNodeRelated(effect);
				if(residentNode == null) {
					Debug.println(DefineDependenceRelation.class + " -- EffectVariableModel not valid");
				}
			}
			
			if(residentNode != null) {
				
				mapResidentNodeArgument(effect, residentNode);
				
				// Create an edge linking the parent node to the child node related to ResidentNode created from EffectModel
				if(nodeFather.getClass().equals(ResidentNodeExtension.class)) {
					
					Edge auxEdge = new Edge((ResidentNodeExtension)nodeFather, residentNode);
					
					try {
						mfragExtensionActive.addEdge(auxEdge);
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
//				else if (nodeFather.getClass().equals(InputNodeExtension.class)) {
//					
//					Edge auxEdge = new Edge(, (ResidentNodeExtension)nodeFather);
//					
//					try {
//						mfragExtensionActive.addEdge(auxEdge);
//					} catch (MEBNConstructionException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (CycleFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					
//				} else {
//					System.err.println("Parent Node related to EffectModel undefined");
//				}
			
			}
		}
	}
	
	public void mapCauseAsInput(CauseVariableModel cause) {
		String sentence = cause.getRelationship() + "(";
		for (int k = 0; k < cause.getOvArgumentList().size(); k++) {				
			sentence = sentence + cause.getOvArgumentList().get(k).getVariable() + ", ";
		}
		int index = sentence.lastIndexOf(", ");
		sentence = sentence.substring(0, index);
		sentence = sentence + ")";
		
		String id = cause.getId();
//		nodeFather = new NodeInputModel(id, sentence, NodeType.INPUT, cause);
	}
	
	/**
	 * This method consider that the {@link ResidentNodeExtension} is not null so it basically add the arguments related
	 * to the {@link ResidentNode}
	 * 
	 * @param cause
	 * @param residentNode
	 * @throws ArgumentNodeAlreadySetException
	 * @throws OVariableAlreadyExistsInArgumentList
	 */
	public void mapResidentNodeArgument(Object node, ResidentNodeExtension residentNode) 
			throws ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList {
				
		List<OrdinaryVariableModel> ovEventModelList = null;
		
		// Verify if the event it is cause or effect
		if(node.getClass().equals(CauseVariableModel.class)) { 
			ovEventModelList = ((CauseVariableModel)node).getOvArgumentList();
		}
		else { // the event it is effect
			ovEventModelList = ((EffectVariableModel)node).getOvArgumentList();
		}
		
		// Add arguments related to the event	
		for (int i = 0; i < ovEventModelList.size(); i++) {
			OrdinaryVariableModel ovModel = ovEventModelList.get(i);
			
			// OrdinaryVariable from MEBN
			List<OrdinaryVariable> ovList = mfragExtensionActive.getOrdinaryVariableList();
			for (int j = 0; j < ovList.size(); j++) {
				
				OrdinaryVariable ov = ovList.get(j);
				if ((ovModel.getVariable().equals(ov.getName()) &&
						(ovModel.getTypeEntity().equals(ov.getValueType().toString())))) {
					
					residentNode.addArgument(ov, true);
				}
			}
		}
	}
	
	/**
	 * Verify if {@link CauseVariableModel} or {@link EffectVariableModel} defined in {@link RuleModel} were mapped as 
	 * {@link ResidentNodeExtension} in {@link MFragExtension} related to {@link RuleModel}. If it is, then return
	 * {@link ResidentNodeExtension} identified.
	 * @param cause or effect
	 * @return residentNode
	 */
	public ResidentNodeExtension searchResidentNodeRelated(Object event) {
		
		// Resident nodes selected in first criterion of selection
		List<ResidentNodeExtension> residentNodeExtensionList = mfragExtensionActive.getResidentNodeExtensionList();
		
		for (int i = 0; i < residentNodeExtensionList.size(); i++) {
			
			// Resident node is random variable related to an attribute or relationship.
			if (residentNodeExtensionList.get(i).getEventRelated().getClass()
					== RelationshipModel.class) {
				
				ResidentNodeExtension residentNode = residentNodeExtensionList.get(i);
				RelationshipModel relationshipModel = (RelationshipModel)residentNode.getEventRelated();

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
	 * Verify if cause relationship defined in rule was mapped as 
	 * input node in MFrag related to rule. If it is, then return
	 * input node identified.
	 * @param cause
	 * @return inputNode
	 */
//	public NodeInputModel searchInputNode(CauseVariableModel cause) {
//		
//		List<NodeInputModel> inputNodeList = mfragSelected.getNodeInputList();
//		
//		for (int i = 0; i < inputNodeList.size(); i++) {
//			
//			// Input node is random variable related to an attribute or relationship.
//			if (inputNodeList.get(i).getEventVariable().getClass()
//					== RelationshipModel.class) {
//				
//				NodeInputModel inputNode = inputNodeList.get(i);
//				RelationshipModel relationshipModel = (RelationshipModel)inputNode.getEventVariable();
//				
//				if (relationshipModel.equals(cause.getRelationshipModel())) {
//					return inputNode;
//				}
//			}
//		}
//		return null;
//			
//	}
}
