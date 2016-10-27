/**
 * 
 */
package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.exception.IncompatibleQuantityException;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.model.umpst.implementation.EventMappingType;
import unbbayes.model.umpst.implementation.node.InputNodeExtension;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.ResidentNodeExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.prs.exception.InvalidParentException;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.exception.ArgumentNodeAlreadySetException;
import unbbayes.prs.mebn.exception.OVDontIsOfTypeExpected;
import unbbayes.prs.mebn.exception.OVariableAlreadyExistsInArgumentList;

/**
 * Maps an event related to the model to resident or input node manually by the user.
 * Create a panel showing the undefined nodes to the user selects as resident or input node.
 * 
 * @author Diego Marques
 */
public class ThirdCriterionOfSelection {

	private MappingController mappingController;	
	
	public ThirdCriterionOfSelection(MappingController mappingController, MultiEntityBayesianNetwork mebn) {
		
		this.mappingController = mappingController;
		try {
			try {
				thirdCriterion(mebn);
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
		} catch (IncompatibleQuantityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void thirdCriterion(MultiEntityBayesianNetwork mebn) throws IncompatibleQuantityException, ArgumentNodeAlreadySetException, OVariableAlreadyExistsInArgumentList, InvalidParentException {
		
		List<UndefinedNode> undefinedNodeList = mappingController.getUndefinedNodeList();
		if(undefinedNodeList.size() > 0) {
			
			mappingController.createThirdCriterionPanel(undefinedNodeList, mebn);
			
			// The nodes were mapped by the user
			List<UndefinedNode> hypothesisListCase = mappingController.getHypothesisListCase();
			
			// Map to resident node
			for (int i = 0; i < hypothesisListCase.size(); i++) {
				
				UndefinedNode nodeMapped = hypothesisListCase.get(i);
				if(nodeMapped.getMappingType().equals(EventMappingType.RESIDENT)) {
					
					CauseVariableModel causeRelated = (CauseVariableModel)nodeMapped.getEventRelated();					
					RelationshipModel relationship = causeRelated.getRelationshipModel();
					MFragExtension mfragRelated = nodeMapped.getMfragExtension();
					
					ResidentNodeExtension residentNode = mappingController.mapToResidentNode(
							relationship, mfragRelated, nodeMapped.getEventRelated());
				}				
			}
			
			// Map to input node
			for (int i = 0; i < hypothesisListCase.size(); i++) {
				
				UndefinedNode nodeMapped = hypothesisListCase.get(i);
				if(nodeMapped.getMappingType().equals(EventMappingType.INPUT)) {
					
					CauseVariableModel causeRelated = (CauseVariableModel)nodeMapped.getEventRelated();
					MFragExtension mfragRelated = nodeMapped.getMfragExtension();
					
					ResidentNodeExtension residentNodeRelated = mappingController.getResidentNodeRelatedToCauseIn(
							causeRelated, mfragRelated);					

					try {
						InputNodeExtension inputNode = mappingController.mapToInputNode(causeRelated, mfragRelated, residentNodeRelated);
						mappingController.mapAllEffectsToResident(inputNode, mfragRelated, nodeMapped.getRuleRelated());
					} catch (OVDontIsOfTypeExpected e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ArgumentNodeAlreadySetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}			
		}		
	}
}
