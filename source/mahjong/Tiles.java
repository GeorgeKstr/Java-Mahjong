

package mahjong;

enum NumberEnum {
        num1, num2, num3, num4, num5, num6, num7, num8, num9; 

    }

enum LoipaEnum{
    lnum1, lnum2, lnum3, lnum4; 
}

public class Tiles {

    private float x,y;
 
    public Tiles(){
  
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    
}
