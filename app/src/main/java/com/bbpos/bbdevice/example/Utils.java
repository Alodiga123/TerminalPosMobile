package com.bbpos.bbdevice.example;

import android.content.Context;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.security.MessageDigest;

public class Utils {
	protected static String encodeNdefFormat(String hexString) {
		String result = "";
		String length = Integer.toHexString(hexString.length() + 3);
		while (length.length() % 2 != 0) {
			length = "0" + length;
		}
		if (length.length() > 2) {
			return result;
		}
		result = "D101" + length + "54" + "02656E" + hexString; 
		return result;
	}
//la prueba
    //prueba2

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public String getkey(Context c) throws Exception , NullPointerException {
        String accesskey = "";
        try {
            String deviceId = android.provider.Settings.Secure.getString(c.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);

            deviceId=sha256(deviceId);
            //Implement
            String macAddres = "2F:11:2F:CF:0F:DC";
            //Implement
            StringBuilder madkeyf = new StringBuilder();
            madkeyf.append(macAddres.substring(0,2)).append(macAddres.substring(3,5)).append(macAddres.substring(6,8)).append(macAddres.substring(9,11));
            accesskey = sha256(deviceId.substring(0,8) + madkeyf).substring(0,16)  ;
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return accesskey;
    }



    /**
     * This method is used to construct the service request
     * @param fieldInformation Object containing the parameters (name, type, encryptedString)
     *                                 String name: field name
     *                           STRING_CLASS type: data type
     *                      String encryptedString: encrypted parameter
     * @param nameSpeace - Namespace of the Webservice - can be found in WSDL
     * @param methodName - Name of the WSDL method to use
     * @return Return the request to consume the service
     */
    public static SoapObject buildRequest(EncryptedParameter[] fieldInformation, String nameSpeace, String methodName){

        PropertyInfo propertyInfo = new PropertyInfo();
        SoapObject request = new SoapObject(nameSpeace, methodName);

        for (int i=0;i < fieldInformation.length;i++)
        {
            propertyInfo.name = fieldInformation[i].name;
            propertyInfo.type = fieldInformation[i].type;

            request.addProperty(propertyInfo, fieldInformation[i].encryptedString);
        }
        return request;
    }


}
