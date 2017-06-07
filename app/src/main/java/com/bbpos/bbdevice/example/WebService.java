package com.bbpos.bbdevice.example;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {
	private static String NAMESPACE = "http://services.ws.acquiring.alodiga.com/";
	//private static String URL = "http://ec2-35-167-158-127.us-west-2.compute.amazonaws.com:8080/AcquiringWSServicesProviderService/AcquiringWSServicesProvider";
	private static String SOAP_ACTION = "";
	
	public static String invokeGetAutoConfigString(String manf, String model, String webMethName, String URL) {
		String resTxt = null;

		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		// Property which holds input parameters
		PropertyInfo propInfo=new PropertyInfo();
		propInfo.name="arg0";
		propInfo.type=PropertyInfo.STRING_CLASS;

		PropertyInfo propInfo1=new PropertyInfo();
		propInfo1.name="arg1";
		propInfo1.type=PropertyInfo.STRING_CLASS;

		request.addProperty(propInfo, manf);
		request.addProperty(propInfo1, model);
		
/*		PropertyInfo apiVersionProperty = new PropertyInfo();
		apiVersionProperty.setName("apiVersion");
		apiVersionProperty.setValue(apiVersion);
		apiVersionProperty.setType(String.class);*/
		
		// Add the property to request object

//		request.addProperty(apiVersionProperty);
		
		// Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//		envelope.dotNet = true;
		// Set output SOAP object
		envelope.setOutputSoapObject(request);
		// Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL.trim());

		try {
			// Invole web service
			androidHttpTransport.call(SOAP_ACTION, envelope);
			// Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			// Assign it to fahren static variable
			resTxt = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		} 
		
		return resTxt;
	}	
}
