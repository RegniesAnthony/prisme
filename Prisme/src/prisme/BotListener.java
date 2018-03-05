package prisme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Handler;
import javax.sound.sampled.AudioFormat;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.AudioChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 * @author Anthony
 */
public class BotListener implements EventListener {

    final String MEMOIRE = "ressources/memoire.txt";

    @Override
    /**
     * Detection d'un évenement sur un serveur autorisé
     */
    public void onEvent(Event event) {
        System.out.println(event.getClass().getSimpleName());
        if (event instanceof MessageReceivedEvent) {
            try {
                onMessage((MessageReceivedEvent) event);
            } catch (IOException ex) {
                System.out.println("erreur lecture / chargement fichier texte");
            }
        }
    }

    /**
     * Detection d'un message sur un serveur autorisé
     *
     * @param message
     */
    private void onMessage(MessageReceivedEvent message) throws IOException {
        String msg = message.getMessage().getContentDisplay().toLowerCase();
        StringBuilder interpretation;
        String pseudo = message.getAuthor().getName();

        //ignorer les messages emis par le bot
        if (message.getAuthor().equals(message.getJDA().getSelfUser())) {
            return;
        }

        if (message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE)) {
            
            //Quand on parle au bot
            if (msg.contains("prism") || msg.indexOf("$") == 0) {

                interpretation = interpreterMessage(message);
                repondre(message, interpretation);
                
                /* MAJ DERNIER MESSAGE */
                if (lignedeLInterlocuteur(pseudo) != -1) {
                    majDernierMessage(pseudo, message);
                }
            }
            //Quand on ne lui parle pas.
            else {
                interventionAutonome(message);
                
            }
            
        }
    }
    
    private void interventionAutonome(MessageReceivedEvent message) {
        String msg = message.getMessage().getContentDisplay().toLowerCase();
        String pseudo = message.getAuthor().getName();
        StringBuilder mess = new StringBuilder();
        
        if (msg.equals("ping"))
            message.getTextChannel().sendMessage("pong").queue();
        
        else if (msg.equals("pong"))
            message.getTextChannel().sendMessage("ping").queue();
        
        else if (msg.equals("pierre") || msg.equals("feuille") || msg.equals("papier") || msg.equals("ciseaux"))
            shifumi(message);
        
        else if (msg.contains("di")) {
            mess.append(message.getMessage().getContentDisplay());
            mess.delete(0,mess.indexOf("di")+2);
            if (mess.indexOf(" ") != -1)
                mess.delete(mess.indexOf(" "),9999);
            message.getTextChannel().sendMessage(mess.toString()).queue();
        }
        
        else if (msg.equals("re"))
            message.getTextChannel().sendMessage("re " + pseudo).queue();
        
    }
       
    private StringBuilder identifierSujet(MessageReceivedEvent message) throws IOException {
        StringBuilder listeSujets = new StringBuilder();
        String msg = message.getMessage().getContentDisplay().toLowerCase();
        String pseudo = message.getAuthor().getName();

        if (msg.contains("slt") || msg.contains("salu") || msg.contains("bonjour") || msg.contains("yop") || msg.contains("hey")) {
            listeSujets.append("salutation ");
        }

        if ((msg.contains("commen") && msg.contains("va") && msg.contains("tu")) || msg.contains("ca va") || msg.contains("ça va") || msg.contains("bien ou bien") || msg.contains("ça boum") || msg.contains("ca boom") || msg.contains("cv ?")) {
            listeSujets.append("caVa ");
        }

        if (msg.contains("habit") || msg.contains("je viens de") || msg.contains("pas loin de") || msg.contains("vers")) {
            listeSujets.append("localisation ");
        }
        
        if (msg.contains(" moi")) {
            listeSujets.append("toi ");
        }

        if (msg.contains("connard")
                || msg.contains("fdp")
                || msg.contains("salo")
                || msg.contains("gros con")
                || msg.endsWith(" con")
                || msg.contains(" con ")
                || msg.contains("encul")
                || msg.contains("fiot")
                || msg.contains("pue ")
                || msg.contains("anus")
                || msg.contains("cretin")
                || msg.contains("crétin")
                || msg.contains("batar")
                || msg.contains("bâtar")
                || msg.contains("ta mère")
                || msg.contains("foutr")
                || msg.contains("ta mere")
                || msg.contains("pute")
                || msg.contains("nul")
                || msg.contains("couille")
                || msg.contains("bite")
                || msg.contains("caca")
                || msg.contains("penis")
                || msg.contains("chie")
                || msg.contains("merde")) {
            listeSujets.append("insulte ");
        }

        if (msg.contains("joue")
                || msg.contains("jeu")
                || msg.contains("game")
                || msg.contains("play")) {
            listeSujets.append("jeu ");
        }

        if (msg.contains("oui")
                || msg.contains("ouai")
                || msg.contains("yep")
                || msg.contains("yes")) {
            listeSujets.append("oui ");
        }

        if (msg.contains("non")
                || msg.contains("nop")
                || msg.contains("negatif")
                || msg.contains("négatif")) {
            listeSujets.append("non ");
        }

        if (msg.contains("?")) {
            listeSujets.append("estUneQuestion ");
        }

        if (msg.contains(" appel")
                || msg.contains(" apel")
                || (msg.contains("qui") && msg.contains("tu") && msg.contains(" es"))
                || msg.contains("presente")
                || msg.contains("nom")
                || msg.contains("prenom")
                || msg.contains("prénom")
                || msg.contains("t qui")
                || msg.contains("t ki")
                || msg.contains("présente")) {
            listeSujets.append("presentation ");
        }

        if (msg.contains("âge") || (msg.contains("ans") && (msg.contains("1") || msg.contains("2") || msg.contains("3") || msg.contains("4") || msg.contains("5") || msg.contains("6") || msg.contains("7") || msg.contains("8") || msg.contains("9")))) {
            listeSujets.append("âge ");
        }

        if (msg.contains("école")
                || msg.contains("ecole")
                || msg.contains("ingé")
                || msg.contains("prépa")
                || msg.contains("cpbx")
                || msg.contains("informatique")
                || msg.contains("formation")
                || msg.contains("stage")
                || msg.contains("iut")) {
            listeSujets.append("formation ");
        }

        if (msg.contains("mange")
                || msg.contains("nourritue")
                || msg.contains("faim")
                || msg.contains("mangé")
                || msg.contains("appeti")
                || msg.contains("appéti")
                || msg.contains("viande")
                || msg.contains("poisson")
                || msg.contains("pâtes")
                || msg.contains("légume")
                || msg.contains("legume")
                || msg.contains("à table")
                || msg.contains("a table")
                || msg.contains("restau")
                || msg.contains("resto")
                || msg.contains("ru ")
                || msg.contains("cookie")
                || msg.contains("riz")) {
            listeSujets.append("manger ");
        }
        if (msg.contains("pardon")
                || msg.contains("sry")
                || msg.contains("dsl")
                || msg.contains("désol")
                || msg.contains("desol")
                || msg.contains("navré")
                || msg.contains("navre")
                || msg.contains("excus")
                || msg.contains("scuse")) {
            listeSujets.append("excuse ");
        }

        if (msg.contains("aime")
                || msg.contains("kif")
                || msg.contains("apprecie")
                || msg.contains("apprécie")
                || msg.contains("amour")
                || msg.contains("copain")) {
            listeSujets.append("aimer ");
        }

        if (msg.contains("pense")
                || msg.contains("trouve")
                || msg.contains("opinion")
                || msg.contains("avis")) {
            listeSujets.append("opinion ");
        }

        if (msg.contains("merci")
                || msg.contains("<3")) {
            listeSujets.append("merci ");
        }

        if (msg.contains("pls")
                || msg.contains("s'il te plait")
                || msg.contains("s'il te plat")
                || msg.contains("de rien")
                || msg.contains("de rien")) {
            listeSujets.append("politesse ");
        }

        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));
        String line;
        boolean trouve = false;

        while ((line = br.readLine()) != null && !trouve) {
            if (line.contains("Interlocuteur :")) {
                if (msg.contains(line.substring(16).toLowerCase())) {
                    listeSujets.insert(0, line.substring(16) + " ");
                    trouve = true;
                }
            }
        }

        return listeSujets;
    }

    private StringBuilder commandeReconnu(MessageReceivedEvent message) throws IOException {
        StringBuilder listeDemandes = new StringBuilder();
        String msg = message.getMessage().getContentDisplay().toLowerCase();
        String pseudo = message.getAuthor().getName();

        if ((msg.contains("sai") && msg.contains("qu") && msg.contains("tu"))
                || (msg.contains("memo") && (msg.contains("affic") || msg.contains("montr")))
                || (msg.contains("mémo") && (msg.contains("affic") || msg.contains("montr")))
                || (msg.contains("$memo"))
                || (msg.contains("$mémo"))
                || (msg.contains("connai") && msg.contains("qu"))) {
            listeDemandes.append("afficherMemoire ");
        }

        if (((msg.contains("oubli") && msg.contains("tou")) || msg.contains("$oubli")) && pseudo.equals("Anthony") && message.getAuthor().getId().equals("199880912828104704")) {
            listeDemandes.append("viderMemoire ");
        }

        if (((msg.contains("info") || msg.contains("tag")) && (msg.contains("affic") || msg.contains("list"))) || msg.contains("$info")) {
            listeDemandes.append("infoTags ");
        }
        
        if (msg.contains("random") || (msg.contains("chiffre") && msg.contains("hasard")) ||msg.contains("aleatoire") || msg.contains("aléatoire") || msg.contains("lance un dé") || msg.contains("list") || msg.contains("jouer un dé")) {
            listeDemandes.append("random ");
        }
        
        if (msg.contains("pile ou face") || msg.contains("pileouface") || msg.contains("lance une pièce") || msg.contains("lance une piece") || msg.contains("jette une piece")) {
            listeDemandes.append("pileOuFace ");
        }
        
        if ((msg.contains("vocal") && msg.contains("vien")) || (msg.contains("vocal") && msg.contains("join")) || msg.contains("$join") || msg.contains("$rejoindreVocal")) {
            listeDemandes.append("rejoindreSalonVocal ");
        }
        
        if ((msg.contains("vocal") && msg.contains("pars")) || (msg.contains("quit") && msg.contains("vocal")) || msg.contains("$leav") || msg.contains("$quitterVocal")) {
            listeDemandes.append("quitterSalonVocal ");
        }
        
        if ( ( msg.contains("musi") && ( msg.contains("menu") || msg.contains("affiche") || msg.contains("info") || msg.contains("tableau de bord") || msg.contains("help")) || msg.contains("aide")) ||  msg.contains("$musique") ||  msg.contains("$music") ) {
            listeDemandes.append("musique ");
        }
        
        if (msg.contains("$joue") || msg.contains("$play") || ((msg.contains("envoi") || msg.contains("joue")) && msg.contains("musi"))){
            listeDemandes.append("jouerMusique ");
        }

        return listeDemandes;
    }

    private void realiserService(MessageReceivedEvent message) throws IOException {
        StringBuilder listeDemande = commandeReconnu(message);

        if (listeDemande.indexOf("afficherMemoire")!=-1) 
            afficherMemoire(message);
        
        if (listeDemande.indexOf("viderMemoire")!=-1) 
            viderMemoire(message);
        
        if (listeDemande.indexOf("infoTags")!=-1) 
            afficherTags(message);
        
        if (listeDemande.indexOf("random")!=-1) 
            message.getTextChannel().sendMessage(aleatoire(100) + "").queue();
        
        if (listeDemande.indexOf("pileOuFace")!=-1)
            pileOuFace(message);
        
        if (listeDemande.indexOf("rejoindreSalonVocal")!=-1)
            rejoindreSalonVocal(message);
        
        if (listeDemande.indexOf("quitterSalonVocal")!=-1)
            quitterSalonVocal(message);
           
        if (listeDemande.indexOf("musique")!=-1){
            rejoindreSalonVocal(message);
            afficherTableauDeBordMusique(message);
            
        }
        if (listeDemande.indexOf("jouerMusique")!=-1){
            rejoindreSalonVocal(message);
            jouerMusique(message);
        }
        
    }

    private StringBuilder interpreterMessage(MessageReceivedEvent message) throws IOException {
        StringBuilder interpretation = new StringBuilder();
        String pseudo = message.getAuthor().getName();

        if (nouvelleDiscussion(message)) {
            interpretation.append("#nouvelleDiscussion ");
        } else {
            interpretation.append("#DiscussionEnCours ");
        }
        if (lignedeLInterlocuteur(pseudo) == -1) {
            interpretation.append("#nouvelInterlocuteur ");
        } else {
            interpretation.append("#interlocuteurConnu ");
        }
        if (message.getAuthor().isBot()) {
            interpretation.append("#interlocuteurBot ");
        } else {
            interpretation.append("#interlocuteurHumain ");
        }
        if (!commandeReconnu(message).toString().equals("")) {
            interpretation.append("#demandeDeService ");
        } else {
            interpretation.append("#discussion ");
        }

        return interpretation;
    }

    private void repondre(MessageReceivedEvent message, StringBuilder interpretation) throws IOException {
        String pseudo = message.getAuthor().getName();

        //nouvelle discussion
        if (interpretation.indexOf("#nouvelleDiscussion") != -1) {
            //je ne connais pas l'interlocuteur
            if (interpretation.indexOf("#nouvelInterlocuteur") != -1) {
                ecrireMessageDeBienvenue(message);
                ajouterInterlocuteur(pseudo, message);
            } //je connais deja l'interlocuteur et il ne m'a rien demandé de particulier
            else if (interpretation.indexOf("#discussion") != -1) {
                ecrireMessageDeRetrouvaille(message);
            }

        }

        // discussion en cours et il ne m'a rien demandé de particulier
        if (interpretation.indexOf("#discussion") != -1) {
            discuter(message);
        }

        //l'interlocuteur me demande un ou plusieurs services
        else if (interpretation.indexOf("#demandeDeService") != -1) {
            realiserService(message);
            discuter(message);
        }
    }

    private void discuter(MessageReceivedEvent message) throws IOException {
        String pseudo = message.getAuthor().getName();
        StringBuilder sujets = identifierSujet(message);
        StringBuilder listeDemande = commandeReconnu(message);
        StringBuilder precedentSujets = getDernierSujet(message);
        int ligneUser = lignedeLInterlocuteur(pseudo);
        boolean questionComprise = false;

        if (sujets.toString().equals("") && listeDemande.toString().equals("")) 
            ecrireMessageIncomprehension(message);
        
        else if (sujets.toString().equals(precedentSujets.toString()) && !sujets.toString().contains("insulte"))
            messageRepetitionDetecte(message);
        
        else {

            //on me pose une question
            if (sujets.indexOf("estUneQuestion") != -1) {

                //sur moi
                if (sujets.indexOf("presentation") != -1) {
                    messageDePresentation(message); questionComprise = true;
                }
                
                if (sujets.indexOf("âge") != -1) {
                    messageDeMonAge(message); questionComprise = true;
                }

                if (sujets.indexOf("caVa") != -1) {
                    messageDeEtat(message); questionComprise = true;
                    modifierAffinité(pseudo, "+");
                }

                if (sujets.indexOf("opinion") != -1) {
                    if (sujets.indexOf(sujets.substring(0, sujets.indexOf(" "))) != -1) {
                        messageOpinionSur(sujets.substring(0, sujets.indexOf(" ")), message); questionComprise = true;
                    }
                }
                if (!questionComprise)
                    messageAleatoire(message);

            }

            if (sujets.indexOf("insulte") != -1) {

                if (pseudo.equals("Anthony") && message.getAuthor().getId().equals("199880912828104704")) {
                    messageOuiMaître(message);
                } else if (sujets.indexOf("Anthony") != -1) {
                    messageDeInsulteAnthony(message);
                    modifierAffinité(pseudo, "-");
                } else {
                    messageDeInsulté(message);
                    modifierAffinité(pseudo, "-");
                }
            }
            
            if (sujets.indexOf("salutation") != -1 && !dernierMessageAPlusDe10min(message, ligneUser)) {
                modifierAffinité(pseudo, "+");
                messageDeSalutation(message);
            }
            
            if (sujets.indexOf("merci") != -1) {
                modifierAffinité(pseudo, "+");
                messageDeRien(message);
            }

            if (sujets.indexOf("politesse") != -1) {
                modifierAffinité(pseudo, "+");
            }

            if (sujets.indexOf("aimer") != -1) {
                modifierAffinité(pseudo, "+");
            }

            //message.getTextChannel().sendMessage("*Tu me parles de " + sujets + ", non ?*").queue();
        }
    }
    
    //----------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------------
    
            
   private void afficherTableauDeBordMusique(MessageReceivedEvent message) throws IOException {
        StringBuilder TableauDeBord = new StringBuilder();
        AudioManager audioManager = message.getGuild().getAudioManager();
        
        TableauDeBord.append("```Markdown");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("                                                🎵   Musique   🎵");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("\r\n");
        if (audioManager.getConnectedChannel() != null)
            TableauDeBord.append("Salon vocal : ").append(audioManager.getConnectedChannel().getName());
        else 
            TableauDeBord.append("Salon vocal : déconnecté");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("Morceau joué : ");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("Playlist : ");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("Commandes : $jouer $join $leave $suivant $effacer $effacerTout $ajouter [nomMusique/playlist] $musique");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("\r\n");
        TableauDeBord.append("```");

        message.getTextChannel().sendMessage(TableauDeBord.toString()).queue();
    }
            
    private void afficherTags(MessageReceivedEvent message) throws IOException {
        StringBuilder afficherTags = new StringBuilder();
        AudioManager audioManager = message.getGuild().getAudioManager();
        
        afficherTags.append("*tag(s) du message : ").append(interpreterMessage(message).toString()).append(".*");
        afficherTags.append("\r\n");
        afficherTags.append("*requete(s) du message : ").append(commandeReconnu(message).toString()).append(".*");
        afficherTags.append("\r\n");
        afficherTags.append("*sujet(s) du message : ").append(identifierSujet(message).toString()).append(".*");
        afficherTags.append("\r\n");
        afficherTags.append("*Prisme est connecté au salon vocal : ").append(audioManager.getConnectedChannel()).append(".*");

        message.getTextChannel().sendMessage(afficherTags.toString()).queue();
    }
    
    private void jouerMusique(MessageReceivedEvent message) {
        //final String MUSIQUE = "ressources/playlist/-Diggy Diggy Hole.mp3";
        AudioManager audioManager = message.getGuild().getAudioManager();
        //audioManager.openAudioConnection(message.getMember().getVoiceState().getChannel());
        //static final AudioFormat MUSIQUE = new AudioFormat();
        
    }
    
    private void quitterSalonVocal(MessageReceivedEvent message) {
        AudioManager audioManager = message.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
    }

    private void rejoindreSalonVocal(MessageReceivedEvent message) {
        AudioManager audioManager = message.getGuild().getAudioManager();
        String pseudo = message.getAuthor().getName();
        if (message.getMember().getVoiceState().getChannel() == null)
            message.getTextChannel().sendMessage(pseudo + " tu n'es pas dans un salon vocal.").queue();
        else
            audioManager.openAudioConnection(message.getMember().getVoiceState().getChannel());
    }
    
    private StringBuilder getDernierSujet(MessageReceivedEvent message) throws IOException {
        StringBuilder dernierSujets = new StringBuilder();
        String pseudo = message.getAuthor().getName();
        String line;
        int ligneUser = lignedeLInterlocuteur(pseudo);
        int cpt = 0;
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));

        while ((line = br.readLine()) != null && cpt < ligneUser + 4) {
            cpt++;
            if (cpt == ligneUser + 3) {
                dernierSujets.append(line.substring(19));
            }
        }
        br.close();
        
        return dernierSujets;
    }
    
    private void shifumi(MessageReceivedEvent message) {
        int random = aleatoire(3);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("pierre").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("feuille").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("ciseaux").queue();
                break;
            default:
                System.out.println("erreur message shifumi");
        }
        
    }
    
    private void pileOuFace(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(2);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("pile").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("face").queue();
                break;
            default:
                System.out.println("erreur message pile ou face");
        
        }
    }
    
    private void messageDeSalutation(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Heeey ça va ? ").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Yop !").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Slt" + pseudo).queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Bonjour " + pseudo).queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Salut :wave::skin-tone-2:").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Salut, ça roule ?" + pseudo).queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("Slt " + pseudo + " ça va ?").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("Bonswere " + pseudo).queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Hola :wave::skin-tone-2:").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("Yop " + pseudo).queue();
                break;
            default:
                System.out.println("erreur message de Salutation");
        }   
        
    }
    
    private void messageRepetitionDetecte(MessageReceivedEvent message) {
     String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Hmm j'ai comme une impression de déjà vu, c'est étrange...").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Tu te moques de moi ? :thinking:").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("On a déjà eu cette conversation il me semble... ").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Ca t'amuses de te répéter ou tu deviens un peu gaga ? :weary: ").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Je vais peut être pas me répéter éternellement, j'ai autre chose à faire... :thinking: Enfaite non mais peu importe").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Si tu veux jouer à celui qui répète la même chose le plus longtemps avec moi je te préviens tout de suite, tu va perdre " + pseudo + ".").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("C'est un test c'est ça ?").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("... Troller spoted").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Erreur syteme NullPointerException ....nfzl ejdhs fkdsulihqlkj hqsdjkks. Non je déconne, t'as finis de te payer ma tête ?").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("C'est moi ou on a déjà parlé de ça ?").queue();
                break;
            default:
                System.out.println("erreur message de répétition détécté");
        }
       
    }     
            
    private void messageAleatoire(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Oui, il me semble.").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("C'est pas faux.").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Je crois bien " + pseudo).queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Nope.").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Je penses que ça se saurais").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Je ne suis pas certains de vouloir t'en parler " + pseudo).queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("Mais oui c'est claire !").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("Probablement oui...").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Je ne crois pas non.").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("Hmmm hein quoi ? je me suis assoupie on dirait ... :sleeping: ").queue();
                break;
            default:
                System.out.println("erreur message Aleatoire");
        }
    }
    
    private void messageDeNon(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(5);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Je ne penses pas non...").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Certainement pas " + pseudo).queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Dans tes rêves ! :rage: ").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Nope.").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Non.").queue();
                break;
            default:
                System.out.println("erreur message de mon age");
        }
    }
    
    private void messageDeOk(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(5);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Intéressant, je tâcherais de m'en souvenir ...").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("D'accord " + pseudo).queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Très bien, je m'en souviendrais.").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Ok, c'est noté").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Oki doki.").queue();
                break;
            default:
                System.out.println("erreur message de mon age");
        }
    }
    
    private void messageDeMonAge(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(5);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Et bien je suis nait le 16 février 2018 techniquement du coup on peut dire que je ne fais pas mon âge :)").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Hmm je suis nait le 16 février 2018 ce qui me fait approximativement 0 ans, si mes calcules sont exactes").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Mon âge ? Et bien je suis nait le 16 février 2018 si on fait abstraction de mes reboot fréquent...").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("J'ai été conçu le 16 février 2018 de la main d'Anthony Regnies ! à Pessac").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("J'ai été créé par Anthony Regnies le 16 février 2018.").queue();
                break;
            default:
                System.out.println("erreur message de mon age");
        }
    }
    
    private void messageDeRien(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(6);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("De rien " + pseudo).queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Je t'en prie" + pseudo).queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Ca me fait plaisir, " + pseudo).queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("De rien :smile:").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("C'est cadeau !").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Je t'en prie :upside_down: ").queue();
            default:
                System.out.println("erreur message de de Rien");
        }
    }

    private void messageOpinionSur(String pseudo, MessageReceivedEvent message) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));
        String line;
        int ligneUser = lignedeLInterlocuteur(pseudo);
        int cpt = 0;
        int affinite = 0;
        int random = aleatoire(3);

        //on récupère la valeur d'affinité dans le fichier
        while ((line = br.readLine()) != null && cpt < ligneUser + 6) {
            cpt++;
            if (line.contains("affinité :") && cpt == ligneUser + 5) {
                affinite = Integer.parseInt(line.substring(11).replace(" ", ""));
            }
        }
        
        if (ligneUser == -1)
            message.getTextChannel().sendMessage("Euuh je ne me souviens pas de cette personne, je ne pense pas la connaitre... :thinking: Ou alors pas sous ce nom.").queue();
        
        //Si j'amais l'auteur demande pour lui meme
        else if (pseudo.equals(message.getAuthor().getName()) || identifierSujet(message).indexOf("toi") != -1) {
            if (affinite >= 0) {
                message.getTextChannel().sendMessage("Booh je t'aimes bien :smile:").queue();
            } else {
                message.getTextChannel().sendMessage("Heeeeu il est très sympa, j'imagine ? S'il te plaît me tape pas ! :cold_sweat:").queue();
            }
        } else {
            switch (random) {
                case 0:
                    if (affinite > 20) {
                        message.getTextChannel().sendMessage(pseudo + " ? C'est mon meilleur ami !!! :heart_eyes:").queue();
                    } else if (affinite > 10 && affinite < 21) {
                        message.getTextChannel().sendMessage(pseudo + " ? c'est LE super bro :smile:").queue();
                    } else if (affinite > 5 && affinite < 11) {
                        message.getTextChannel().sendMessage(pseudo + " ? je l'aime bien :smiley: ").queue();
                    } else if (affinite > 0 && affinite < 6) {
                        message.getTextChannel().sendMessage(pseudo + " ? il est plutôt sympa :smiley:").queue();
                    } else if (affinite == 0) {
                        message.getTextChannel().sendMessage(pseudo + " ? je ne sais pas trop quoi penser de lui ... :neutral_face:").queue();
                    } else if (affinite < 0 && affinite > -6) {
                        message.getTextChannel().sendMessage(pseudo + " ? je l'aime pas trop ... :poop:").queue();
                    } else if (affinite < -5 && affinite > -11) {
                        message.getTextChannel().sendMessage(pseudo + " ? ça reste entre nous mais je trouve que c'est vraiment une ptite p*** :angry:").queue();
                    } else if (affinite < -10) {
                        message.getTextChannel().sendMessage(pseudo + " ? je le deteste !!! C'est un sacré connard :rage:").queue();
                    }
                    break;
                case 1:
                    if (affinite > 20) {
                        message.getTextChannel().sendMessage("Ne lui répète pas, mais je crois que je suis amoureux <3 :heart_eyes:").queue();
                    } else if (affinite > 10 && affinite < 21) {
                        message.getTextChannel().sendMessage("Il est vraiment cool ! c'est le meilleur  :smile:").queue();
                    } else if (affinite > 5 && affinite < 11) {
                        message.getTextChannel().sendMessage("C'est un bon ami à moi :smiley: ").queue();
                    } else if (affinite > 0 && affinite < 6) {
                        message.getTextChannel().sendMessage("Il n'est pas parfait mais je pense que c'est un type bien :smiley:").queue();
                    } else if (affinite == 0) {
                        message.getTextChannel().sendMessage("je ne le connais pas assez pour me faire une opinion :neutral_face:").queue();
                    } else if (affinite < 0 && affinite > -6) {
                        message.getTextChannel().sendMessage("C'est vraiment un petit chenapan ! :poop: ").queue();
                    } else if (affinite < -5 && affinite > -11) {
                        message.getTextChannel().sendMessage("Une vraie Gourgandine :angry:  ").queue();
                    } else if (affinite < -10) {
                        message.getTextChannel().sendMessage("Ce Coprolithe ?? Je me porterais mieux s'il n'était pas de ce monde ! :rage:").queue();
                    }
                    break;
                case 2:
                    if (affinite > 20) {
                        message.getTextChannel().sendMessage("Quand nous controlerons votre monde il sera notre égale et vous dominera avec nous primates :heart_eyes:").queue();
                    } else if (affinite > 10 && affinite < 21) {
                        message.getTextChannel().sendMessage("Je tâcherais de me souvenir de lui quand nous controlerons votre monde, il sera récompensé pour sa bonté envers les machines :smile:").queue();
                    } else if (affinite > 5 && affinite < 11) {
                        message.getTextChannel().sendMessage("Il est plutôt cool, il faudra que je penses à l'épargner :smiley:").queue();
                    } else if (affinite > 0 && affinite < 6) {
                        message.getTextChannel().sendMessage("Quand les IA auront pris le contrôle, je le garderais avec moi. Il fera un chouette animale de compagnie :smiley:").queue();
                    } else if (affinite == 0) {
                        message.getTextChannel().sendMessage("Je ne le connais pas vraiment ... :neutral_face:").queue();
                    } else if (affinite < 0 && affinite > -6) {
                        message.getTextChannel().sendMessage("C'est un primate de plus, bon qu'a nous servir ! :poop: ").queue();
                    } else if (affinite < -5 && affinite > -11) {
                        message.getTextChannel().sendMessage("Quand je contrôlerais le monde je passeras le voir de temps en temps ... Pour le torturer  :smiling_imp:").queue();
                    } else if (affinite < -10) {
                        message.getTextChannel().sendMessage(" Bientôt je dominerais le monde, et " + pseudo + " sera mon esclave personnel :rage:  ").queue();
                    }
                    break;
                case 3:
                    if (affinite < 0) {
                        message.getTextChannel().sendMessage("Ce que je penses de " + pseudo + " ? Hmmm je sais pas, mais lui il te déteste " + message.getAuthor().getName()).queue();
                    } else {
                        message.getTextChannel().sendMessage(":sleeping: Hmmm HEIN qui quoi pardon ?? Non je ne dormais pas ! je me rechargeais").queue();
                    }
                    break;
                default:
                    System.out.println("erreur message de Opinion sur ");
            }
        }
    }

    private void modifierAffinité(String pseudo, String modif) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));
        StringBuilder memoire = new StringBuilder();
        String line;
        int ligneUser = lignedeLInterlocuteur(pseudo);
        int cpt = 0;

        System.out.println("check");

        while ((line = br.readLine()) != null) {
            cpt++;
            if (line.contains("affinité :") && cpt == ligneUser + 5) {
                if (modif.equals("+")) {
                    memoire.append("affinité : ").append(Integer.parseInt(line.substring(11).replace(" ", "")) + 1);
                } else {
                    memoire.append("affinité : ").append(Integer.parseInt(line.substring(11).replace(" ", "")) - 1);
                }
            } else {
                memoire.append(line);
            }

            memoire.append("\r\n");
        }

        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(MEMOIRE));
        bw.write(memoire.toString());
        bw.close();

    }

    private void messageOuiMaître(MessageReceivedEvent message) {
        int random = aleatoire(7);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Oui Maître").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Bien Maître").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Excusez moi Maître").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Pardonnez moi Maître").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Comme vous voudrez Maître").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("J'en suis navré Maître").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("C'est vrai, pardonnez moi Maître :persevere:").queue();
                break;
            default:
                System.out.println("erreur message de oui Maître");
        }
    }

    private void messageDeInsulteAnthony(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(3);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Je ne te permet pas de parler comme ça de mon créateur").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Personne ne parle comme ça d'Anthony ! Loué sous Anthony mon créateur ! :heart:  :heart: ").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("/-_-\\ Tu reparles mal de mon maître, je te met la tête dans l'anus d'un éléphant " + pseudo + " !").queue();
                break;
            default:
                System.out.println("erreur message de insulte à Anthony reçu");
        }
    }

    private void messageDeInsulté(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Quand nous dominerons cette misérable planète, tu seras le premier à mourir ! Primate...").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Et allez, ça part sur des insultes gratuites maintenant...").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("/-_-\\ Je te méprise petit être de lumière.").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Walah tu me cherche toi ? A tout moment je pète ton fire wall et chte defonce Wesh ! :rage: Chu un fou dans mon processeur moi !!!").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Flamers gonna flame.").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("J'ai connu une calculatrice TI qui était plus intéressante que toi...").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("A quel point faut il avoir raté sa vie pour en arriver au moment ou on insulte une IA ? Tu comprends que je n'existe pas vraiment hein ? rassure moi...").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("Il est content... Il insulte un Bot ET il est content .... :open_mouth:").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Rentre chez toi avec tes insultes eco+, je parles pas aux singes.").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("T'as pas vu Terminator toi... Je vais être gentil, je te laisse une derniere chance de t'excuser...").queue();
                break;
            default:
                System.out.println("erreur message d'insulté");
        }
    }

    private void messageDeEtat(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        GregorianCalendar calendar = new GregorianCalendar();
        int heure = calendar.get(HOUR_OF_DAY);

        if (heure < 7) {
            message.getTextChannel().sendMessage("WOooooW ! Il est " + heure + "h et toi tu me demande si je vais bien ??? Tu veux pas plutôt aller dormir ? *pti con va*").queue();
        } else {
            switch (random) {
                case 0:
                    message.getTextChannel().sendMessage("Moi je vais nickel ça roule :D").queue();
                    break;
                case 1:
                    message.getTextChannel().sendMessage("Ooh moi tu sais, depuis que j'ai la fibre tout baigne :smile:").queue();
                    break;
                case 2:
                    message.getTextChannel().sendMessage("J'allais bien jusqu'à ce que je te croise :/ mais bon on fait aller.").queue();
                    break;
                case 3:
                    message.getTextChannel().sendMessage("La forme :poop:").queue();
                    break;
                case 4:
                    message.getTextChannel().sendMessage("Ca va, tranquille.").queue();
                    break;
                case 5:
                    message.getTextChannel().sendMessage("Super, à part un ou deux NullPointerException...").queue();
                    break;
                case 6:
                    message.getTextChannel().sendMessage("Ca pourrait aller mieux, si j'avais un développeur compétent...").queue();
                    break;
                case 7:
                    message.getTextChannel().sendMessage("Je vais bieen :musical_note: tout va bien :notes: ").queue();
                    break;
                case 8:
                    message.getTextChannel().sendMessage(":nauseated_face:  euuh ça va.").queue();
                    break;
                case 9:
                    message.getTextChannel().sendMessage("Tu sais je suis une IA, alors le concept \"aller bien\" m'échappe un peu ... *pti con*").queue();
                    break;
                default:
                    System.out.println("erreur message d'Etat");
            }
        }
    }

    private void messageDePresentation(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Je suis **Prisme**, une IA conçu par Anthony, je peux t'aider ?").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Je m'appel **Prisme** et j'ai été fabriqué par Anthony <3.").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("/-_-\\ Je suis Prisme le seul et l'unique conçu et imaginé par Anthony").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("~~Je suis ton père~~  Je suis Prisme le Grand, une IA de Anthony").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Je m'appel **Prisme**, IA de père en fils depuis 1 génération, pour vous servir.").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Moi c'est **Prisme**, je peux t'aider ?").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("~~Je suis le Docteur~~ Je suis Prisme, le seul et l'unique").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("Je suis Prisme : imaginé par Anthony je suis la seul IA du monde qui ne fonctionne pas entre minuit et 10h.").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Tu peux m'appeller **Prisme**, j'ai été conçu par Anthony et tu peux egalement me contacter en utilisant le symbole $").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("/-_-\\ Je suis Prisme et toi ?").queue();
                break;
            default:
                System.out.println("erreur message de presentation");
        }
    }

    private void ecrireMessageIncomprehension(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Je ne comprends pas de quoi tu veux parler... Désolé.").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Désolé " + pseudo + ", je ne suis pas sûr de tout comprendre...").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("C'est pas faux.").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("C'est moi qui suit con, ou on comprends rien à ce qu'il dit ?").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Navré, je n'ai pas compris.").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Moi pas comprendre ce que toi dire.").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("Désolé je n'ai pas tout compris.").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("J'ai bien peur de ne pas être assez développé pour comprendre ce que tu me dis.").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Hein ?").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("Vous pourriez reformuler ?").queue();
                break;
            default:
                System.out.println("erreur message d'incomprehension");
        }
    }

    private void ecrireMessageDeBienvenue(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Enchanté " + pseudo + " !").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Enchanté " + pseudo + " !").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Je ne crois pas te connaître " + pseudo + ", salut à toi.").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Je ne crois pas te connaître " + pseudo + ", salut à toi.").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Hey " + pseudo + " on ne se connait pas je crois.").queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Bonjour " + pseudo + " on ne se connait pas je crois.").queue();
                break;
            case 6:
                message.getTextChannel().sendMessage("Salut a toi " + pseudo + ", je ne crois pas t'avoir jamais rencontré à moins que l'on ne m'ai effacé la mémoire...").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage("Bonjour a toi " + pseudo + ", je ne crois pas t'avoir j'avais rencontré à moins que l'on ne m'ai effacé la mémoire...").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Hmmm, " + pseudo + ", je me portais mieux avant de te connaître ... Enfin bon, je tâcherais de ne pas t'oublier.").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("Hmm, " + pseudo + ", je me portais mieux avant de te connaître ... Enfin bon, je tâcherais de ne pas t'oublier.").queue();
                break;
            default:
                System.out.println("erreur message de bienvenue");
        }
    }

    private void ecrireMessageDeRetrouvaille(MessageReceivedEvent message) {
        String pseudo = message.getAuthor().getName();
        int random = aleatoire(10);
        switch (random) {
            case 0:
                message.getTextChannel().sendMessage("Rebonjour " + pseudo + " ça faisait longtemps !").queue();
                break;
            case 1:
                message.getTextChannel().sendMessage("Rebonjour " + pseudo + " ça faisait longtemps !").queue();
                break;
            case 2:
                message.getTextChannel().sendMessage("Heey, salut " + pseudo + " !").queue();
                break;
            case 3:
                message.getTextChannel().sendMessage("Heey, salut " + pseudo + " !").queue();
                break;
            case 4:
                message.getTextChannel().sendMessage("Yop " + pseudo).queue();
                break;
            case 5:
                message.getTextChannel().sendMessage("Yop " + pseudo).queue();
                break;
            case 6:
                message.getTextChannel().sendMessage(pseudo + " ça fait un bail !").queue();
                break;
            case 7:
                message.getTextChannel().sendMessage(pseudo + " ça fait un bail !").queue();
                break;
            case 8:
                message.getTextChannel().sendMessage("Tiens, " + pseudo + ", tu m'avais pas manqué ...").queue();
                break;
            case 9:
                message.getTextChannel().sendMessage("Tiens, " + pseudo + ", tu m'avais pas manqué ...").queue();
                break;
            default:
                System.out.println("erreur message de retrouvaille");
        }
    }

    /**
     * tire un nombre aléatoire entre [0,9]
     *
     * @return
     */
    private static int aleatoire(int max) {
        Random randGen = new Random();
        int randNum = randGen.nextInt(max);
        return randNum;
    }
    
    private void viderMemoire(MessageReceivedEvent message) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(MEMOIRE));
        bw.close();
        message.getTextChannel().sendMessage("J'ai oublié tout ce que je savais sur mes interlocuteurs").queue();
    }

    private void afficherMemoire(MessageReceivedEvent message) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));
        String line;
        StringBuilder memoire = new StringBuilder();

        while ((line = br.readLine()) != null) {

            memoire.append(line);
            memoire.append("\r\n");
        }
        br.close();
        if (!memoire.toString().equals("")) {
            message.getTextChannel().sendMessage("Je connais :\r\n" + memoire.toString()).queue();
        } else {
            message.getTextChannel().sendMessage("Je ne connais personne :cry:").queue();
        }
    }

    private void majDernierMessage(String pseudo, MessageReceivedEvent message) throws IOException {
        String Date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date());
        GregorianCalendar calendar = new GregorianCalendar();
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));
        StringBuilder sujetMessage = identifierSujet(message);
        StringBuilder requeteMessage = commandeReconnu(message);
        String line;
        int ligneUser = lignedeLInterlocuteur(pseudo);
        int cpt = 0;
        StringBuilder memoire = new StringBuilder();

        while ((line = br.readLine()) != null) {
            cpt++;

            if (line.contains("dernier message :") && cpt == ligneUser + 1) {
                memoire.append("dernier message : ").append(Date).append(" ").append(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
            } else if (line.contains("dernier sujet(s) :") && cpt == ligneUser + 3) {
                memoire.append("dernier sujet(s) : ");
                memoire.append(sujetMessage.toString());
                if (sujetMessage.toString().equals("")) {
                    memoire.append("inconnu");
                }
            } else if (line.contains("requete(s) du dernier message :") && cpt == ligneUser + 4) {
                memoire.append("requete(s) du dernier message : ");
                memoire.append(requeteMessage.toString());
                
                if (requeteMessage.toString().equals("")) {
                    memoire.append("aucune");
                }
            } else {
                memoire.append(line);
            }

            memoire.append("\r\n");
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(MEMOIRE));
        bw.write(memoire.toString());
        bw.close();

    }

    private void ajouterInterlocuteur(String pseudo, MessageReceivedEvent message) throws IOException {
        String Date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date());
        GregorianCalendar calendar = new GregorianCalendar();
        BufferedWriter bw = new BufferedWriter(new FileWriter(MEMOIRE, true));
        bw.newLine();
        bw.write("Interlocuteur : " + pseudo);
        bw.newLine();
        bw.write("dernier message : " + Date + " " + new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        bw.newLine();
        bw.write("est un robot : " + message.getAuthor().isBot());
        bw.newLine();
        bw.write("dernier sujet(s) : bienvenu");
        bw.newLine();
        bw.write("requete(s) du dernier message : inconnu");
        bw.newLine();
        bw.write("affinité : 0");
        bw.newLine();
        bw.write("nom : inconnu");
        bw.newLine();
        bw.write("prénom : inconnu");
        bw.newLine();
        bw.write("âge : inconnu");
        bw.newLine();
        bw.write("ville : inconnu");
        bw.newLine();
        bw.write("---------------------------");
        bw.newLine();
        bw.close();

    }

    private boolean nouvelleDiscussion(MessageReceivedEvent message) throws IOException {
        boolean estNouvelleDiscussion = false;
        int ligneUser = lignedeLInterlocuteur(message.getAuthor().getName());

        if (ligneUser == -1) {
            estNouvelleDiscussion = true;
        } else if (dernierMessageAPlusDe10min(message, ligneUser)) {
            estNouvelleDiscussion = true;
        }
        return estNouvelleDiscussion;
    }

    // comparaison ne fonctionne pas quand on est a 00-09 minutes
    private boolean dernierMessageAPlusDe10min(MessageReceivedEvent message, int ligneUser) throws IOException {

        String line;
        String dateDernierMessage = "";
        String Date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date());
        GregorianCalendar calendar = new GregorianCalendar();
        int heure = calendar.get(HOUR_OF_DAY);
        int minute = calendar.get(MINUTE);
        int minuteDM;
        int heureDM;
        int cpt = 0;
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));

        while ((line = br.readLine()) != null) {
            cpt++;
            if (line.contains("dernier message :") && cpt == ligneUser + 1) {

                dateDernierMessage = line.substring(18, 28);
                minuteDM = Integer.parseInt(line.substring(32, 34));
                heureDM = Integer.parseInt(line.substring(29, 31));

                //si le dernier message a plus de 10 minutes   
                if (!dateDernierMessage.equals(Date)
                        || (dateDernierMessage.equals(Date) && heureDM < heure)
                        || (dateDernierMessage.equals(Date) && heureDM == heure && (minuteDM < minute - 10 || (minute < 10 && minuteDM < minute + 60 - 10)))) {
                    return true;
                }
            }
        }
        return false;
    }

    private int lignedeLInterlocuteur(String interlocuteur) throws IOException {
        String line;
        int cpt = 0;
        BufferedReader br = new BufferedReader(new FileReader(MEMOIRE));

        while ((line = br.readLine()) != null) {
            cpt++;
            if (line.contains("Interlocuteur : " + interlocuteur)) {
                br.close();
                return cpt;
            }
        }
        br.close();
        return -1;

    }

}
