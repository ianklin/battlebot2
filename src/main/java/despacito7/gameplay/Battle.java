package despacito7.gameplay;

import despacito7.App;
import despacito7.Constants;
import despacito7.FeatureLoader;
import despacito7.Constants.GameState;
import despacito7.Constants.Stat;
import despacito7.detail.*;
import despacito7.menu.Menu;
import despacito7.menu.Menu.BattleButton;

import java.awt.Graphics2D;
import java.awt.Taskbar.Feature;
import java.util.Random;
import java.awt.Color;

public class Battle {
    private Monster currentMonster;
    private Monster playerMonster;
    private NPC currentNPC;
    private int moveindex;
    private int monsterindex;
    private int itemindex;

    private enum BattleStates {
        ENTER, YOURTURN, ENEMYTURN, POSTPLAYER, POSTENEMY,
        SELECTITEM, SELECTMONSTER, SELECTMOVE, END
    }

    private BattleStates currentState = BattleStates.YOURTURN;

    public Battle(NPC npc){
        currentNPC = npc;
        currentMonster = currentNPC.getMonster(0);
        currentMonster.setCoord(10, 20);
        playerMonster = FeatureLoader.player.getMonster(0);
        playerMonster.setCoord(10, 10);
        createMenu();
    }

    public Battle(Monster monster){
        currentMonster = monster;
        currentMonster.setCoord(10, 20);
        playerMonster = FeatureLoader.player.getMonster(0);
        playerMonster.setCoord(10, 10);
        //!!!!!!!!!!!!!!!ALERT!!!!!!!!!!!!!!!!!!!!!!
        //something very wrong here with this function
        playerMonster.getStat(Constants.Stat.HEALTH);

        /* System.out.println("Playermonster moves:" + playerMonster.getMoves() + " health: " + playerMonster.getStat(Constants.Stat.HEALTH));
        playerMonster.updateStat(Constants.Stat.HEALTH, -5);
        playerMonster.learnMove(FeatureLoader.getMove("punch"),playerMonster.getMoves().get(3));
        System.out.println("Playermonster moves:" + playerMonster.getMoves() + " health: " + playerMonster.getStat(Constants.Stat.HEALTH)); */
        createMenu();
    }

    public void createMenu(){
        int menuX = 300;
        int menuY = 266;
        Menu.battleMenu.resetButtons();
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY-66, 100, 20, "Attack", new Menu.ButtonCallback(){
            public void activate(){
                createMoveMenu();
                currentState = BattleStates.SELECTMOVE;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY-44, 100, 20, "Pick Item", new Menu.ButtonCallback(){
            public void activate(){
                createItemMenu();
                currentState = BattleStates.SELECTITEM;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY-22, 100, 20, "Switch Monster", new Menu.ButtonCallback(){
            public void activate(){
                createMonsterMenu();
                currentState = BattleStates.SELECTMONSTER;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY, 100, 20, "Run Away", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.END;
            }
        }));
    }

    public void createMoveMenu(){
        int menuX = 300;
        int menuY = 266;
        moveindex = 0;
        Menu.moveMenu.resetButtons();
        for(Move m : playerMonster.getMoves()){
            Menu.moveMenu.addButton(Menu.generateButton(menuX, menuY-(moveindex*22), 100, 20, m.getId(), new Menu.ButtonCallback(){
                public void activate(){
                    if(m.getTarget().equals("self")){
                        playerMonster.updateStatChange(m);
                        currentState = BattleStates.POSTPLAYER;
                    } else if(m.getTarget().equals("enemy")){
                        currentMonster.updateStatChange(m);
                        currentState = BattleStates.POSTPLAYER;
                    }
                    // playerMonster.getMoves().get(buttonNum);
                    
                }
            }));
            moveindex++;
        }
        Menu.moveMenu.addButton(Menu.generateButton(menuX, menuY-(moveindex*22), 100, 20, "Return", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.YOURTURN;
            }
        }));
    }

    public void createMonsterMenu(){
        int menuX = 300;
        int menuY = 266;
        monsterindex = 0;
        Menu.monsterMenu.resetButtons();
        for(String m : FeatureLoader.player.getMonsterNames()){
            BattleButton monsterButton = Menu.generateButton(menuX, menuY-(monsterindex*22), 100, 20, m, new Menu.ButtonCallback(){
                int buttonNum = monsterindex;
                public void activate(){
                    // System.out.println(buttonNum);
                    if(FeatureLoader.player.getMonster(buttonNum).getStat(Stat.HEALTH) > 0.0){
                        playerMonster = FeatureLoader.player.getMonster(buttonNum);
                        playerMonster.setCoord(10, 10);
                        currentState = BattleStates.POSTPLAYER;
                    }
                    // System.out.println("getting monster works");
                    // System.out.println(FeatureLoader.player.getMonster(buttonNum).getName());
                    // System.out.println("get name function works");
                }
            });
            Menu.monsterMenu.addButton(monsterButton);
            if(FeatureLoader.player.getMonster(monsterindex).getStat(Stat.HEALTH) <= 0.0){monsterButton.disable();}
            monsterindex++;
        }
        Menu.monsterMenu.addButton(Menu.generateButton(menuX, menuY-(monsterindex*22), 100, 20, "Return", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.YOURTURN;
            }
        }));
    }

    public void createItemMenu(){
        int menuX = 300;
        int menuY = 266;
        itemindex = 0;
        Menu.itemMenu.resetButtons();
        for(Item i : FeatureLoader.player.getItemList()){
            if (i != null) {
                Menu.itemMenu.addButton(Menu.generateButton(menuX, menuY-(itemindex*22), 100, 20, i.id, new Menu.ButtonCallback() {
                    public void activate(){
                        if(i.getTarget().equals("self")){
                            playerMonster.updateStatChange(i);
                            System.out.println(i.id);
                        } else {
                            // currentMonster.updateStatChange(FeatureLoader.player.getItemList()[buttonNum]);
                            // under assumption that the only "enemy" item is the pokeball
                            FeatureLoader.player.addMonster(currentMonster);
                            currentState = BattleStates.END;
                        }
                        if(i.id.equals("PotionHealth") && playerMonster.getStat(Stat.HEALTH) == playerMonster.getStat(Stat.MAX_HEALTH)){
                            FeatureLoader.player.setItemCount(i, FeatureLoader.player.getItemCount(i)+1);
                        }
                        FeatureLoader.player.setItemCount(i, FeatureLoader.player.getItemCount(i)-1);
                        if(currentState.equals(BattleStates.SELECTITEM)){
                            currentState = BattleStates.POSTPLAYER;
                        }
                    }
                }));
                itemindex++;
            }
        }
        Menu.itemMenu.addButton(Menu.generateButton(menuX, menuY-(itemindex*22), 100, 20, "Return",  new Menu.ButtonCallback() {
            public void activate(){
                currentState = BattleStates.YOURTURN;
            }
        }));
    }

    public void enemyAttack(){
        Random rand = new Random();
        // float randint = rand.nextFloat()*currentMonster.getStat(Stat.MAX_HEALTH);
        // if(randint <= (currentMonster.getStat(Stat.MAX_HEALTH)-currentMonster.getStat(Stat.HEALTH))/2f){
        //     //heal
        //     System.out.println("heal");
        // } else if (randint <= (currentMonster.getStat(Stat.MAX_HEALTH)-currentMonster.getStat(Stat.HEALTH))/2f + currentMonster.getStat(Stat.MAX_HEALTH)/4f){
        //     //buff
        //     System.out.println("buff");
        // } else if (randint <= (currentMonster.getStat(Stat.MAX_HEALTH)-currentMonster.getStat(Stat.HEALTH))/2f + currentMonster.getStat(Stat.MAX_HEALTH)/2f){
        //     //debuff
        //     System.out.println("debuff");
        // } else {
        //     //fight
        //     System.out.println("fight");
        // }
        int randint = rand.nextInt(0, 4);
        Move m = currentMonster.getMoves().get(randint);
        System.out.println("enemy is using " + m.getId());
        if(m.getTarget().equals("self")){
            currentMonster.updateStatChange(m);
        } else if(m.getTarget().equals("enemy")){
            playerMonster.updateStatChange(m);
        }
        currentState = BattleStates.POSTENEMY;
    }

    public void checkHP(){
        if(currentMonster.getStat(Stat.HEALTH) <= 0.0){
            System.out.println("player wins");
            playerMonster.changeExp(50);
            currentState = BattleStates.END;
        } else if (playerMonster.getStat(Stat.HEALTH) <= 0.0){
            System.out.println("player monster dies");
            createMonsterMenu();
            currentState = BattleStates.SELECTMONSTER;
            System.out.println(currentState);
        }
    }

    public void draw(Graphics2D g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, App.width*Constants.tilesize, App.height*Constants.tilesize);
        playerMonster.draw(g);
        if(currentNPC != null) {
            currentNPC.draw(g);
        }
        currentMonster.draw(g);
        switch(currentState){
            case ENTER:
            break;
            case YOURTURN:
                Menu.battleMenu.draw(g);
            break;
            case ENEMYTURN:
            break;
            case POSTPLAYER:
            break;
            case POSTENEMY:
            break;
            case SELECTITEM:
                Menu.itemMenu.draw(g);
            break;
            case SELECTMONSTER: 
                Menu.monsterMenu.draw(g);
            break;
            case SELECTMOVE: 
                Menu.moveMenu.draw(g);
            break;
            case END:
            break;
        }
    }
    
    public void tick(){
        switch(currentState){
            case ENTER:
            break;
            case YOURTURN:
                Menu.battleMenu.tick();
            break;
            case ENEMYTURN:
                System.out.println("postenemy");
                enemyAttack();
            break;
            case POSTPLAYER:
                System.out.println("postplayer");
                currentState = BattleStates.ENEMYTURN;
                checkHP();
            break;
            case POSTENEMY:
                currentState = BattleStates.YOURTURN;
                createMenu();
                checkHP();
            break;
            case SELECTITEM:
                // System.out.println("pick item works");
                Menu.itemMenu.tick();
            break;
            case SELECTMONSTER: 
                // System.out.println("switch monster works");
                Menu.monsterMenu.tick();
            break;
            case SELECTMOVE: 
                // System.out.println("attack works");
                Menu.moveMenu.tick();
            break;
            case END:
                System.out.println("run away works");
                App.currentGameState = GameState.WORLD;
            break;
       } 
    }
}
