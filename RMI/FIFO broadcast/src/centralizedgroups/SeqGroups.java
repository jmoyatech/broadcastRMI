/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.util.ArrayList;

/**
 *
 * @author jmoya
 */
public class SeqGroups {
    int idgroup;
    ArrayList<SequenciaMem> Seqmemb;
    
    public SeqGroups(int id, ArrayList<SequenciaMem> s){
        idgroup=id;
        Seqmemb=s;
    }
}
