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
import unbbayes.model.umpst.implementation.EnumType;
import unbbayes.model.umpst.implementation.EventNCPointer;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.NodeFormulaTree;
import unbbayes.model.umpst.implementation.node.NodeInputModel;
import unbbayes.model.umpst.implementation.node.NodeObjectModel;
import unbbayes.model.umpst.implementation.node.NodeResidentModel;
import unbbayes.model.umpst.implementation.node.NodeType;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;

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
	
	private MFragModel mfragSelected;
	private NodeObjectModel nodeFather;
	private NodeObjectModel notDefinedNode;
//	private NodeResidentModel residentNodeFather;
	
	private MappingController generateMTheoryController;
	private SecondCriterionOfSelection secondCriterion;
	
	private UMPSTProject umpstProject;
	
	public DefineDependenceRelation(RuleModel rule, GroupModel group, MappingController generateMTheoryController, 
			UMPSTProject umpstProject, SecondCriterionOfSelection secondCriterion) {
		
		this.rule = rule;
		this.group = group;
		this.generateMTheoryController = generateMTheoryController;
		this.umpstProject = umpstProject;
		this.secondCriterion = secondCriterion;
		
		selectMFragRelated();
		mapCausalRelation();
		mapMissingRelationships();
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
			NodeFormulaTree formulaTree = nc.getFormulaTree();
			
			exists = compareNodeFormula(formulaTree, relationship);
			if (exists) {
				System.out.println("--"+group.getName()+"--");
				System.out.println(relationship.getName());
				return true;
			}
		}
		return false;
	}
	
	public boolean compareNodeFormula(NodeFormulaTree node, RelationshipModel relationship) {
		if (node.getTypeNode() == EnumType.OPERAND) {
			EventNCPointer event = (EventNCPointer)node.getNodeVariable();
			RelationshipModel ncRelationship = event.getEventVariable().getRelationshipModel();
			if (ncRelationship.equals(relationship)) {
				return true;
			}
			if (node.getChildren().size() > 0) {
				compareNodeFormula(node.getChildren().get(0), relationship);			
			}
		}
		return false;
	}
		
	public void mapCausalRelation() {
		for (int j = 0; j < rule.getCauseVariableList().size(); j++) {				
			CauseVariableModel cause = rule.getCauseVariableList().get(j);

			// Cause was mapped as resident node in first criterion of selection
			NodeResidentModel residentNode = searchResidentNode(cause);
			
			if (residentNode != null) {
				nodeFather = residentNode;
				mapCauseAsResident(cause);
				mapAllEffectsAsResident();
			}
			// Cause is Effect in other rule
			else if(existsAsEffect(cause)) {
				mapCauseAsInput(cause);
				mapAllEffectsAsResident();
			}
			// Verify if there are nodes that were not mapped. 
			// Usually, these are nodes that were not mapped in other rule as effect.
			else if(searchInputNode(cause) == null) {
				String key = mfragSelected.getId();				
				mapCauseAsNotDefined(cause);
				
				secondCriterion.getMapDoubtNodes().put(key, notDefinedNode);
			}
		}
	}
	
	public void mapCauseAsNotDefined(CauseVariableModel cause) {
		String sentence = cause.getRelationship() + "(";
		for (int k = 0; k < cause.getArgumentList().size(); k++) {				
			sentence = sentence + cause.getArgumentList().get(k) + ", ";
		}
		int index = sentence.lastIndexOf(", ");
		sentence = sentence.substring(0, index);
		sentence = sentence + ")";
		
		String id = cause.getId();
		notDefinedNode = new NodeInputModel(id, sentence, NodeType.NOT_DEFINED, cause);
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
	 * Map all effects as resident node and set as father node the node mapped in cause relation.
	 * On this method residentNode
	 */
	public void mapAllEffectsAsResident() {
		for (int l = 0; l < rule.getEffectVariableList().size(); l++) {
			EffectVariableModel effect = rule.getEffectVariableList().get(l);
			String id = effect.getId();
						
			String sentence = effect.getRelationship() + "(";
			for (int k = 0; k < effect.getArgumentList().size(); k++) {				
				sentence = sentence + effect.getArgumentList().get(k) + ", ";
			}
			int indexChild = sentence.lastIndexOf(", ");
			sentence = sentence.substring(0, indexChild);
			sentence = sentence + ")";
			
			NodeResidentModel residentNodeChild = new NodeResidentModel(id, sentence, 
					NodeType.RESIDENT, effect);
			residentNodeChild.addFatherNode(nodeFather);
			nodeFather.addChildrenNode(residentNodeChild);
			
//			generateMTheoryController.addResidentNodeInMFrag(group.getId(), residentNodeChild);
		}
		if (nodeFather.getNodeType() == NodeType.RESIDENT_CAUSE) {
//			generateMTheoryController.updateResidentNodeInMFrag(group.getId(), (NodeResidentModel)nodeFather);
		}
		else if (nodeFather.getNodeType() == NodeType.INPUT) {
//			generateMTheoryController.updateInputNodeInMFrag(group.getId(), (NodeInputModel)nodeFather);
		}
	}
	
	public void mapCauseAsInput(CauseVariableModel cause) {
		String sentence = cause.getRelationship() + "(";
		for (int k = 0; k < cause.getArgumentList().size(); k++) {				
			sentence = sentence + cause.getArgumentList().get(k) + ", ";
		}
		int index = sentence.lastIndexOf(", ");
		sentence = sentence.substring(0, index);
		sentence = sentence + ")";
		
		String id = cause.getId();
//		nodeFather = new NodeInputModel(id, sentence, NodeType.INPUT, cause);
	}
	
	public void mapCauseAsResident(CauseVariableModel cause) {
		String sentence = cause.getRelationship() + "(";
		for (int k = 0; k < cause.getArgumentList().size(); k++) {				
			sentence = sentence + cause.getArgumentList().get(k) + ", ";
		}
		int index = sentence.lastIndexOf(", ");
		sentence = sentence.substring(0, index);
		sentence = sentence + ")";
		
		if (nodeFather != null) {
			nodeFather.setName(sentence);
			nodeFather.setNodeType(NodeType.RESIDENT_CAUSE);
			nodeFather.setEventVariable(cause);
		} else {
			String id = cause.getId();			
			NodeResidentModel residentNode = new NodeResidentModel(id, sentence,
					NodeType.RESIDENT_CAUSE, cause);
			nodeFather = residentNode;
		}
		
	}
	
	/**
	 * Verify if cause relationship defined in rule was mapped as 
	 * resident node in MFrag related to rule. If it is, then return
	 * resident node identified.
	 * @param cause
	 * @return residentNode
	 */
	public NodeResidentModel searchResidentNode(CauseVariableModel cause) {
		
		// Resident nodes selected in first criterion of selection
		List<NodeResidentModel> residentNodeList = mfragSelected.getNodeResidentList();
		
//		boolean isResident = false;
		for (int i = 0; i < residentNodeList.size(); i++) {
			
			// Resident node is random variable related to an attribute or relationship.
			if (residentNodeList.get(i).getEventVariable().getClass()
					== RelationshipModel.class) {
				
				NodeResidentModel residentNode = residentNodeList.get(i);
				RelationshipModel relationshipModel = (RelationshipModel)residentNode.getEventVariable();
				
				// Can exist more than one cause with same name, but with different arguments.
				if (relationshipModel.equals(cause.getRelationshipModel())) {
					return residentNode;
				}
			}
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
	public NodeInputModel searchInputNode(CauseVariableModel cause) {
		
		List<NodeInputModel> inputNodeList = mfragSelected.getNodeInputList();
		
		for (int i = 0; i < inputNodeList.size(); i++) {
			
			// Input node is random variable related to an attribute or relationship.
			if (inputNodeList.get(i).getEventVariable().getClass()
					== RelationshipModel.class) {
				
				NodeInputModel inputNode = inputNodeList.get(i);
				RelationshipModel relationshipModel = (RelationshipModel)inputNode.getEventVariable();
				
				if (relationshipModel.equals(cause.getRelationshipModel())) {
					return inputNode;
				}
			}
		}
		return null;
			
	}
	
	/**
	 * Select MFrag related to group.
	 */
	public void selectMFragRelated() {
//		List<MFragModel> mfrag = generateMTheoryController.getMTheory().getMFragList();
//		mfragSelected = null;
//		
//		for (int i = 0; i < mfrag.size(); i++) {
//			if (mfrag.get(i).getId().equals(group.getId())) {
//				mfragSelected = mfrag.get(i);
//				break;
//			}
//		}
	}
}

