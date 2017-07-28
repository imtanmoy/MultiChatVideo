/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverlab;

/**
 *
 * @author TanmoyBanik
 */
public interface ClientListener {

    void signIn(String userName);

    void signOut(String userNamme);

    void clientStatus(String status);

    void mapped(String nam, String ip);
}
