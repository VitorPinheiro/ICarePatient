package br.pucrio.inf.lac.mhub.s2pa.filter;
/**
 * 
 */


import java.util.HashSet;

import br.pucrio.inf.lac.mhub.components.MOUUID;

/**
 * @author bertodetacio
 *
 */
public class Service implements Comparable<Service>{
	
	private String serviceName;
	
	private HashSet<Integer>  exceptionsTecnologiesSet = new HashSet <Integer>();
	
	private HashSet<String>  exceptionsMobileObjectSet = new HashSet <String>();
	
	/**
	 * 
	 */
	public Service() {
		// TODO Auto-generated constructor stub
	}
	
	public Service(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public boolean containsTechnologyOrMobileObjectExclusively(MOUUID mouuid){
		return (containsMobileObject(mouuid) ^ containsTechnology((int)mouuid.getTechnologyID()));		
	}
	
	public boolean containsTechnologyOrMobileObject(MOUUID mouuid){
		return (containsMobileObject(mouuid) || containsTechnology((int)mouuid.getTechnologyID()));		
	}
	
	public void addTechnology(Integer technologyID){
		
		removeObjectsOfThisTechnology(technologyID);
		
		exceptionsTecnologiesSet.add(technologyID);
	}
	
	public void removeTechnology(Integer technologyID){
		exceptionsTecnologiesSet.remove(technologyID);
		removeObjectsOfThisTechnology(technologyID);
	}
	
	private void removeObjectsOfThisTechnology(Integer technologyID){
		
		HashSet<String> objects = new HashSet<String>();

		for(String mouuid: exceptionsMobileObjectSet){
			if(mouuid.startsWith(technologyID.toString())){
				objects.add(mouuid);
			}
		}
		
		exceptionsMobileObjectSet.removeAll(objects);
	}
	
	public boolean containsTechnology(Integer technologyID){
		return exceptionsTecnologiesSet.contains(technologyID);
	}
	
	public boolean containsMobileObject(MOUUID mouuid){
		return exceptionsMobileObjectSet.contains(mouuid.toString());
	}
	
	public void addMobileObject(MOUUID mouuid){
		exceptionsMobileObjectSet.add(mouuid.toString());
	}
	
	public void removeMobileObject(MOUUID mouuid){
		exceptionsMobileObjectSet.remove(mouuid.toString());
	}
	
	
	@Override
	public int compareTo(Service another) {
		if(this.serviceName.equalsIgnoreCase(another.getServiceName())){
			return 0;
		}
		else{
			return 1;
		}
	}
	
	
	public String getServiceName() {
		return serviceName;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	
	
	@Override
	public boolean equals(Object object) {
		
		Service another = (Service) object;
		
		if(this.getServiceName() == null && another.getServiceName() == null){
			return true;
		}
		
		else if(this.getServiceName() == null  && another.getServiceName() != null){
			return false;
		}
		
		else if(this.getServiceName() != null  && another.getServiceName() == null){
			return false;
		}
		
		else {
			return	this.getServiceName().equalsIgnoreCase(another.getServiceName());
		}
	}


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		
		if(this.getServiceName()!=null){
			return this.getServiceName().hashCode();
		}
		
		return super.hashCode();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getServiceName();
	}


}
