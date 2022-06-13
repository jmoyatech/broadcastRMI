/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jmoya
 */
public class GroupServer extends UnicastRemoteObject implements GroupServerInterface{
    LinkedList<ObjectGroup> GroupList;
    int idgrupos;
    
    public GroupServer() throws RemoteException{
        super();
        this.GroupList=new LinkedList();
        idgrupos=-1;
    
    }
    
    
    public static void main(String[] args) {
        
        try {
            // TODO code application logic here
            GroupServer Server;
            
            System.setProperty("java.security.policy", "server.policy");
            if (System.getSecurityManager()== null){
            System.setSecurityManager(new SecurityManager());
            }
            int numPuerto=1099;
                LocateRegistry.createRegistry(numPuerto);
                Server=new GroupServer();
            try {
                Naming.rebind("//161.67.196.102"+":"+numPuerto+"/HolaServer", Server);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\t--------------------SERVIDOR LANZADO--------------------");
        
        
    }
    
    @Override
    public int numgrup() {
        return this.GroupList.size();
    }
    
    @Override
     public String MemberList(int grupid) {
         String solve="";
         
             for(int j=0;j<this.GroupList.get(grupid).members.size();j++){
             solve+="Alias: "+this.GroupList.get(grupid).members.get(j).alias+" Hostname: "+this.GroupList.get(grupid).members.get(j).hostname+"\n";
             }
         
         
        return solve;
    }

    @Override
    public int createGroup(String galias, String oalias, String ohostname, int puerto) {
        if(findGroup(galias)==-1){
            idgrupos=idgrupos+1;
            GroupList.add(new ObjectGroup(galias, idgrupos, oalias, ohostname, puerto));
            System.out.println("Grupo creado, ahora hay "+GroupList.size());
            return idgrupos;
        }
        else{
            return -1;
        }
    }

    @Override
    public int findGroup(String galias) {
        for(int i=0; i<this.GroupList.size();i++){
            if(GroupList.get(i).groupname.equals(galias)){
                return GroupList.get(i).idgroup;
            }
        }
        return -1;
    }

    @Override
    public boolean removeGroup(String galias, String oalias) {
        for(int i=0; i<this.GroupList.size();i++){
            if(GroupList.get(i).groupname.equals(galias) && GroupList.get(i).propietario.alias.equals(oalias)){
                this.GroupList.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public GroupMember addMember(int gid, String alias, String hostname, int puerto) {
        for(int i=0; i<this.GroupList.size();i++){
            if(GroupList.get(i).idgroup==gid){
                if(GroupList.get(i).isMember(alias)==null){
                    try {
                        System.out.println("Miembro: Alias: "+alias+" Hostname: "+hostname +" insertado en "+gid+" con puerto "+puerto);
                        return GroupList.get(i).addMember(alias, hostname, puerto);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    return null;
                }
            }
        }
        return null;
    }
    
    public boolean removeMember(int gid, String alias) {
        for(int i=0; i<this.GroupList.size();i++){
            if(GroupList.get(i).idgroup==gid){
                if(GroupList.get(i).isMember(alias)!=null){
                    try {
                        GroupList.get(i).removeMember(alias);
                        System.out.println("Miembro: Alias: "+alias+"eliminado de "+gid);
                        return true;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public GroupMember isMember(int gid, String alias) {
         for(int i=0; i<this.GroupList.size();i++){
              if(GroupList.get(i).idgroup==gid){
                  return GroupList.get(i).isMember(alias);
              }
         }
         return null;
    }

    
//    public boolean StopMembers(int gid) {
//        for(int i=0; i<this.GroupList.size();i++){
//              if(GroupList.get(i).idgroup==gid){
//                  GroupList.get(i).StopMembers();
//                  System.out.println("Bloqueo de cambios en "+gid);
//                  return true;
//              }
//         }
//        return false;
//    }
//
//    
//    public boolean AllowMembers(int gid) {
//        for(int i=0; i<this.GroupList.size();i++){
//              if(GroupList.get(i).idgroup==gid){
//                  GroupList.get(i).AllowMembers();
//                  System.out.println("Desbloqueo de cambios en "+gid);
//                  return true;
//              }
//         }
//        return false;
//    }

    @Override
    public boolean sendGroupMessage(byte[] msg,GroupMember gm) throws RemoteException {
        boolean correcto=true;
        ObjectGroup grupo=null;
        ArrayList<ClientInterface> clientes=new ArrayList<ClientInterface>();

        for(int i=0;i<this.GroupList.size();i++){
            if(this.GroupList.get(i).idgroup==gm.idgroup){
                grupo=this.GroupList.get(i);
                break;
            }
        }
        
        for(int i=0;i<grupo.members.size();i++){
            if((!gm.alias.equals(grupo.members.get(i).alias))&&(gm.idgroup==grupo.members.get(i).idgroup)){
                Registry reg=LocateRegistry.getRegistry(grupo.members.get(i).hostname,grupo.members.get(i).nport);
                
                try {
            
            ClientInterface a=(ClientInterface) reg.lookup(grupo.members.get(i).alias);
            
            clientes.add(a);

        } catch (NotBoundException ex) {
            correcto=false;
        } 
            }
        }
        
        if(correcto){
            grupo.Sending();
            GroupMessage mens=new GroupMessage(msg,gm,grupo.nextSeqNumber(gm.idmember));
            SendingMessage envio= new SendingMessage(mens,grupo,clientes);
            return true;
        }
        else{
            return false;
        }
        
    }
}
