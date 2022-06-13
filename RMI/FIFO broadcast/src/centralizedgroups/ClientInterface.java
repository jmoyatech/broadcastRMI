/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author jmoya
 */
public interface ClientInterface extends Remote {
    public void DepositMessage(GroupMessage m) throws RemoteException;
    public byte[] receiveGroupMessage(String galias,GroupServerInterface stub) throws RemoteException;
}
