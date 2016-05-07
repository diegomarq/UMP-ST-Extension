/**
 * 
 */
package unbbayes.model.umpst.implementation;

import java.util.ArrayList;

import unbbayes.model.umpst.entity.AttributeModel;
import unbbayes.model.umpst.entity.RelationshipModel;


/**
 * Variable object of Effect
 * @author Diego Marques
 */
public class EffectVariableModel extends EventVariableObjectModel {
	
	private String id;
	private String relationship;
	private RelationshipModel relationshipModel;
	private ArrayList<String> argumentList;
	private String attribute;
	private AttributeModel attributeModel;

	/**
	 * Constructor of effect variable object
	 */
	public EffectVariableModel(String id) {
		super(id, EventType.EFFECT);
		this.id = id;
		this.relationship = null;
		this.attribute = null;
		
		argumentList = new ArrayList<String>();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the relationship
	 */
	public String getRelationship() {
		return relationship;
	}

	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}	

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * @return the argumentList
	 */
	public ArrayList<String> getArgumentList() {
		return argumentList;
	}

	/**
	 * @param argumentList the argumentList to set
	 */
	public void setArgumentList(ArrayList<String> argumentList) {
		this.argumentList = argumentList;
	}

	/**
	 * @return the relationshipModel
	 */
	public RelationshipModel getRelationshipModel() {
		return relationshipModel;
	}

	/**
	 * @param relationshipModel the relationshipModel to set
	 */
	public void setRelationshipModel(RelationshipModel relationshipModel) {
		this.relationshipModel = relationshipModel;
	}

	/**
	 * @return the attributeModel
	 */
	public AttributeModel getAttributeModel() {
		return attributeModel;
	}

	/**
	 * @param attributeModel the attributeModel to set
	 */
	public void setAttributeModel(AttributeModel attributeModel) {
		this.attributeModel = attributeModel;
	}
}
