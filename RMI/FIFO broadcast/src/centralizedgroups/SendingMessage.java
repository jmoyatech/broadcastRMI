/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmoya
 */
public class SendingMessage extends Thread{
    GroupMessage message;
    ObjectGroup group;
    ArrayList<ClientInterface> clientes;
    
    public SendingMessage(GroupMessage mg, ObjectGroup gr, ArrayList<ClientInterface> s){
        message=mg;
        group=gr;
        clientes=s;
        start();
    }

    public void run(){
        for(int i=0;i<clientes.size();i++){
            try {
//              try {
//                   Thread.sleep((long) (Math.random() * (60000-30000+1) + 30000));
//                }catch (InterruptedException ex) {
//                    Logger.getLogger(SendingMessage.class.getName()).log(Level.SEVERE, null, ex);
//                }
                clientes.get(i).DepositMessage(message);
            } catch (RemoteException ex) {
                Logger.getLogger(SendingMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
    }
}
