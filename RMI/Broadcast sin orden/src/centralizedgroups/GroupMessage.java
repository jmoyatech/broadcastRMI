/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.io.Serializable;

/**
 *
 * @author jmoya
 */
public class GroupMessage implements Serializable{
    byte[] msg;
    GroupMember emisor;
    
    public GroupMessage(byte[] message, GroupMember miembro){
        msg=message;
        emisor=miembro;
    }
}
