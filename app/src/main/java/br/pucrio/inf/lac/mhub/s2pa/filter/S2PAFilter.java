/**
 * 
 */
package br.pucrio.inf.lac.mhub.s2pa.filter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import br.pucrio.inf.lac.mhub.components.MOUUID;

/**
 * @author bertodetacio
 *
 */
public class S2PAFilter {
	
	private boolean active = false;
	
	public static final int ALL_SERVICES_ENABLE = 0;
	
	public static final int ALL_SERVICES_DISABLE = 1;
	
	private int currentServicePolicy = ALL_SERVICES_ENABLE;
	
	private Hashtable<Integer,Technology>  exceptionsTecnologiesMap = new Hashtable<Integer, Technology>();
	
	private Hashtable<String,MobileObject>  exceptionsMobileObjectMap = new Hashtable<String, MobileObject>();
	
	private Hashtable<String, Service>  exceptionsServiceMap = new Hashtable<String, Service>();
	
	private Hashtable<String, Service>  noExceptionsServiceMap = new Hashtable<String, Service>();
	
	private Hashtable<String, Double[]>  sensorReadingsMap = new Hashtable<String, Double[]>();
	
	public static final int ALL_SENSOR_READINGS = 0;
	
	public static final int ONLY_NEW_SENSOR_READINGS = 1;
	
	private int currentValuesPolicy = ALL_SENSOR_READINGS;
	
	private boolean disseminateOnlyEnabledServices = false;
	
	private static S2PAFilter instance = null;
	
	
	/**
	 * 
	 */
	private S2PAFilter() {
		// TODO Auto-generated constructor stub
	}
	
	public static S2PAFilter getInstance(){
		if(instance == null){
			instance = new S2PAFilter();
		}
		return instance;
	}
	
	
	public synchronized void enableAllSensors(){
		this.currentServicePolicy = ALL_SERVICES_ENABLE;
		cleanSets();
	}
	
	public synchronized void disableAllSensors(){
		this.currentServicePolicy = ALL_SERVICES_DISABLE;
		cleanSets();
	}
	
	public synchronized void enableAllSensorsOfThisTechnology(Integer technologyID){
		
		removeAllMobileObjectsFromThisTechnology(technologyID);
		removeTechnologyFromAllNoServiceException(technologyID);
				
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			removeTechnology(technologyID);
			includeThisTechnologyForAllServices(technologyID);
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			Technology technology = new Technology(technologyID);
			includeTechnology(technology);
			removeTechnologyFromAllServices(technologyID);
		}
	}
	
	public synchronized void disableAllSensorsOfThisTechnology(Integer technologyID){
		
		removeAllMobileObjectsFromThisTechnology(technologyID);
		removeTechnologyFromAllNoServiceException(technologyID);
		
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			Technology technology = new Technology(technologyID);
			includeTechnology(technology);
			removeTechnologyFromAllServices(technologyID);			
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			removeTechnology(technologyID);
			includeThisTechnologyForAllServices(technologyID);
		}
	}
	
	public synchronized void enableAllSensorsOfThisObject(MOUUID mouuid){
		
		Integer technologyID = (int)mouuid.getTechnologyID();

		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			includeObjectFromAllServices(mouuid);
			if(containsTechnology(technologyID)){
				includeObjectFromTechology(mouuid);
			}
			removeMobileObject(mouuid);
			if(containsTechnologyInNoServiceException((int)mouuid.getTechnologyID())){
				includeObjectForNoExceptionServiceContainingTechnology(mouuid);
			}
			else{
				removeObjectFromAllNoServiceException(mouuid);
			}
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			MobileObject mobileObject = new MobileObject(mouuid);
			removeObjectFromAllServices(mouuid);
			if(containsTechnology(technologyID)){
				removeObjectFromTechology(mouuid);
			}
			includeMobileObject(mobileObject);
			removeObjectFromAllNoServiceException(mouuid);
		}
	}
	
	public synchronized void disableAllSensorsOfThisObject(MOUUID mouuid){
		Integer technologyID = (int)mouuid.getTechnologyID();
		
		removeObjectFromAllNoServiceException(mouuid);;
		
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			MobileObject mobileObject = new MobileObject(mouuid);
			removeObjectFromAllServices(mouuid);
			if(containsTechnology(technologyID)){
				removeObjectFromTechology(mouuid);
			}
			includeMobileObject(mobileObject);
			removeObjectFromAllNoServiceException(mouuid);
		}

		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			includeObjectFromAllServices(mouuid);
			if(containsTechnology(technologyID)){
				includeObjectFromTechology(mouuid);
			}
			removeMobileObject(mouuid);
			if(containsTechnologyInNoServiceException((int)mouuid.getTechnologyID())){
				includeObjectForNoExceptionServiceContainingTechnology(mouuid);
			}
			else{
				removeObjectFromAllNoServiceException(mouuid);
			}
		}
	}
	
	public synchronized void enableSensor(String serviceName){
		
		removeNoExceptionService(serviceName);
		
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			removeService(serviceName);
			includeServiceFromObjects(serviceName);
			includeServiceFromTechnologies(serviceName);
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			Service service = new Service(serviceName);
			includeService(service);
			removeServiceFromObjects(serviceName);
			removeServiceFromTechnologies(serviceName);
		}
	}
		
	public synchronized void disableSensor(String serviceName){
	
		removeNoExceptionService(serviceName);
		
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			Service service = new Service(serviceName);
			includeService(service);
			removeServiceFromObjects(serviceName);
			removeServiceFromTechnologies(serviceName);
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			removeService(serviceName);
			includeServiceFromObjects(serviceName);
			includeServiceFromTechnologies(serviceName);
		}
		
	}
	
	
	public synchronized void disseminateOnlyDiscoveredServicesEnable(){
		disseminateOnlyEnabledServices = true;
	}
	
	public void disseminateAllServicesDiscovered(){
		disseminateOnlyEnabledServices = false;
	}
	
	
	public synchronized void enableSensorOfTechnology(String serviceName, Integer technologyID){
		Service service = exceptionsServiceMap.get(serviceName);
		Technology technology = exceptionsTecnologiesMap.get(technologyID);
		if(currentServicePolicy == ALL_SERVICES_ENABLE){	
			if(service!=null){
				service.addTechnology(technologyID);
			}
			else{
				removeTechnologyFromNoExceptionService(serviceName, technologyID);
			}
			if(technology!=null){
				technology.addService(serviceName);
			}
			includeServiceOnTheObjectsOfThisTechnology(serviceName, technologyID);
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			if(service!=null){
				service.removeTechnology(technologyID);
			}
			else{
				includeTechnologyForNoExceptionService(serviceName, technologyID);
			}
			if(technology!=null){
				technology.removeService(serviceName);
			}
			removeServiceOnTheObjectsOfThisTechnology(serviceName, technologyID);
		}
	}
	
	public synchronized void disableSensorOfTechnology(String serviceName, Integer technologyID){
		Service service = exceptionsServiceMap.get(serviceName);
		Technology technology = exceptionsTecnologiesMap.get(technologyID);
		if(currentServicePolicy == ALL_SERVICES_ENABLE){
			if(service!=null){
				service.removeTechnology(technologyID);
			}
			else{
				includeTechnologyForNoExceptionService(serviceName, technologyID);
			}
			if(technology!=null){
				technology.removeService(serviceName);
			}
			removeServiceOnTheObjectsOfThisTechnology(serviceName, technologyID);
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			if(service!=null){
				service.addTechnology(technologyID);
			}
			else{
				removeTechnologyFromNoExceptionService(serviceName, technologyID);
			}
			if(technology!=null){
				technology.addService(serviceName);
			}
			includeServiceOnTheObjectsOfThisTechnology(serviceName, technologyID);
			
		}
		
	}
	
	public synchronized void enableSensorOfDevice(String serviceName, MOUUID mouuid){
		Service service = exceptionsServiceMap.get(serviceName);
		Service noService = noExceptionsServiceMap.get(serviceName);
		
		MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid.toString());
		if(currentServicePolicy == ALL_SERVICES_ENABLE){	
			
			if(mobileObject!=null){
				mobileObject.addService(serviceName);
			}	
			
			if(service!=null){
				if(!service.containsTechnology((int)mouuid.getTechnologyID())){
					service.addMobileObject(mouuid);
				}
			}
			
			if(noService!=null){
				
				if(containsTechnology((int)mouuid.getTechnologyID())){
					noService.addMobileObject(mouuid);
				}
				
				else if(noService.containsMobileObject(mouuid)){
					noService.removeMobileObject(mouuid);
				}
				
			}	
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			
			if(mobileObject!=null){
				mobileObject.removeService(serviceName);
			}
			
			
			if(service!=null){
				service.removeMobileObject(mouuid);
			}
			
			else{
				includeObjectForNoExceptionService(serviceName, mouuid);
			}
			
		}
	}
	
	public synchronized void disableSensorOfDevice(String serviceName, MOUUID mouuid){
		Service service = exceptionsServiceMap.get(serviceName);
		Service noService = noExceptionsServiceMap.get(serviceName);
		MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid.toString());
		if(currentServicePolicy == ALL_SERVICES_ENABLE){	
			if(service!=null){
				service.removeMobileObject(mouuid);
			}
			
			if(mobileObject!=null){
				mobileObject.removeService(serviceName);
			}
			
			else{
				includeObjectForNoExceptionService(serviceName, mouuid);
			}
			
		}
		
		if(currentServicePolicy == ALL_SERVICES_DISABLE){
			
			
			if(mobileObject!=null){
				mobileObject.addService(serviceName);
			}	
			
			if(service!=null){
				if(!service.containsTechnology((int)mouuid.getTechnologyID())){
					service.addMobileObject(mouuid);
				}
			}
			
			if(noService!=null){
				
				if(containsTechnology((int)mouuid.getTechnologyID())){
					noService.addMobileObject(mouuid);
				}
				
				else if(noService.containsMobileObject(mouuid)){
					noService.removeMobileObject(mouuid);
				}
				
			}

		}
	}
	
	
	public synchronized void enableAllSensorReadings(){
		currentValuesPolicy = ALL_SENSOR_READINGS;
	}
	
	public synchronized void enableOnlyNewValuesSensorReadings(){
		currentValuesPolicy = ONLY_NEW_SENSOR_READINGS;
	}
	
	
	public synchronized List<String> getCanPass(MOUUID mouuid,List<String> services){
		
		List<String> discoveredServiceEnable = new ArrayList<String>();
		
		if(disseminateOnlyEnabledServices){
			for(String service: services){
				if(canPass(mouuid, service)){
					discoveredServiceEnable.add(service);
				}
			}
		}
		
		else{
			discoveredServiceEnable.addAll(services);
		}
		
		return discoveredServiceEnable;
	}
	
	
	public synchronized boolean canPass(MOUUID mouuid, String service, Double [] values){
		
		boolean changeValues = changeValues(mouuid, service, values);
		
		if(!isActive()){
			return true;
		}
		
		if(canPass(mouuid, service)){
			
			if(currentValuesPolicy == ONLY_NEW_SENSOR_READINGS && !changeValues){
				return false;
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	private synchronized boolean canPass(MOUUID mouuid, String service) {
		
		if(!isActive()){
			return true;
		}
		
		if (currentServicePolicy == ALL_SERVICES_ENABLE) {

			if (checkService(mouuid, service)) {
				return false;
			}

			return true;
		}

		if (currentServicePolicy == ALL_SERVICES_DISABLE) {

			if (checkService(mouuid, service)) {

				return true;
			}

			return false;
		}

		return true;
	}
	
	private synchronized void includeTechnology(Technology technology){
		exceptionsTecnologiesMap.put(technology.getTechnologyID(),technology);
		
	}
	
	private synchronized void includeMobileObject(MobileObject mobileObject){
		exceptionsMobileObjectMap.put(mobileObject.getMouuid().toString(),mobileObject);
		
	}
	
	private synchronized void removeMobileObject(MOUUID mouuid){
		exceptionsMobileObjectMap.remove(mouuid.toString());
		
	}
	
	private synchronized void includeThisTechnologyForAllServices(Integer technologyID){
		Set <String> servicesKeys = exceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = exceptionsServiceMap.get(serviceName);
			if(!service.containsTechnology(technologyID)){
				service.addTechnology(technologyID);
			}
		}
		
	}
	
	private synchronized void removeTechnology(Integer technologyID){
		if(exceptionsTecnologiesMap.containsKey(technologyID)){
			exceptionsTecnologiesMap.remove(technologyID);
		}
	}
	
	
	
	private synchronized void removeAllMobileObjectsFromThisTechnology(Integer technologyID){
		Set <String>mobileObjectsKeys = exceptionsMobileObjectMap.keySet();
		for(String mouuid: mobileObjectsKeys){
			MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid);
			if((int)mobileObject.getMouuid().getTechnologyID() == technologyID){
				exceptionsMobileObjectMap.remove(mouuid);
			}
		}
	}
	
	private synchronized void removeTechnologyFromAllServices(Integer technologyID){
		Set <String> servicesKeys = exceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = exceptionsServiceMap.get(serviceName);
			if(service.containsTechnology(technologyID)){
				service.removeTechnology(technologyID);
			}
		}
	}
	
	private synchronized void includeObjectFromAllServices(MOUUID mouuid){
		Set <String> servicesKeys = exceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = exceptionsServiceMap.get(serviceName);
			if(!service.containsMobileObject(mouuid)){
				service.addMobileObject(mouuid);
			}
		}
	}	
	
	private synchronized void removeObjectFromAllServices(MOUUID mouuid){
		Set <String> servicesKeys = exceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = exceptionsServiceMap.get(serviceName);
			if(service.containsMobileObject(mouuid)){
				service.removeMobileObject(mouuid);
			}
		}
	}
	
	private synchronized void includeObjectFromTechology(MOUUID mouuid){
		Technology technology = exceptionsTecnologiesMap.get((int)mouuid.getTechnologyID());
		if(technology!=null){
			technology.addMobileObject(mouuid);
		}
	}
	
	private synchronized void removeObjectFromTechology(MOUUID mouuid){
		Technology technology = exceptionsTecnologiesMap.get((int)mouuid.getTechnologyID());
		if(technology!=null){
			technology.removeMobileObject(mouuid);
		}
	}
	
	private synchronized void includeService(Service service){
		exceptionsServiceMap.put(service.getServiceName(), service);
		noExceptionsServiceMap.remove(service);
	}
	
	private synchronized void removeService(String serviceName){
		exceptionsServiceMap.remove(serviceName);
	}
	
	private void includeServiceOnTheObjectsOfThisTechnology(String serviceName, Integer technologyID){
		Set <String>mobileObjectsKeys = exceptionsMobileObjectMap.keySet();
		for(String mouuid: mobileObjectsKeys){
			MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid);
			if((int)mobileObject.getMouuid().getTechnologyID() == technologyID){
				mobileObject.addService(serviceName);
			}
		}
	}
	
	private void removeServiceOnTheObjectsOfThisTechnology(String serviceName, Integer technologyID){
		Set <String>mobileObjectsKeys = exceptionsMobileObjectMap.keySet();
		for(String mouuid: mobileObjectsKeys){
			MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid);
			if((int)mobileObject.getMouuid().getTechnologyID() == technologyID){
				mobileObject.removeService(serviceName);
			}
		}
	}
	
	private synchronized void includeServiceFromTechnologies(String serviceName){
		Set<Integer> technologiesKeys = exceptionsTecnologiesMap.keySet();
		for(Integer technologyID: technologiesKeys){
			Technology technology = exceptionsTecnologiesMap.get(technologyID);
			if(!technology.containsService(serviceName)){
				technology.addService(serviceName);
			}
		}
	}
	
	private synchronized void removeServiceFromTechnologies(String serviceName){
		Set<Integer> technologiesKeys = exceptionsTecnologiesMap.keySet();
		for(Integer technologyID: technologiesKeys){
			Technology technology = exceptionsTecnologiesMap.get(technologyID);
			if(technology.containsService(serviceName)){
				technology.removeService(serviceName);
			}
		}
	}
	
	private synchronized void removeServiceFromObjects(String serviceName){
		Set <String>mobileObjectsKeys = exceptionsMobileObjectMap.keySet();
		for(String mouuid: mobileObjectsKeys){
			MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid);
			if(mobileObject.containsService(serviceName)){
				mobileObject.removeService(serviceName);
			}
		}
	}
	
	private synchronized void includeServiceFromObjects(String serviceName){
		Set <String>mobileObjectsKeys = exceptionsMobileObjectMap.keySet();
		for(String mouuid: mobileObjectsKeys){
			MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid);
			if(!mobileObject.containsService(serviceName)){
				mobileObject.addService(serviceName);
			}
		}
	}
	
	private synchronized void removeNoExceptionService(String serviceName){
		if(noExceptionsServiceMap.containsKey(serviceName)){
			noExceptionsServiceMap.remove(serviceName);
		}
	}
	
	private synchronized void includeObjectForNoExceptionService(String serviceName, MOUUID mouuid){
		Service service = noExceptionsServiceMap.get(serviceName);
		if(service==null){
			service = new Service(serviceName);
			noExceptionsServiceMap.put(serviceName, service);
		}
		if(!service.containsTechnology((int)mouuid.getTechnologyID())){
			service.addMobileObject(mouuid);
		}
	}

	
	
	private synchronized void includeTechnologyForNoExceptionService(String serviceName, Integer technologyID){
		Service service = noExceptionsServiceMap.get(serviceName);;
		if(service==null){
			service = new Service(serviceName);
			noExceptionsServiceMap.put(serviceName, service);
		}
		service.addTechnology(technologyID);
	}
	
	private synchronized void removeTechnologyFromNoExceptionService(String serviceName, Integer technologyID){
		Service service = noExceptionsServiceMap.get(serviceName);;
		if(service!=null){
			service = noExceptionsServiceMap.get(serviceName);
			service.removeTechnology(technologyID);
		}
	}
	
	private synchronized void removeTechnologyFromAllNoServiceException(Integer technologyID){
		Set <String> servicesKeys = noExceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = noExceptionsServiceMap.get(serviceName);
			if(service.containsTechnology(technologyID)){
				service.removeTechnology(technologyID);
			}
		}
	}
	
	private synchronized boolean containsTechnologyInNoServiceException(Integer technologyID){
		Set <String> servicesKeys = noExceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = noExceptionsServiceMap.get(serviceName);
			if(service.containsTechnology(technologyID)){
				return true;
			}
		}
		return false;
	}
	
	
	private synchronized void includeObjectForNoExceptionServiceContainingTechnology(MOUUID mouuid){
		Set <String> servicesKeys = noExceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = noExceptionsServiceMap.get(serviceName);
			if(service.containsTechnology((int)mouuid.getTechnologyID())){
				service.addMobileObject(mouuid);
			}

		}
	}
	
	private synchronized void removeObjectFromAllNoServiceException(MOUUID mouuid){
		Set <String> servicesKeys = noExceptionsServiceMap.keySet();
		for(String serviceName: servicesKeys){
			Service service = noExceptionsServiceMap.get(serviceName);
			if(service.containsMobileObject(mouuid)){
				service.removeMobileObject(mouuid);;
			}
		}
	}
	
	
	private boolean containsTechnology(Integer technologyID){
		return exceptionsTecnologiesMap.containsKey(technologyID);
	}
	
	
		
	private boolean checkService(MOUUID mouuid, String serviceName){
		
		boolean result = false;
		
		Integer technologyID = (int) mouuid.getTechnologyID();
		
		Service serviceException = exceptionsServiceMap.get(serviceName);
		
		Service noServiceException = noExceptionsServiceMap.get(serviceName);		
		
		if(noServiceException!=null && (noServiceException.containsTechnologyOrMobileObjectExclusively((mouuid)))){			
			return true;
		}
		
		if(serviceException!=null && (!serviceException.containsTechnologyOrMobileObject(mouuid))){
			return true;
		}
		
		MobileObject mobileObject = exceptionsMobileObjectMap.get(mouuid.toString());
		
		if(mobileObject!=null && (!mobileObject.containsService(serviceName))){
			return true;
		}
		
		Technology technology = exceptionsTecnologiesMap.get(technologyID);
		
		if(technology!=null && (!technology.containsMobileObjectOrService(mouuid, serviceName))){
			return true;
		}
				
		return result;
	}
	
	
	private boolean changeValues(MOUUID mouuid, String serviceName, Double[]newValues){
		
		if(newValues == null){
			return false;
		}
		
		String serviceKey = mouuid.toString()+serviceName;
		
		Double[] oldValues = sensorReadingsMap.get(serviceKey);
		
		
		if(oldValues == null){
			sensorReadingsMap.put(serviceKey, newValues);
			return true;
		}
		
		if(oldValues.length != newValues.length){
			sensorReadingsMap.put(serviceKey, newValues);
			return true;
		}
		
		else {
			
			for(int i = 0; i < oldValues.length;i++){
				
				if(oldValues[i].doubleValue()!=newValues[i].doubleValue()){
					sensorReadingsMap.put(serviceKey, newValues);		
					return true;
				}
				
			}
				
			return false;
		}
	}
	
	
	private void cleanSets(){
		exceptionsTecnologiesMap.clear();
		exceptionsMobileObjectMap.clear();
		exceptionsServiceMap.clear();
		noExceptionsServiceMap.clear();
	}
	
	

	public int getCurrentPolicy() {
		return currentServicePolicy;
	}


	public void setCurrentPolicy(int currentPolicy) {
		this.currentServicePolicy = currentPolicy;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}
	

}
