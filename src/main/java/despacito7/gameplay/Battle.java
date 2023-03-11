package despacito7.gameplay;

import despacito7.App;
import despacito7.Constants;
import despacito7.FeatureLoader;
import despacito7.detail.*;
import despacito7.menu.Menu;

import java.awt.Graphics2D;
import java.awt.Color;

public class Battle {
    private Monster currentMonster;
    private Monster playerMonster;
    private NPC currentNPC;

    private enum BattleStates {
        ENTER, YOURTURN, ENEMYTURN, 
        SELECTITEM, SELECTMONSTER, SELECTMOVE, END
    }

    private BattleStates currentState = BattleStates.YOURTURN;

    public Battle(NPC npc){
        currentNPC = npc;
        currentMonster = currentNPC.getMonster(0);
        currentMonster.setCoord(20, 10);
        playerMonster = FeatureLoader.player.getMonster(0);
        playerMonster.setCoord(10, 20);
        createMenu();
    }

    public Battle(Monster monster){
        currentMonster = monster;
        currentMonster.setCoord(20, 10);
        playerMonster = FeatureLoader.player.getMonster(0);
        playerMonster.setCoord(10, 20);
        playerMonster.getStat(Constants.Stat.HEALTH);
        /* System.out.println("Playermonster moves:" + playerMonster.getMoves() + " health: " + playerMonster.getStat(Constants.Stat.HEALTH));
        playerMonster.updateStat(Constants.Stat.HEALTH, -5);
        playerMonster.learnMove(FeatureLoader.getMove("punch"),playerMonster.getMoves().get(3));
        System.out.println("Playermonster moves:" + playerMonster.getMoves() + " health: " + playerMonster.getStat(Constants.Stat.HEALTH)); */
        createMenu();
    }

    public void createMenu(){
        int menuX = 300;
        int menuY = 300;
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY, 100, 20, "Attack", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.SELECTMOVE;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY+22, 100, 20, "Pick Item", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.SELECTITEM;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY+44, 100, 20, "Switch Monster", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.SELECTMONSTER;
            }
        }));
        Menu.battleMenu.addButton(Menu.generateButton(menuX, menuY+66, 100, 20, "Run Away", new Menu.ButtonCallback(){
            public void activate(){
                currentState = BattleStates.END;
            }
        }));
        for(Move m : playerMonster.getMoves()){
            Menu.moveMenu.addButton(Menu.generateButton(menuX, menuY, 100, 20, m.id(), new Menu.ButtonCallback(){
                public void activate(){
                    
                }
            }));
        }
        // for(Monster m : FeatureLoader.player.getMonsters()){
        //     Move.moveMenu.addButton(Menu.generateButton(menuX, menuY, 100, 20, , new Menu.ButtonCallback(){
        //         public void activate(){

        //         }
        //     }))
        // }
        for(Item i : FeatureLoader.player.getItemList()){
            Menu.itemMenu.addButton(Menu.generateButton(menuX, menuY, 100, 20, null,  new Menu.ButtonCallback() {
                public void activate(){
                    
                }
            }));
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
        Menu.battleMenu.tick();
        switch(currentState){
            case ENTER:
            break;
            case YOURTURN:
            break;
            case ENEMYTURN:
            break;
            case SELECTITEM:
                System.out.println("pick item works");
            break;
            case SELECTMONSTER: 
                System.out.println("switch monster works");
            break;
            case SELECTMOVE: 
                System.out.println("attack works");
            break;
            case END:
                System.out.println("run away works");
            break;
       } 
    }
}
