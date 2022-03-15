package customer.wellcompletion;

import java.util.stream.Stream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.ParameterInfo;
import com.sap.cds.services.request.UserInfo;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import cds.gen.masterdataservice.MasterDataService_;
import cds.gen.masterdataservice.NetworkObjects;


@Component
@ServiceName(MasterDataService_.CDS_NAME) // In the Gen Folder in SRV
public class NetworkObjectsHandler implements EventHandler {

    @After(event = CqnService.EVENT_READ, entity = "MasterDataService.NetworkObjects")
    public void networkObjectAfterRead(Stream<NetworkObjects> netObj, CdsReadEventContext context) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Inside the Read Method of the Network Objects");
        System.out.println("Network Object Method : Sysout");

        netObj
                //.peek(b -> System.out.println("ID : " + b.getNetworkObjectID() + "Country Code :" + b.getCountryCode()))
                .filter(b -> b.getCountryCode().equals("DE")).forEach(b -> b.setCountryCode("DI"));

     //   netObj.forEach(
       //         b -> System.out.println("ID : " + b.getNetworkObjectID() + "Country Code :" + b.getCountryCode()));
       // netObj.forEach(b -> logger.info("ID : " + b.getNetworkObjectID() + "Country Code :" + b.getCountryCode()));
    }


    @Before(event = CqnService.EVENT_READ)
    public void networkObjectBeforeRead( CdsReadEventContext context) {
        
        UserInfo userInfo = context.getUserInfo();
        // System.out.println("Sys Print User Id: " + userInfo.getId() + "Get Name :" + userInfo.getName() + " authenticated User: " + userInfo.isAuthenticated());

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info(" Logger User Id: " + userInfo.getId() + "Get Name :" + userInfo.getName() + " authenticated User: " + userInfo.isAuthenticated()); 

       // System.out.println("Sys Print Email from Role :"  + userInfo.getAdditionalAttribute("email"));

        //logger.info("Logger :-> " + "Email from Role :" + userInfo.getAdditionalAttribute("email") + " ->Role: " + userInfo.getRoles()); 
        logger.info("Logger :-> " + "Email from Role :" + userInfo.getAdditionalAttribute("email") + " ->Role: " + userInfo.getRoles()); 
      

       Set<String> userRoles = userInfo.getRoles();

       for(String role : userRoles) {
           logger.info("Role : " + role );
       }


      /*  Map<String, String> headerInfo = parameterInfo.getHeaders();
        for (Map.Entry<String,String> entry : headerInfo.entrySet())
        logger.info("Key = " + entry.getKey() +
                         ", Value = " + entry.getValue());
                         */



       List<String> userCountries =  userInfo.getAttributeValues("countryassigned");  
       for( String userCountry : userCountries)
       {
        logger.info(" -> Country Assigned " + userCountry );  
       }   
       
      // check if countryassigned is unrestricted.  isUnrestrictedAttribute
      logger.info(" ->isUnrestrictedAttribute  " + userInfo.getAttributeValues("isUnrestrictedAttribute") );


      //Get list of all the attributes for the user. 
      Map<String, List<String>> attributes =  userInfo.getAttributes();
      for (Map.Entry<String,List<String>> entry : attributes.entrySet())
      { logger.info("Key = " + entry.getKey() );
      List<String> attr = entry.getValue();
      for( String attrItem : attr)
      {
       logger.info("User Info Attributes" + entry.getKey() + " : " + attrItem );  
      }  
      
    }

    } 

}