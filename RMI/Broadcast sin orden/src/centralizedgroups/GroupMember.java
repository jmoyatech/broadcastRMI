

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.io.*;
/**
 *
 * @author jmoya
 */
public class GroupMember implements Serializable{
    String alias;
    String hostname;
    int idmember;
    int idgroup;
    int nport;
    String group;
    
    public GroupMember(String al, String host, String grup, int idm, int idg, int port){
        alias=al;
        hostname=host;
        idmember=idm;
        idgroup=idg;
        nport=port;
        group=grup;
    }
    
}
