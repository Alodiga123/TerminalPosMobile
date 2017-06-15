package com.bbpos.bbdevice.example;

import android.content.Context;
import com.alodiga.security.encryption.S3cur1ty3Cryt3r;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
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
    public static SoapObject buildRequest(EncryptedParameter[] fieldInformation, String nameSpeace, String methodName)
    {
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

    /**
     * Method used to make requests to services
     * @param request - SoapObject type parameter that contains the service's input parameters
     * @param url - String parameter that indicates the location of the server
     * @param key - String parameter used to decrypt the transaction response
     * @return - Returns a parameter of type String with the response of the service
     * @throws Exception
     */
    public static String  processPetition(SoapObject request, String url, String key) throws Exception
    {
        String soapAction = " ";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url.trim());
        androidHttpTransport.call(soapAction, envelope);
        SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();

        final String value = resultsRequestSOAP.toString();
        final String response = S3cur1ty3Cryt3r.aloDesencrypter(value,key,null);

        return response;
    }

    /**
     * Method that searches the device ID
     * @param context
     * @return an String with the android Id
     */
    public static String getAndroidId(Context context)
    {
        final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }
}
