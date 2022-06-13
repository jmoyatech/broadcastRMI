/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.util.*;
import java.util.concurrent.locks.*;
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
public class ObjectGroup {
    String groupname;
    int idgroup;
    ArrayList<GroupMember> members;
    GroupMember propietario;
    int contador=0;
    ReentrantLock lock = new ReentrantLock(true);
    Condition free;
    boolean operando,permitir;
    int sendcont;
    LinkedList<SequenciaMem> seqmsg;
    
    
    public ObjectGroup(String group, int id, String alias, String hostname,int port){
        this.groupname=group;
        this.idgroup=id;
        members=new ArrayList();
        this.members.add(new GroupMember(alias, hostname, this.groupname, contador, id, port));
        this.propietario=members.get(0);
        this.contador=contador+1;
        operando=false;
        permitir=true;
        free=lock.newCondition();
        seqmsg=new LinkedList();
    }
    
    public int nextSeqNumber(int memberid){
       GroupMember miembro=null;
       for(int i=0;i<this.members.size();i++){
            if(this.members.get(i).idmember==memberid){
                miembro=this.members.get(i);
                break;
            }
        }
       if(miembro==null){
           return -1;
       }else if(seqmsg.isEmpty()){
                seqmsg.add(new SequenciaMem(miembro));
                int secuencia=seqmsg.get(0).Seq;
                seqmsg.get(0).Seq=seqmsg.get(0).Seq+1;
                return secuencia;
        }else{
            for(int i=0;i<seqmsg.size();i++){
                if(seqmsg.get(i).member.idmember==memberid){
                    int secuencia=seqmsg.get(i).Seq;
                    seqmsg.get(i).Seq=seqmsg.get(i).Seq+1;
                    return secuencia;
                }
            }
            seqmsg.add(new SequenciaMem(miembro));
            int secuencia=seqmsg.getLast().Seq;
            seqmsg.getLast().Seq=seqmsg.getLast().Seq+1;
            return secuencia;
        }
       
    }
    
    public GroupMember isMember(String alias){
        for(int i=0;i<this.members.size();i++){
            if(this.members.get(i).alias.equals(alias)){
                return this.members.get(i);
            }
        }
        return null;
    }
    
    public synchronized GroupMember addMember(String alias, String hostname, int port) throws InterruptedException{
        if(permitir){
        lock.lock();
        if(this.isMember(alias)!=null){
            lock.unlock();
            return null;
        }
        else{
            if(operando){
                free.await();
            }
            operando=true;
            GroupMember miembro=new GroupMember(alias, hostname, this.groupname, contador, idgroup, port);
            this.members.add(miembro);
            contador=contador+1;
            operando=false;
            free.signal();
            lock.unlock();
            return miembro;
        }
        }
        else{
            System.out.println("No se pudo añadir miembro en "+this.idgroup+". Las altas/bajas están bloqueadas");
            return null;
        }
    }
    
    public synchronized boolean removeMember(String alias) throws InterruptedException{
        if(permitir){
        lock.lock();
        if(operando){
               free.await();
            }
        operando=true;
        GroupMember miembro=this.isMember(alias);
        if((miembro!=null)&&(!miembro.equals(this.propietario))){
            this.members.remove(miembro);
            operando=false;
            free.signal();
            lock.unlock();
            return true;
        }
        else{
            operando=false;
            free.signal();
            lock.unlock();
            return false;
        } 
        }
        else{
            System.out.println("No se pudo eliminar miembro en"+this.idgroup+". Las altas/bajas están bloqueadas");
            return false;
        }
    }
    
//    public void StopMembers(){
//       permitir=false;
//    }
//    public void AllowMembers(){
//        permitir=true;
//    }
    public void Sending(){
        permitir=false;
        this.sendcont=this.sendcont+1;
    }
    
//    public void EndSending(GroupMember gm){
//        System.out.println("Fin envio emisor: "+gm.alias);
//        permitir=true;
//        
//    }
}
