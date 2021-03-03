package br.pucrio.inf.lac.mhub.s2pa.filter;

import java.util.HashSet;

import br.pucrio.inf.lac.mhub.components.MOUUID;

/**
 * @author bertodetacio
 *
 */
public class Technology implements Comparable<Technology> {

	private Integer technologyID;

	private HashSet<String> exceptionsMobileObjectSet = new HashSet<String>();

	private HashSet<String> exceptionsServicesSet = new HashSet<String>();

	/**
	 * 
	 */
	public Technology() {
		// TODO Auto-generated constructor stub
	}

	public Technology(Integer technologyID) {
		this.technologyID = technologyID;
	}

	public boolean containsMobileObjectOrService(MOUUID mouuid, String service) {
		if (containsMobileObject(mouuid) || containsService(service)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean containsMobileObject(MOUUID mouuid) {
		return exceptionsMobileObjectSet.contains(mouuid.toString());
	}

	public void removeMobileObject(MOUUID mouuid) {
		exceptionsMobileObjectSet.remove(mouuid.toString());
	}

	public void addMobileObject(MOUUID mouuid) {

		exceptionsMobileObjectSet.add(mouuid.toString());

	}

	public boolean containsService(String service) {
		return exceptionsServicesSet.contains(service);
	}

	public void addService(String service) {
		exceptionsServicesSet.add(service);

	}

	public void removeService(String service) {

		exceptionsServicesSet.remove(service);

	}

	@Override
	public int compareTo(Technology another) {
		if (this.technologyID == another.technologyID) {
			return 0;
		} else {
			return 1;
		}
	}

	public Integer getTechnologyID() {
		return technologyID;
	}

	public void setTechnologyID(Integer technologyID) {
		this.technologyID = technologyID;
	}

	public HashSet<String> getExceptionsServicesSet() {
		return exceptionsServicesSet;
	}

	public HashSet<String> getExceptionsMobileObjectSet() {
		return exceptionsMobileObjectSet;
	}

	@Override
	public boolean equals(Object object) {

		Technology another = (Technology) object;

		if (this.getTechnologyID() == null && another.getTechnologyID() == null) {
			return true;
		}

		else if (this.getTechnologyID() == null
				&& another.getTechnologyID() != null) {
			return false;
		}

		else if (this.getTechnologyID() != null
				&& another.getTechnologyID() == null) {
			return false;
		}

		else {
			return ((int) this.getTechnologyID() == (int) another
					.getTechnologyID());
		}
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub

		if ((this.getTechnologyID() != null)) {
			return this.getTechnologyID().hashCode();
		}

		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(getTechnologyID());
	}

}
