package unbbayes.model.umpst.implementation.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.ObjectModel;
import unbbayes.model.umpst.entity.EntityModel;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.NodeObjectModel;
import unbbayes.model.umpst.implementation.node.ResidentNodeExtension;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.Argument;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.exception.ArgumentNodeAlreadySetException;
import unbbayes.prs.mebn.exception.OVariableAlreadyExistsInArgumentList;

/**
 * This criterion classifies not defined nodes present in MFrags as context, input
 * or resident nodes and set the dependencies between each other.  
 * @author Diego Marques
 */
public class SecondCriterionOfSelection {
	
	private UMPSTProject umpstProject;
	private MappingController mappingController;	
	
	private Map<String, GroupModel> mapGroup;
	private Map<String, RuleModel> mapRule;
	private Map<String, NodeObjectModel> mapDoubtNodes;
	private List<ObjectModel> objectModel;
	
	
	public SecondCriterionOfSelection(UMPSTProject umpstProject,
			MappingController mappingController, MultiEntityBayesianNetwork mebn) {
		
		this.umpstProject = umpstProject;
		this.mappingController = mappingController;
		
		mapGroup = new HashMap<String, GroupModel>();
		mapRule = new HashMap<String, RuleModel>();
		mapDoubtNodes = new HashMap<String, NodeObjectModel>();
		
		secondSelection(mebn);		
	}
	
	/**
	 * Second Selection based on Second Criterion Of Selection algorithm.
	 */
	public void secondSelection(MultiEntityBayesianNetwork mebn) {
		List<RuleModel> listRules = new ArrayList<RuleModel>();
		DefineDependenceRelation ruleRelation;
		
		// Verify list of rules related to each group		
		Map<String, MFragExtension> mapMFragExtension = mappingController.getMapMFragExtension();
		Set<String> keys = mapMFragExtension.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String groupId : sortedKeys) {	
			MFragExtension mfragExtension = mapMFragExtension.get(groupId);
			GroupModel group = mfragExtension.getGroupRelated();
			
			// Mapping of causal relation defined by rule
			listRules = group.getBacktrackingRules();
			for (int i = 0; i < listRules.size(); i++) {
				
				// Compare if rule and group have the same elements
				RuleModel rule = group.getBacktrackingRules().get(i);
				if (compareElements(rule, group)) {
					
//					mappingController.addOrdinaryVariable(rule, mfragExtension);
//					mappingController.addNecessaryConditionFromRule(rule, mfragExtension);
					ruleRelation = new DefineDependenceRelation(rule, group, mfragExtension, mappingController,
							umpstProject, this);
//					searchOVMissing(rule, group);
//					defineMFragCausal(rule, group, mfragExtension);
					
				} else {
					System.err.println("Number of element in rule: " + rule.getId() +
							" " + "does not match with group: " + group.getId());
					
					// Elements of rule that does not check
					for (int j = 0; j < objectModel.size(); j++) {
						System.err.println(objectModel.get(j).getName());
					}
				}
			}
			
			// Add missing OrdinaryVariables. Entities present in group but not defined in any rule.
//			insertMissingOV(group);
			
			/**
			 * Map relationships that are not defined in a rule. This kind of relationship can be in a group that
			 * has rules.
			 */
			List<RelationshipModel> relationshipModelList = group.getBacktrackingRelationship();
			for (int i = 0; i < relationshipModelList.size(); i++) {
				
				RelationshipModel relationship = relationshipModelList.get(i);
				if(relationship.getFowardtrackingRules().size() == 0) {
					
					 /**
					  * First verify if there any residentNode related to the relationshipModel that was
					  * created during the First Criteria of Selection. If it exists, then inserMissingOrdinaryVariable
					  * related to the node. Case it was not created, then map the relationshipModel to residentNode.
					  */
					ResidentNodeExtension residentNode = mappingController.getResidentNodeRelatedTo(relationship, mfragExtension);
					if(residentNode == null) {
						residentNode = mappingController.mapToResidentNode(relationship, mfragExtension, null);
					}
					
					try {
						insertMissingOrdinaryVariableIn(residentNode, mfragExtension);
					} catch (ArgumentNodeAlreadySetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OVariableAlreadyExistsInArgumentList e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void classifyInputNode(RuleModel rule, GroupModel group) {
		
//		List<RuleModel> causeIsEffectList = new ArrayList<RuleModel>();
//		
//		mapRule = umpstProject.getMapRules();
//		Set<String> keys = mapRule.keySet();
//		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
//				
//		int flag = 0;
//		for (String key : sortedKeys) {
//			RuleModel ruleSelected = mapRule.get(key);
//			if (!(ruleSelected.getId().equals(rule.getId()))) {
//				
//				for (int k = 0; k < rule.getCauseVariableList().size(); k++) {
//					CauseVariableModel cause = rule.getCauseVariableList().get(k);
//				
//					for (int j = 0; j < ruleSelected.getEffectVariableList().size(); j++) {	
//						EffectVariableModel effect = ruleSelected.getEffectVariableList().get(j);
//						
//						if (cause.getRelationshipModel().equals(effect.getRelationshipModel())) {
//							// cause is effect
//							causeIsEffectList.add(ruleSelected);
//							flag++;
//						}
//					}
//				}
//			}
//		}
//		
//		if (causeIsEffectList.size() > 0) {
//			mapRule = umpstProject.getMapRules();
//			Set<String> keys2 = mapRule.keySet();
//			TreeSet<String> sortedKeys2 = new TreeSet<String>(keys2);
//					
//			for (String key2 : sortedKeys2) {
//				RuleModel ruleSelected = mapRule.get(key2);
//			
//				for (int i = 0; i < causeIsEffectList.size(); i++) {
//					String idCauseIsEffect = causeIsEffectList.get(i).getId();
//					
//					if (!(ruleSelected.getId().equals(idCauseIsEffect))) {
//						
//						for (int k = 0; k < ruleSelected.getCauseVariableList().size(); k++) {
//							CauseVariableModel cause = ruleSelected.getCauseVariableList().get(k);
//							String id = cause.getId();
//							
//							String sentence = cause.getRelationship() + "(";
//							for (int k1 = 0; k1 < cause.getArgumentList().size(); k1++) {				
//								sentence = sentence + cause.getArgumentList().get(k1) + ", ";
//							}
//							int index = sentence.lastIndexOf(", ");
//							sentence = sentence.substring(0, index);
//							sentence = sentence + ")";
//							
//							NodeInputModel inputNode = new NodeInputModel(id, sentence, NodeType.INPUT, cause);
//							
//							List<GroupModel> idGroupList = ruleSelected.getFowardtrackingGroupList();
//							if (idGroupList.size() == 1) {
////								mappingController.addInputNodeInMFrag(idGroupList.get(0).getId(), inputNode);
//							} else {
//								System.err.println("Error classifyInputNode. Rule is in more than one group.");
//							}
//						}				
//					}
//				}
//			}
//		}
	}
	
	public void setOthersResidentNode() {
		
		// Set resident node as effect event present in rule related to group.
//		for (int i = 0; i < rule.getEffectVariableList().size(); i++) {
//			
//			EffectVariableModel effect = rule.getEffectVariableList().get(i);
//			if (effect.getRelationship() != null) {
//				
//				String id = effect.getId();
//				String sentence = effect.getRelationship() + "(";
//				for (int j = 0; j < effect.getArgumentList().size(); j++) {				
//					sentence = sentence + effect.getArgumentList().get(j) + ", ";
//				}
//				int index = sentence.lastIndexOf(", ");
//				sentence = sentence.substring(0, index);
//				sentence = sentence + ")";
//			
//				NodeResidentModel residentNode = new NodeResidentModel(id, sentence,
//						NodeType.RESIDENT, effect);
//				
//				generateMTheoryController.addNodeResidentInMFrag(group.getId(), residentNode);
//				
//			} else {
//				System.err.println("Error EffectModel, RULE: "+ rule.getId() + 
//						". Effect relationship not define");
//			}
//			
//		}
	}
	
	/**
	 * Add the {@link OrdinaryVariable} related to {@link ResidentNodeExtension} mapped from {@link RelationshipModel}
	 * that was not present in any {@link RuleModel}
	 * @param relationship
	 * @throws OVariableAlreadyExistsInArgumentList 
	 * @throws ArgumentNodeAlreadySetException 
	 */
	public void insertMissingOrdinaryVariableIn(ResidentNodeExtension residentNode, MFragExtension mfragExtension)
			throws ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList {
		
		RelationshipModel relationship = (RelationshipModel)residentNode.getEventRelated();
		
		for (int i = 0; i < relationship.getEntityList().size(); i++) {
			EntityModel entity = relationship.getEntityList().get(i);
			
			String id = Integer.toString(i);
			String variable = entity.getName().toLowerCase();
			String typeEntity = entity.getName();
			
			OrdinaryVariableModel ovModel = new OrdinaryVariableModel(id, variable, typeEntity, entity);
//				String name = "isA( " + variable + ", " + typeEntity + " )";
//			NodeContextModel contextNode = new NodeContextModel(
//					ov.getId(), name, NodeType.CONTEXT, ov);
			//			mappingController.addContextNodeInMFrag(group.getId(), contextNode);
			
			// Map the ordinaryVariableModel to the ordinaryVariable and add it to the mfragExtension
			OrdinaryVariable ov = mappingController.mapToOrdinaryVariable(ovModel, mfragExtension);
			
			// Add the ordinaryVariable to residentNode
			residentNode.addArgument(ov, true);
		}
	}
	
//	/**
//	 * Create missing {@link OrdinaryVariable} present in the {@link GroupModel} and are not
//	 * related to any RuleModel.
//	 */
//	public void insertMissingOVOf(GroupModel group) {
//		
//		for (int i = 0; i < group.getBacktrackingEntities().size(); i++) {			
//			EntityModel entity = group.getBacktrackingEntities().get(i);
//			
//			// keep entities that are not related to a rule
//			if(entity.getFowardTrackingRules().size() == 0) {
//			
//				String id = Integer.toString(i);
//				String variable = entity.getName().toLowerCase();
//				String typeEntity = entity.getName();
//				
//				OrdinaryVariableModel ovModel = new OrdinaryVariableModel(id, variable, typeEntity, entity);
//	//				String name = "isA( " + variable + ", " + typeEntity + " )";
//	//			NodeContextModel contextNode = new NodeContextModel(
//	//					ov.getId(), name, NodeType.CONTEXT, ov);
//				//			mappingController.addContextNodeInMFrag(group.getId(), contextNode);
//				
//				// Map ordinary variable model to ordinary variable and add it to mfragExtension
//				OrdinaryVariable ov = mappingController.mapToOrdinaryVariable(ovModel, mfragExtension);
//			}
//		}
//	}
//	
	/**
	 * Insert missing Ordinary Variables that has rule, but it is not
	 * present.
	 * @param rule
	 * @param model
	 */
	public void searchOVMissing(RuleModel rule, GroupModel group) {
		
		int flag = 0;
		for (int j = 0; j < group.getBacktrackingEntities().size(); j++) {
			for (int i = 0; i < rule.getOrdinaryVariableList().size(); i++) {
				EntityModel entityOV = rule.getOrdinaryVariableList().get(i).getEntityObject();
				EntityModel entityGroup = group.getBacktrackingEntities().get(j);
				
				if (entityOV.getId().equals(entityGroup.getId())) {
					flag++;
					break;
				}
			}
		}
		if (flag < group.getBacktrackingEntities().size()) {
			System.err.println("Error OV definition" + "Missing Entities GROUP :" + group.getId() +
					"Missing Entities RULE :" + rule.getId());
		}
	}
	
	/**
	 * Compare if rule is according to group related.
	 * @param rule
	 * @param group
	 * @return
	 */
	public boolean compareElements(RuleModel rule, GroupModel group) {
		int confirm = 0;
		int actflag = 0;
		int lstflag = 0;
		
		objectModel = new ArrayList<ObjectModel>();
		
		// Attributes
		for (int i = 0; i < rule.getAttributeList().size(); i++) {
			for (int j = 0; j < group.getBacktrackingAtributes().size(); j++) {
				String idRuleAtt = rule.getAttributeList().get(i).getId();
				String idGroupAtt = group.getBacktrackingAtributes().get(j).getId();
				if (idRuleAtt.equals(idGroupAtt)) {
					actflag++;
					lstflag = actflag;
					break;
				} else {
					lstflag++;
				}
			}
			if (lstflag > actflag) {
				objectModel.add(rule.getAttributeList().get(i));
			}
		}
		if (actflag >= rule.getAttributeList().size()) {
			confirm++;
		}
		
		// Entities
		actflag = 0;
		lstflag = 0;
		for (int i = 0; i < rule.getEntityList().size(); i++) {
			for (int j = 0; j < group.getBacktrackingEntities().size(); j++) {
				String idRuleEnt = rule.getEntityList().get(i).getId();
				String idGroupEnt = group.getBacktrackingEntities().get(j).getId();
				if (idRuleEnt.equals(idGroupEnt)) {
					actflag++;
					lstflag = actflag;
					break;
				} else {
					lstflag++;
				}
			}		
			if (lstflag > actflag) {
				objectModel.add(rule.getEntityList().get(i));
			}
		}
		if (actflag >= rule.getEntityList().size()) {
			confirm++;
		}
		
		// Relationship
		actflag = 0;
		lstflag = 0;
		for (int i = 0; i < rule.getRelationshipList().size(); i++) {
			for (int j = 0; j < group.getBacktrackingRelationship().size(); j++) {
				String idRuleRel = rule.getRelationshipList().get(i).getId();
				String idGroupRel = group.getBacktrackingRelationship().get(j).getId();
				if (idRuleRel.equals(idGroupRel)) {
					actflag++;
					lstflag = actflag;
					break;
				} else {
					lstflag++;
				}
			}
			if (lstflag > actflag) {
				objectModel.add(rule.getRelationshipList().get(i));
			}
		}
		if (actflag >= rule.getRelationshipList().size()) {
			confirm++;
		}
		
		if (confirm == 3) {
			return true;
		}
		
		return false;		
	}
	
	/**
	 * Set ID according to the last necessary condition ID created.
	 */
	public String getOVId(RuleModel rule) {
		int id = 0;
		int greaterID = -1;
		boolean beginID = true; // created to set Id = 0
		for (int i = 0; i < rule.getOrdinaryVariableList().size(); i++) {
			if (greaterID < Integer.parseInt(rule.getOrdinaryVariableList().get(i).getId())) {
				greaterID = Integer.parseInt(rule.getOrdinaryVariableList().get(i).getId());
				beginID = false;
			}
		}
		if (!beginID) {
			id = greaterID+1;
			return Integer.toString(id);
		}
		return "0";
	}

	/**
	 * @return the mapDoubtNodes
	 */
	public Map<String, NodeObjectModel> getMapDoubtNodes() {
		return mapDoubtNodes;
	}

	/**
	 * @param mapDoubtNodes the mapDoubtNodes to set
	 */
	public void setMapDoubtNodes(Map<String, NodeObjectModel> mapDoubtNodes) {
		this.mapDoubtNodes = mapDoubtNodes;
	}

}
