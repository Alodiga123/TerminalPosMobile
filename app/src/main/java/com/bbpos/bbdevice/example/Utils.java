package com.bbpos.bbdevice.example;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alodiga.security.encryption.S3cur1ty3Cryt3r;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public
class Utils {
    /**
     * CAPITAL_LETTER: Constant used to detect the existence of a capital letter in a text string
     */
    private static final String CAPITAL_LETTER = "(?=.*?[A-Z])";
    /**
     * LOWERCASE_LETTER: Constant used to detect the existence of a lowercase letter in a text string
     */
    private static final String LOWERCASE_LETTER = "(?=.*?[a-z])";
    /**
     * NUMBER: Constant used to detect the existence of a number in a text string
     */
    private static final String NUMBER = "(?=.*?[0-9])";
    /**
     * SPECIAL_CHARACTER: Constant used to detect the existence of a special character in a text string
     */
    private static final String SPECIAL_CHARACTER = "(?=.*?[#?!@$%^&*-])";
    /**
     * NUMBER_OF_CHARACTERS: Constant used to detect the number of characters allowed in a text string
     */
    private static final String NUMBER_OF_CHARACTERS = ".{8,}";
    /**
     * TEXT_SIZE: Constant used to indicate letter size
     */
    private static final int TEXT_SIZE = 15;

    private static final String INDICATRO_TO_DISABLE = "10";

    private static final int CANTIDAD_PARAMETROS_DE_ENTRADA = 2;


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

    public static String getkey(String deviceId) throws Exception , NullPointerException {
        String accesskey = "";
        try {
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

        SoapObject request = new SoapObject(nameSpeace, methodName);

        for (int i=0;i < fieldInformation.length;i++)
        {
            PropertyInfo propertyInfo = new PropertyInfo();
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
        final String value;
        String response;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(url.trim());

        if (!(envelope == null && soapAction == null && envelope.equals("")  && soapAction.equals("") ))
        {
           androidHttpTransport.call(soapAction, envelope);
        }
        Object resultsRequestSOAP = (Object) envelope.getResponse();
        value = resultsRequestSOAP.toString();
        response = S3cur1ty3Cryt3r.aloDesencrypter(value,key,null);
        return response;
     }

    /**
     * Method that searches the device ID
     * @param context
     * @return an String with the android Id
     */
    public static String getAndroidId(@NonNull Context context)
    {
        final String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Method used to generate a toast that indicates the errors committed.
     * @param context
     * @param text : Integer parameter containing the error to be displayed.
     */
    public static void createToast(@NonNull Context context, @NonNull int text)
    {
        LinearLayout layoutToast=new LinearLayout(context);
      //  layoutToast.setBackgroundResource(R.color.Toast_color);
        layoutToast.setBackgroundResource(R.color.Toast_background_color_error);
        TextView textViewToast=new TextView(context);
        // set the TextView properties like color, size etc
        textViewToast.setWidth(350);
        textViewToast.setTextColor(Color.parseColor("#a00037"));
        textViewToast.setTextSize(TEXT_SIZE);
        textViewToast.setGravity(Gravity.CENTER_VERTICAL);
        // set the text you want to show in  Toast
        textViewToast.setText(text);
       // textViewToast.setPadding(10,10,5,10);
        ImageView imageError=new ImageView(context);
        // give the drawble resource for the ImageView
        imageError.setImageResource(R.drawable.error);
        //TODO - verificar que la imagen tenga las dimensiones correctas para eliminar esta modificacion de su tamaño
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(80,80);
        imageError.setLayoutParams(params);
        // add both the Views TextView and ImageView in layout
        layoutToast.addView(imageError);
        layoutToast.addView(textViewToast);
        Toast toast=new Toast(context); //context is object of Context write "this" if you are an Activity
        // Set The layout as Toast View
        toast.setView(layoutToast);
        // Position you toast here toast position is 50 dp from bottom you can give any integral value
        toast.setGravity(Gravity.TOP, 0, 80);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     *Method that indicates all the conditions that a password must fulfill to be valid for the system.
     * @param newPassword :  New password that the user wants to use
     * @param confirmPassword :Confirmation of the new password
     * @return Returns an integer that contains the condition that the password must meet to be valid.
     * If all conditions are met returns a zero
     */
    public static int validatePassword(@NonNull String newPassword, @NonNull String confirmPassword)
    {
        Pattern capitalLetter = Pattern.compile(CAPITAL_LETTER);
        Matcher capLet = capitalLetter.matcher(newPassword);
        Pattern lowerLetter = Pattern.compile(LOWERCASE_LETTER);
        Matcher lowLet = lowerLetter.matcher(newPassword);
        Pattern number = Pattern.compile(NUMBER);
        Matcher nb = number.matcher(newPassword);
        Pattern specialCharacter = Pattern.compile(SPECIAL_CHARACTER);
        Matcher spChar = specialCharacter.matcher(newPassword);
        Pattern numberCharacters = Pattern.compile(NUMBER_OF_CHARACTERS);
        Matcher nmbChar = numberCharacters.matcher(newPassword);

        if(!capLet.lookingAt())
        {
            return R.string.validate_password_change_capital_letter;
        }else if(!lowLet.lookingAt())
        {
            return R.string.validate_password_change_lowercase_letter;
        }else if(!nb.lookingAt())
        {
            return R.string.validate_password_change_number;
        }else if(!spChar.lookingAt())
        {
            return R.string.validate_password_change_special_character;
        }else if(!nmbChar.lookingAt())
        {
            return R.string.validate_password_change_number_characters;
        }else if(!newPassword.equals(confirmPassword))
        {
            return R.string.toast_different_passwords;
        }
        return 0;
    }

    /**
     * Method used to increase the progress bar, depending on the conditions
     * used to create a valid key for the system
     * @param newPassword : New password that the user wants to use
     * @return Returns an integer that indicates the amount of conditions the
     * user has fulfilled to change the password
     */
    public static int progressBar(String newPassword)
    {
        Pattern capitalLetter = Pattern.compile(CAPITAL_LETTER);
        Matcher capLet = capitalLetter.matcher(newPassword);
        Pattern lowerLetter = Pattern.compile(LOWERCASE_LETTER);
        Matcher lowLet = lowerLetter.matcher(newPassword);
        Pattern number = Pattern.compile(NUMBER);
        Matcher nb = number.matcher(newPassword);
        Pattern specialCharacter = Pattern.compile(SPECIAL_CHARACTER);
        Matcher spChar = specialCharacter.matcher(newPassword);
        Pattern numberCharacters = Pattern.compile(NUMBER_OF_CHARACTERS);
        Matcher nmbChar = numberCharacters.matcher(newPassword);
        int progressIndicator = 0;

        boolean[]var = new boolean[]{false,false,false,false,false,false};

        if(!newPassword.equals(""))
        {
            if(capLet.lookingAt())
            {
                var[0]= true;
            }
            if(lowLet.lookingAt())
            {
                var[1]= true;
            }
            if(nb.lookingAt())
            {
                var[2]= true;
            }
            if(spChar.lookingAt())
            {
                var[3]= true;
            }
            if(nmbChar.lookingAt())
            {
                var[4]= true;
            }
            for(int i = 0; i< var.length ; i++)
            {
                if(var[i]==true)
                {
                    progressIndicator += 10;
                }
            }
             return progressIndicator;
        }else
        {
            return 0;
        }
    }



 public static String disableTerminal(String androidID, String key ) throws Exception {
     String responceReceived;

        //Namespace of the Webservice - can be found in WSDL
        String  NAMESPACE = "http://services.ws.acquiring.alodiga.com/";
        //Webservice URL - WSDL File location
        String URL = "http://192.168.3.144:8080/AcquiringWSServicesProviderService/AcquiringWSServicesProvider";
        //SOAP Action URI again Namespace + Web method name
        String METHOD_NAME = "disableTerminal";
        final String androidIdEncrypted = Utils.sha256(androidID);
        final String parametroDeBloqueo = S3cur1ty3Cryt3r.aloEncrypter(INDICATRO_TO_DISABLE,key,null);
        EncryptedParameter encrypterParam[] = new EncryptedParameter[CANTIDAD_PARAMETROS_DE_ENTRADA];
        EncryptedParameter encrypterParamObject = new EncryptedParameter();
        EncryptedParameter encrypterParamObjectAndroid = new EncryptedParameter();

        encrypterParamObject.name = "encryptedStatusIndicator";
        encrypterParamObject.encryptedString = parametroDeBloqueo;
        encrypterParamObject.type = PropertyInfo.STRING_CLASS;
        encrypterParam[0] = encrypterParamObject;

        encrypterParamObjectAndroid.name = "androidId";
        encrypterParamObjectAndroid.encryptedString = androidIdEncrypted;
        encrypterParamObjectAndroid.type = PropertyInfo.STRING_CLASS;
        encrypterParam[1] = encrypterParamObjectAndroid;

       SoapObject requestReceived = Utils.buildRequest(encrypterParam, NAMESPACE, METHOD_NAME);
       responceReceived = Utils.processPetition(requestReceived, URL, key);

     return responceReceived;
 }

}
