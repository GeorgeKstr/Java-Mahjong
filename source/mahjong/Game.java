

package mahjong;

import java.util.ArrayList;

public class Game implements GameInterface { 

    
    private ArrayList<Tiles> dash;
    private int score;
    private ArrayList<Tiles> deleted;
    private boolean[] indicated; 
    private Tiles selected;
    @Override
    public Tiles getSelected(){
        return this.selected;
    }
    public Game() {

        
        
        

    }
    @Override
    public void Dash(int stage) { 
        dash = new ArrayList<>();
        selected = null;
        indicated = null;
        score = 0;
        deleted = new ArrayList<>();
        int i;
        if(stage==0){
            for(i=0;i<9;i++){
                add(1, i, 2+i, 0);
                add(1, i, 2+i, 1);
                add(1, i, 2+i, 11);
                add(1, i, 2+i, 12);
                add(2, i, 1, 2+i);
                add(2, i, 2, 2+i);
                add(2, i, 10, 2+i);
                add(2, i, 11, 2+i);
                add(3, i, 4, 2+i);
                add(3, i, 5, 2+i);
                add(3, i, 7, 2+i);
                add(3, i, 8, 2+i);
            }
            for(i=0;i<4;i++){
                add(4, i, 0, (float) (2.5+i*2));
                add(5, i, 12, (float) (3.5+i*2));
            }
        }
        else if(stage==1){
            for(i=0;i<9;i++){
                add(1, i, 0, i);
                add(1, i, 1, (float) (i+0.5));
                add(1, i, 2, i);
                add(1, i, 6, i);
                add(2, i, 7, (float) (i+0.5));
                add(2, i, 8, i);
                add(2, i, 10, i);
                add(2, i, 11, (float) (i+0.5));
                add(3, i, 12, i);
                add(3, i, 16, i);
                add(3, i, 17, (float) (i+0.5));
                add(3, i, 18, i);
            }
            for(i=0;i<4;i++){
                add(4, i, 4, (float) 0.5+i*2);
                add(5, i, 14, (float) 1.5+i*2);
            }
        }
        else{
            for(i=0;i<9;i++){
                add(1, i, i, 2);
                add(1, i, i, 3);
                add(1, i, i, 5);
                add(1, i, i, 6);
                add(2, i, 9, i);
                add(2, i, 10, i);
                add(2, i, 12, i);
                add(2, i, 13, i);
                add(3, i, i+14, 2);
                add(3, i, i+14, 3);
                add(3, i, i+14, 5);
                add(3, i, i+14, 6);
            }
            for(i=0;i<4;i++){
                add(4, i, i*2, 1);
                add(5, i, 15+i*2, 7);
            }
        }
    }
    @Override
    public int getScore(){
        return score;
    }
    @Override
    public boolean Select(Tiles tile) {  
        float x=tile.getX();
        float y=tile.getY();
        if(isFree(tile)==false)
        {
            selected = null;
            return false;
        }
        if (selected==null) { 
            selected=tile;
            return true;
        }
        if(tile==selected){
            selected=null;
            return true;
        }
        if (isFree(tile) && compareTiles(selected, tile)) { 
            
            if(tile instanceof BabouTiles || tile instanceof CharacterTiles || tile instanceof CircleTiles)
                score+=20;
            else
                score+=10;
            
            deleted.add(selected);
            deleted.add(tile);
            dash.remove(selected);
            dash.remove(tile);
            indicated = null;
            selected=null;
            return true;
        } else { 
            selected = null;
            return false;
        }

    }
    @Override
    public boolean undo() { 
        if(deleted.isEmpty())
            return false;
        dash.add(deleted.remove(deleted.size()-1));
        dash.add(deleted.remove(deleted.size()-1));
        selected = null; 
        indicated = null;
        return true;
    }
    @Override
    public void rearrangement() { 
       float dash_x[] = new float[dash.size()];
       float dash_y[] = new float[dash.size()];
       int i;
       for(i=0; i<dash.size(); i++)
       {
           dash_x[i]=-1;
           dash_y[i]=-1;
       }
       for(Tiles t:dash){
           do
               i=(int) (Math.random()*dash.size());
           while(dash_x[i]!=-1);
           dash_x[i]=t.getX();
           dash_y[i]=t.getY();
       }
       for(i=0;i<dash.size();i++)
       {
           dash.get(i).setX(dash_x[i]);
           dash.get(i).setY(dash_y[i]);
       }
       indicated = null;
    }
    @Override
    public boolean suggestPair(){  
        int i,j;
        for(i=0;i<dash.size();i++){ 
            Tiles t1=dash.get(i);
            if(isFree(t1)){         
                for(j=i+1;j<dash.size();j++){   
                    Tiles t2=dash.get(j);
                    if(isFree(t2) && compareTiles(t1,t2))
                    {
                        indicated=new boolean[dash.size()];     
                        for(boolean b:indicated)
                            b=false;
                        indicated[i]=indicated[j]=true;         
                        return true;
                    }
                }
            }
        }
        return false;                                           
    }
    @Override
    public boolean removePair(){                               
        for(Tiles t1:dash){
            if(isFree(t1)){
                for(Tiles t2:dash){
                    if(t1!=t2 && isFree(t2) && compareTiles(t1,t2))
                    {
                        indicated = null;
                        dash.remove(t1);
                        dash.remove(t2);
                        indicated = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public boolean[] freeTiles(){  
        int i,j;
        boolean result[]=new boolean[dash.size()];
        for(i=0;i<dash.size();i++){ 
            result[i]=false;
            Tiles t1=dash.get(i);
            if(isFree(t1)){         
                for(j=i+1;j<dash.size();j++){   
                    Tiles t2=dash.get(j);
                    if(isFree(t2) && compareTiles(t1,t2))
                        result[i]=true;
                }
            }
        }
        return result;
    }
    @Override
    public ArrayList<Tiles> getDash() {
        return dash;
    }
    @Override
    public boolean[] getIndicated() {
        return indicated;
    }
    @Override
    public void indicate(){
        this.indicated = freeTiles();
    }
    @Override
    public float getMaxX(){
        float max=0;
        for(Tiles t:dash){
            if(t.getX()>max)
                max=t.getX();
        }
        return max+1;
    }
    @Override
    public float getMaxY(){
        float max=0;
        for(Tiles t:dash){
            if(t.getY()>max)
                max=t.getY();
        }
        return max+1;
    }
    @Override
    public float getMinX(){
       float min=getMaxX();
       for(Tiles t:dash){
           if(t.getX()<min)
               min=t.getX();
       }
       return min;
   }
    @Override
    public float getMinY(){
        float min=getMaxY();
        for(Tiles t:dash){
            if(t.getY()<min)
                min=t.getY();
        }
        return min;
    }
   
    private boolean isFree(Tiles tile){ 
        float x=tile.getX();
        float y=tile.getY();
        if(x==getMinX() || x==getMaxX()-1)
            return false;
        boolean left=false;             
        boolean right=false;
        for(Tiles t:dash){              
            if(!left && t.getX()==x-1){
                if(t.getY()>=y-0.5 && t.getY()<=y+0.5)  
                    left=true;                          
            }
            if(!right && t.getX()==x+1){
                if(t.getY()>=y-0.5 && t.getY()<=y+0.5)  
                    right=true;
            }
            if(left&&right)
                return false;                           
        }
        return true;                                    
    }
    private static void print(String s) {
        System.out.println(s);
    }
    private void add(int type, int num, float x, float y){
        Tiles t;
        if(type==1)
            t = new CharacterTiles(NumberEnum.values()[num]);
        else if(type==2)
            t = new CircleTiles(NumberEnum.values()[num]);
        else if(type==3)
            t = new BabouTiles(NumberEnum.values()[num]);
        else if(type==4)
            t = new SeasonTiles(LoipaEnum.values()[num]);
        else
            t = new FlowerTiles(LoipaEnum.values()[num]);
        t.setX(x);
        t.setY(y);
        dash.add(t);
    }
    private boolean compareTiles(Tiles t1, Tiles t2) { 
        
        if (t1.getClass() == t2.getClass()) { 
            if (t1 instanceof CharacterTiles) { 
                if (((CharacterTiles) t1).getCh() == ((CharacterTiles) t2).getCh()) { 
                    return true; 
                }
            } else if (t1 instanceof CircleTiles) { 
                if (((CircleTiles) t1).getCi() == ((CircleTiles) t2).getCi()) { 
                    return true; 
                }
            } else if (t1 instanceof BabouTiles) { 
                if (((BabouTiles) t1).getBa() == ((BabouTiles) t2).getBa()) { 
                    return true; 
                }
            } else if (t1 instanceof SeasonTiles) { 
                return true; 
            } else if (t1 instanceof FlowerTiles) { 
                return true; 
            }
        }
        
        return false;
    }
}
