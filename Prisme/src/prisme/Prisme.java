/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prisme;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game.GameType;

/**
 *
 * @author Anthony
 */
public class Prisme {
    
    
    
    /**
     * Connexion au serveur
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        
        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken("NDEzNjI5NTQyMzE0NDA5OTk0.DWbmmQ.oOIi1U5FAOoFdh9MOWU6O0hYcXI").buildAsync();
            jda.addEventListener(new BotListener());
            jda.getPresence().setGame(net.dv8tion.jda.core.entities.Game.of(GameType.WATCHING, "tout ce que tu fais", null));
            
        } catch (LoginException | IllegalArgumentException e) {}
    }
    
}
