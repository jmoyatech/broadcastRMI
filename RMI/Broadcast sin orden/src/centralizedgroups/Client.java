/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package centralizedgroups;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author jmoya
 */
public class Client extends UnicastRemoteObject implements ClientInterface{
    Queue<GroupMessage> colamsg;
    ReentrantLock lock = new ReentrantLock(true);
    Condition permision;
    boolean espera;
    String aliasClient;
    
    public Client(String aliasCliente) throws RemoteException{
        super();
        colamsg=new LinkedList();
        aliasClient=aliasCliente;
        permision=lock.newCondition();
        espera=false;
    }
    
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        
            // TODO code application logic here
        
        Client client;
        
         Scanner sc = new Scanner(System.in);
         int a,b;
         String alias;
         String hostname;
         String aliasgrupo;
         String aliasCliente;
         int port=1099;
         boolean mientras=false;
         
         System.setProperty("java.security.policy", "server.policy");
        
        if (System.getSecurityManager() == null){
        System.setSecurityManager(new SecurityManager());
        }
        
        
         System.out.printf("Inserte alias de cliente: ");
         aliasCliente=sc.nextLine(); 
         client=new Client(aliasCliente);
         do{
         try {  
            mientras=false;
            Naming.bind("//localhost:"+port+"/"+aliasCliente, client);
        } catch (AlreadyBoundException ex) {
            System.out.println("Inserte otro alias");
            aliasCliente=sc.nextLine();
            mientras=true;
        }
         }while(mientras);
         
         GroupServerInterface stub;
        
            stub = (GroupServerInterface) Naming.lookup("rmi://localhost:1099/HolaServer");
        
        

         do{
         
         
         System.out.println("Cliente lanzado. Elija opción:");
         System.out.println("1. Crear grupo");
         System.out.println("2. Eliminar grupo");
         System.out.println("3. Añadir miembro al grupo");
         System.out.println("4. Eliminar miembro del grupo");
         System.out.println("5. ENVIAR MENSAJE");
         System.out.println("6. VER MENSAJE");
         System.out.println("7. Número de grupos en servidor");
         System.out.println("8. Miembros en grupo a indicar");
         System.out.println("9. Terminar ejecución");             
             
             
         a=sc.nextInt();
          switch (a) {
            case 1:  
                System.out.println("\t--------------------CREAR GRUPO--------------------");
                System.out.printf("Inserte alias de grupo: \n");
                aliasgrupo=sc.next();                              
                System.out.printf("Inserte hostname de propietario: \n");
                hostname=sc.next();
                stub.createGroup(aliasgrupo, aliasCliente, hostname,port);
                System.out.println("Grupo creado\n");
                System.out.println("\t---------------------------------------------------");
                break;
            case 2: System.out.println("\t--------------------ELIMINACION DE GRUPO--------------------");
                System.out.print("\nInserte alias de grupo: \n");
                aliasgrupo=sc.next();
                System.out.print("\nInserte alias de propietario: \n");
                alias=sc.next(); 
                    if(!(stub.removeGroup(aliasgrupo, alias))){
                        System.out.println("No se pudo borrar el grupo seleccionado\n");
                    };
                    System.out.println("\t---------------------------------------------------");
                     break;
            case 3: System.out.println("\t--------------------NUEVO MIEMBRO--------------------");
                System.out.print("\nInserte alias de miembro nuevo: \n");
                alias=sc.next();
                System.out.print("\nInserte hostname: \n");
                hostname=sc.next(); 
                System.out.print("\nInserte id de grupo: \n");
                b=sc.nextInt();
                    if(stub.addMember(b, alias, hostname,port) ==null){
                        System.out.println("No se pudo añadir miembro");
                    }
                    else{
                         System.out.println("Miembro insertado");
                    }
                    System.out.println("\t---------------------------------------------------");
                     break;
            case 4: System.out.println("\t--------------------BORRAR MIEMBRO--------------------");
                System.out.print("\nInserte alias de miembro a borrar: \n");
                        alias=sc.next();
                        System.out.print("\nInserte hostname: \n");
                        hostname=sc.next(); 
                        System.out.print("\nInserte id de grupo: \n");
                        b=sc.nextInt();
                    if(!(stub.removeMember(b, alias))){
                        System.out.println("No se pudo eliminar miembro");
                    }
                    else{
                        System.out.println("Miembro eliminado");
                    };
                    System.out.println("\t---------------------------------------------------");
                     break;
            case 5:  System.out.println("\t--------------------ENVIAR MENSAJE--------------------");
                InputStreamReader leer = new InputStreamReader(System.in);
                BufferedReader buff = new BufferedReader(leer);
                System.out.print("Escriba el mensaje: \n");
                String mensaje="";
                try {
                    mensaje = buff.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.print("Inserte id de grupo: \n");
                int idgrupo=sc.nextInt();
                GroupMember gr=stub.isMember(idgrupo, aliasCliente);
                mensaje=mensaje+" | enviado por: "+aliasCliente;
                byte[] msg=mensaje.getBytes();
                stub.sendGroupMessage(msg,gr);
                System.out.println("\nEnviado");
                System.out.println("\t---------------------------------------------------");
                break;
            case 6:  System.out.println("\t--------------------VER MENSAJE--------------------");
               
                InputStreamReader leer2 = new InputStreamReader(System.in);
                BufferedReader buff2 = new BufferedReader(leer2);
                System.out.print("Nombre del grupo: \n");
                String mensaje2="";
                try {
                    mensaje2 = buff2.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                
             try {
                 System.out.println("\t\t"+new String(client.receiveGroupMessage(mensaje2), "UTF-8"));
             } catch (UnsupportedEncodingException ex) {
                 Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
             } catch (NullPointerException ex) {
                 System.out.println("Sin Mensajes");
             }
                
                System.out.println("\t---------------------------------------------------");
                     break;
                case 7: System.out.println("\t------------------NUMERO DE GRUPOS EN SERVER------------------");
                    System.out.println(stub.numgrup());
                    System.out.println("\t---------------------------------------------------");
                     break;
                    
            case 8: System.out.println("\t--------------------MIEMBROS DEL GRUPO--------------------");
                System.out.print("\nInserte id de grupo: \n");
                b=sc.nextInt(); 
                System.out.println(stub.MemberList(b));
                System.out.println("\t---------------------------------------------------");
                     break;
            case 9:  System.out.println("\t---------------------------------------------------");
                client.unexportObject(client, true);
                     break;
                
            
        
    }
         }while(a!=9);

    

    
    }
  @Override
    public synchronized void DepositMessage(GroupMessage m) throws RemoteException {
        lock.lock();
        this.colamsg.add(m);
        if(espera){
            permision.signal();
            espera=false;
        
        }
        lock.unlock();
    }

    @Override
    public byte[] receiveGroupMessage(String galias) throws RemoteException {
        lock.lock();
        ArrayList<GroupMessage> aux=new ArrayList();
        GroupMessage auxm;
        if(colamsg.isEmpty()){
            System.out.println("Esperando mensajes...");
            try {
                espera=true;
                permision.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while(!colamsg.isEmpty()){
            auxm=colamsg.poll();
            if(auxm.emisor.group.equals(galias)){
                if(!colamsg.isEmpty()){
                    aux.addAll(colamsg);    
                }
                if((aux!=null)&&(!aux.isEmpty())){
                    colamsg.addAll(aux);
                }
                lock.unlock();
                return auxm.msg;
            }
            aux.add(auxm);   
        }
        colamsg.addAll(aux);
        lock.unlock();
        return null;
    }
}

