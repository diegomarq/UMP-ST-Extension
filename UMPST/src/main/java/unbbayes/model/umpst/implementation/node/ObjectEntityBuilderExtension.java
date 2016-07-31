package unbbayes.model.umpst.implementation.node;

public class ObjectEntityBuilderExtension {
	
	private MebnExtension mebnExtension;
	private boolean isToCreateEntity;

	public ObjectEntityBuilderExtension(MebnExtension mebnExtension, boolean isToCreateEntity) {
		this.setMebnExtension(mebnExtension);
		this.setToCreateEntity(isToCreateEntity);
	}
	
	public ObjectEntityBuilderExtension getInstance(MebnExtension mebnExtension, boolean isToCreateEntity) {
		return new ObjectEntityBuilderExtension(mebnExtension, isToCreateEntity);
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

	/**
	 * @return the isToCreateEntity
	 */
	public boolean isToCreateEntity() {
		return isToCreateEntity;
	}

	/**
	 * @param isToCreateEntity the isToCreateEntity to set
	 */
	public void setToCreateEntity(boolean isToCreateEntity) {
		this.isToCreateEntity = isToCreateEntity;
	}

}
