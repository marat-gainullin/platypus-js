/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.eas.client.scripts;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.script.ScriptUtils;
import java.math.RoundingMode;
import java.security.AccessControlException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSObjectFacade extends JSObjectFacade {

    private final Pattern roleTemplate = Pattern.compile("(\\$\\d+)");// WARNING!!! Don't make this member static!
    protected Map<String, Set<String>> propertiesAllowedRoles;
    // configuration
    protected String appElementId;
    protected Set<String> moduleAllowedRoles;
    /**
     * Current principal provider
     */
    protected PrincipalHost principalHost;

    public SecuredJSObjectFacade(JSObject aDelegate, String aAppElementId, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles, PrincipalHost aPrincipalHost) {
        super(aDelegate);
        appElementId = aAppElementId;
        moduleAllowedRoles = aModuleAllowedRoles;
        propertiesAllowedRoles = aPropertiesAllowedRoles;
        principalHost = aPrincipalHost;
    }

    protected PlatypusPrincipal getPrincipal() {
        if (principalHost != null) {
            return principalHost.getPrincipal();
        }
        return null;
    }

    /**
     * Checks module access roles.
     *
     */
    protected void checkModulePermissions() throws AccessControlException {
        if (moduleAllowedRoles != null && !moduleAllowedRoles.isEmpty()) {
            try {
                PlatypusPrincipal principal = getPrincipal();
                if (principal == null || !principal.hasAnyRole(moduleAllowedRoles)) {
                    throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                            appElementId,
                            principal != null ? principal.getName() : null));
                }
            } catch (Exception ex) {
                throw new AccessControlException(ex.getMessage());
            }
        }
    }

    private NumberFormat getNumberFormatter() {
        DecimalFormat formatter = new DecimalFormat();
        formatter.setMinimumIntegerDigits(1);
        formatter.setMaximumFractionDigits(100);
        formatter.setRoundingMode(RoundingMode.DOWN);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter;
    }

    protected Set<String> filterRoles(Set<String> aRoles, Object[] args) {
        Set<String> roles = new HashSet<>();
        for (String role : aRoles) {
            Matcher m = roleTemplate.matcher(role);
            StringBuilder processedRole = new StringBuilder();
            int begin = 0;
            NumberFormat formatter = getNumberFormatter();
            while (m.find()) {
                String template = m.group();
                int argIdx = Integer.valueOf(template.substring(1));
                Object arg = (args.length > argIdx && argIdx >= 0) ? ScriptUtils.toJava(args[argIdx]) : "undefined";
                String argValue;
                if (arg instanceof Date) {
                    argValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(arg);
                } else if (arg instanceof Number) {
                    argValue = formatter.format(arg);
                } else {
                    argValue = String.valueOf(arg);
                }
                processedRole.append(role.substring(begin, m.start()));
                processedRole.append(argValue);
                begin = m.end();
            }
            processedRole.append(role.substring(begin, role.length()));
            roles.add(processedRole.toString());
        }
        return roles;
    }

    /**
     * Checks module instance property access roles.
     *
     * @param aName A property name access is checked for.
     */
    protected void checkPropertyPermission(String aName) throws AccessControlException {
        checkPropertyPermission(aName, null);
    }

    protected void checkPropertyPermission(String aName, Object[] aArgs) throws AccessControlException {
        try {
            PlatypusPrincipal principal = getPrincipal();
            if (propertiesAllowedRoles != null && propertiesAllowedRoles.get(aName) != null && !propertiesAllowedRoles.get(aName).isEmpty()) {
                Set<String> declaredRoles = propertiesAllowedRoles.get(aName);
                Set<String> filteredRoles;
                if (aArgs == null) {
                    filteredRoles = declaredRoles;
                } else {
                    filteredRoles = filterRoles(declaredRoles, aArgs);
                }
                if (principal != null && principal.hasAnyRole(filteredRoles)) {
                    return;
                }
                throw new AccessControlException(String.format("Access denied to %s function in %s module for '%s'.",//NOI18N
                        aName,
                        appElementId,
                        principal != null ? principal.getName() : null));
            } else {
                checkModulePermissions();
            }
        } catch (Exception ex) {
            if (ex instanceof AccessControlException) {
                throw (AccessControlException) ex;
            } else {
                throw new AccessControlException(ex.getMessage());
            }
        }
    }
}
