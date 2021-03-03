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
public class MobileObject implements Comparable<MobileObject>{
	
	private MOUUID mouuid;
	
	private HashSet<String>  exceptionsServicesSet = new HashSet <String>();
	
	/**
	 * 
	 */
	public MobileObject() {
		// TODO Auto-generated constructor stub
	}
	
	public MobileObject(MOUUID mouuid) {
		this.mouuid = mouuid;
	}
	
	
	@Override
	public int compareTo(MobileObject another) {
		if(this.mouuid.toString().equalsIgnoreCase(another.mouuid.toString())){
			return 0;
		}
		else{
			return 1;
		}
	}
	
	
	public boolean containsService(String service){
		return exceptionsServicesSet.contains(service);
	}
	
	public void addService(String service){
		exceptionsServicesSet.add(service);
	}
	
	public void removeService(String service){
		exceptionsServicesSet.remove(service);
	}
	

	public MOUUID getMouuid() {
		return mouuid;
	}

	public void setMouuid(MOUUID mouuid) {
		this.mouuid = mouuid;
	}

	public HashSet<String> getExceptionsMobileObjectSet() {
		return exceptionsServicesSet;
	}
	
	
	@Override
	public boolean equals(Object object) {
		
		MobileObject another = (MobileObject) object;
		
		if(this.getMouuid()== null && another.getMouuid() == null){
			return true;
		}
		
		else if(this.getMouuid() == null  && another.getMouuid() != null){
			return false;
		}
		
		else if(this.getMouuid() != null  && another.getMouuid() == null){
			return false;
		}
		
		else {
			return this.getMouuid().toString().equalsIgnoreCase(another.getMouuid().toString());
		}
	}


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		
		if(this.getMouuid()!=null){
			return this.getMouuid().hashCode();
		}
		
		return super.hashCode();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getMouuid().toString();
	}

	

}
