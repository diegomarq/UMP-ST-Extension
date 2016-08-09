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
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.model.umpst.implementation.EffectVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.NodeContextModel;
import unbbayes.model.umpst.implementation.node.NodeInputModel;
import unbbayes.model.umpst.implementation.node.NodeObjectModel;
import unbbayes.model.umpst.implementation.node.NodeType;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

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
	
	private MFragExtension mfragExtension;
	
	public SecondCriterionOfSelection(UMPSTProject umpstProject,
			MappingController mappingController, MultiEntityBayesianNetwork mebn) {
		
		this.umpstProject = umpstProject;
		this.mappingController = mappingController;
		
		mapGroup = new HashMap<String, GroupModel>();
		mapRule = new HashMap<String, RuleModel>();
		mapDoubtNodes = new HashMap<String, NodeObjectModel>();
		
//		secondSelection(mebn);		
	}
	
	/**
	 * Second Selection based on Second Criterion Of Selection algorithm.
	 */
//	public void secondSelection() {
//		List<RuleModel> listRules = new ArrayList<RuleModel>();
//		DefineDependenceRelation mfragRelation;
//		
//		// Verify list of rules related to each group		
//		List<MFragExtension> mfragExtensionList = mebnExtension.getMFragExtensionList();
//		for (MFragExtension mfragExtension : mfragExtensionList) {	
//			
//			GroupModel group = mfragExtension.getGroupPointer();
//			
//			listRules = group.getBacktrackingRules();
//			for (int i = 0; i < listRules.size(); i++) {
//				
//				// Compare if rule and group have the same elements
//				RuleModel rule = group.getBacktrackingRules().get(i);
//				if (compareElements(rule, group)) {
//					
//					
//					mappingController.addOrdinaryVariable(rule, mfragExtension);
////					mappingController.addNecessaryConditionFromRule(rule, mfragExtension);
////					mfragRelation = new DefineDependenceRelation(rule, group, mappingController,
////							umpstProject, this);
////					searchOVMissing(rule, group);
////					defineMFragCausal(rule, group);
//					
//				} else {
//					System.err.println("Number of element in rule: " + rule.getId() +
//							" " + "does not match with group: " + group.getId());
//					
//					// Elements of rule that does not check
//					for (int j = 0; j < objectModel.size(); j++) {
//						System.err.println(objectModel.get(j).getName());
//					}
//				}
//			}
//			if (group.getBacktrackingRules().size() == 0) {
//				
//				// Add missing OrdinaryVariables
////				insertMissingOV(group);
//			}
//		}
//	}
	
	public void classifyInputNode(RuleModel rule, GroupModel group) {
		
		List<RuleModel> causeIsEffectList = new ArrayList<RuleModel>();
		
		mapRule = umpstProject.getMapRules();
		Set<String> keys = mapRule.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
				
		int flag = 0;
		for (String key : sortedKeys) {
			RuleModel ruleSelected = mapRule.get(key);
			if (!(ruleSelected.getId().equals(rule.getId()))) {
				
				for (int k = 0; k < rule.getCauseVariableList().size(); k++) {
					CauseVariableModel cause = rule.getCauseVariableList().get(k);
				
					for (int j = 0; j < ruleSelected.getEffectVariableList().size(); j++) {	
						EffectVariableModel effect = ruleSelected.getEffectVariableList().get(j);
						
						if (cause.getRelationshipModel().equals(effect.getRelationshipModel())) {
							// cause is effect
							causeIsEffectList.add(ruleSelected);
							flag++;
						}
					}
				}
			}
		}
		
		if (causeIsEffectList.size() > 0) {
			mapRule = umpstProject.getMapRules();
			Set<String> keys2 = mapRule.keySet();
			TreeSet<String> sortedKeys2 = new TreeSet<String>(keys2);
					
			for (String key2 : sortedKeys2) {
				RuleModel ruleSelected = mapRule.get(key2);
			
				for (int i = 0; i < causeIsEffectList.size(); i++) {
					String idCauseIsEffect = causeIsEffectList.get(i).getId();
					
					if (!(ruleSelected.getId().equals(idCauseIsEffect))) {
						
						for (int k = 0; k < ruleSelected.getCauseVariableList().size(); k++) {
							CauseVariableModel cause = ruleSelected.getCauseVariableList().get(k);
							String id = cause.getId();
							
							String sentence = cause.getRelationship() + "(";
							for (int k1 = 0; k1 < cause.getArgumentList().size(); k1++) {				
								sentence = sentence + cause.getArgumentList().get(k1) + ", ";
							}
							int index = sentence.lastIndexOf(", ");
							sentence = sentence.substring(0, index);
							sentence = sentence + ")";
							
							NodeInputModel inputNode = new NodeInputModel(id, sentence, NodeType.INPUT, cause);
							
							List<GroupModel> idGroupList = ruleSelected.getFowardtrackingGroupList();
							if (idGroupList.size() == 1) {
//								mappingController.addInputNodeInMFrag(idGroupList.get(0).getId(), inputNode);
							} else {
								System.err.println("Error classifyInputNode. Rule is in more than one group.");
							}
						}				
					}
				}
			}
		}
	}
	
	public void setOthersResidentNode() {
		
//		// Set resident node as effect event present in rule related to group.
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
	 * Insert missing Ordinary Variables as context node in groups that
	 * do not have definition rule.
	 */
	public void insertMissingOV(GroupModel group) {
		OrdinaryVariableModel ov;
		
		for (int i = 0; i < group.getBacktrackingEntities().size(); i++) {
			EntityModel entity = group.getBacktrackingEntities().get(i);
			
			String id = Integer.toString(i);
			String variable = entity.getName().toLowerCase();
			String typeEntity = entity.getName();
			
			ov = new OrdinaryVariableModel(id, variable, typeEntity, entity);
			
			String name = "isA( " + variable + ", " + typeEntity + " )" ;
			
			NodeContextModel contextNode = new NodeContextModel(
					ov.getId(), name, NodeType.CONTEXT, ov);
			
//			mappingController.addContextNodeInMFrag(group.getId(), contextNode);
		}
	}
	
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
