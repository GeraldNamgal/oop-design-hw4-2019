package com.cscie97.store.authentication;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PrintInventoryVisitor implements Visitor
{      
    /* VARIABLES */
    
    Integer baseLevel = 0;
    Integer tabSpace = 4;
    Integer levelPtr;
    
    /* API METHODS */

    @Override
    public void visitAuthenticator(StoreAuthenticationService authenticator)
    {
        Integer level = baseLevel.intValue();        
        
        System.out.println();
        
        for (Entry<String, User> userEntry : authenticator.getUsers().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("User: id = \"" + userEntry.getValue().getId() + "\"; name = \"" + userEntry.getValue().getName() + "\"");
            userEntry.getValue().acceptVisitor(this);
        }
    }
    
    @Override
    public void visitUser(User user)
    {
        Integer level = baseLevel + 1;
        
        for (Entry<String, Credential> credentialEntry : user.getCredentials().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("Credential: id = \"" + credentialEntry.getValue().getId() + "\"; type = \"" + credentialEntry.getValue().getType() + "\"");
        }
                    
        for (Entry<String, AuthToken> authTokenEntry : user.getAuthTokens().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("AuthToken: id = \"" + authTokenEntry.getValue().getId() + "\"; active = \"" + authTokenEntry.getValue().isActive() + "\"");
        }
        
        for (Entry<String, Entitlement> entitlementEntry : user.getEntitlements().entrySet())
        {       
            traverseTreeAndPrint(entitlementEntry.getValue(), level);              
        }        
    }  
    
    @Override
    public void visitRole(Role role)
    {        
        System.out.println("Role: id = \"" + role.getId() + "\"; name = \"" + role.getName() + "\"; description = \"" + role.getDescription() + "\"");
    }
    
    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        Integer level = levelPtr + 1;
        
        System.out.println("ResourceRole: id = \"" + rRole.getId() + "\""); //+ "\"; name = \"" + rRole.getName() + "\"; description = \"" + rRole.getDescription() + "\"");
        for (int i = 0; i < level * tabSpace; i++)
            System.out.print(" ");
        System.out.println("|Resource: id = \"" + rRole.getResource().getId() + "\"");        
    }

    @Override
    public void visitPermission(Permission permission)
    {
        System.out.println("Permission: id = \"" + permission.getId() + "\"; name = \"" + permission.getName() + "\"; description = \""
                + permission.getDescription() + "\"");        
    }
    
    /* UTILITY METHODS */
    
    public void traverseTreeAndPrint(Visitable entitlement, Integer level)
    {
        levelPtr = level.intValue();
        
        for (int i = 0; i < level * tabSpace; i++)
            System.out.print(" ");
        System.out.print("|");
        
        // Call entitlement's acceptVisitor method
        entitlement.acceptVisitor(this);
                       
        // If current node is a Role, recurse
        if (entitlement.getClass().getName().endsWith(".Role") || entitlement.getClass().getName().endsWith(".ResourceRole"))
        {           
            Integer newLevel = level + 1;
            
            Role role = (Role) entitlement;            
            LinkedHashMap<String, Entitlement> entitlements = role.getEntitlements();
            for (Entry<String, Entitlement> entitlementEntry : entitlements.entrySet())
            {       
                traverseTreeAndPrint(entitlementEntry.getValue(), newLevel);                
            }
        }              
    }      
}
