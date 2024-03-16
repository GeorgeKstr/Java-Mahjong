

package mahjong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class Gui extends JFrame {
    private static final String gamerules = "Για να αφαιρέσει ο παίκτης ένα ζευγάρι πλακιδίων θα πρέπει να ισχύουν οι ακόλουθες\n" +
"συνθήκες:\n" +
"▪ Αν τα πλακίδια του ζευγαριού προέρχονται από τις ομάδες χαρακτήρων, κύκλων και\n" +
"μπαμπού, τότε θα πρέπει να είναι πανομοιότυπα.\n" +
"▪ Ειδικά για τα πλακίδια εποχών και λουλουδιών, ο παίκτης μπορεί να συνδυάσει\n" +
"οποιαδήποτε πλακίδια από την ίδια κατηγορία.\n" +
"▪ Κάθε πλακίδιο του ζευγαριού θα πρέπει να είναι ελεύθερο. Δηλαδή να μην υπάρχει\n" +
"άλλο γειτονικό πλακίδιο που να βρίσκεται στα αριστερά ή στα δεξιά του.";
    Game game;
    int stage;
    String name;
    boolean ingame;
    int difficulty;
    JPanel p;
    JLabel tiles_left;
    JLabel score;
    JPanel buttons;
    JButton rearrange;
    JButton suggest;
    JButton viewfree;
    JButton remove;
    JButton undo;
    private int re_times;
    private int suggest_times;
    private int free_times;
    private int remove_times;
    private int undo_times;
    class GamePanel extends JPanel {
        GamePanel(){
            addMouseListener(new MouseAdapter() {
             @Override
             public void mousePressed(MouseEvent e) {
                 handleClick(e.getX(), e.getY());
             }
         });
        }       
        private void handleClick(int x, int y){
            ArrayList<Tiles> board = game.getDash();
            int xpos, ypos;
            float xmin = game.getMinX();
            float ymin = game.getMinY();
            int xsize = (int) (this.getWidth()/(game.getMaxX()-xmin));
            
            int ysize = (int) (this.getHeight()/(game.getMaxY()-ymin));
            for(Tiles t:board){
                xpos = (int) ((t.getX()-xmin)*xsize);
                ypos = (int) ((t.getY()-ymin)*ysize);
                if(x>=xpos && x<=xpos+xsize && y>=ypos && y<=ypos+ysize){
                    
                    game.Select(t);
                    repaint();
                    return;
                }
                    
            }
        }
        private int getTilePicture(Tiles t){            
            int number;
            if(t instanceof CharacterTiles){
                CharacterTiles ct = (CharacterTiles)t;
                number = ct.getCh().ordinal();
            }
            else if(t instanceof BabouTiles){
                BabouTiles bt = (BabouTiles)t;
                number = 9+bt.getBa().ordinal();
            }
            else if(t instanceof CircleTiles){
                CircleTiles ct = (CircleTiles)t;
                number = 18+ct.getCi().ordinal();
            }
            else if(t instanceof SeasonTiles){
                SeasonTiles st = (SeasonTiles)t;
                number = 27+st.getSe().ordinal();
            }
            else {
                FlowerTiles ft = (FlowerTiles)t;
                number = 31+ft.getFl().ordinal();
            }
            return number;
        }
        @Override
        public void paintComponent(Graphics g){     
            super.paintComponent(g);
            if(!ingame)                             
                return;
            ArrayList<Tiles> board = game.getDash();        
            tiles_left.setText("Tiles left: "+board.size());    
            score.setText("Score: "+game.getScore());
            rearrange.setText("Rearrange ("+re_times+')');
            if(re_times<=0)
                rearrange.setEnabled(false);                    
            suggest.setText("Suggest a pair ("+suggest_times+')');
            if(suggest_times<=0)
                suggest.setEnabled(false);                      
            viewfree.setText("View selectable pairs ("+free_times+')');
            if(free_times<=0)
                viewfree.setEnabled(false);
            remove.setText("Remove a pair ("+remove_times+')');
            if(remove_times<=0)
                remove.setEnabled(false);
            undo.setText("Undo action ("+undo_times+')');
            if(undo_times<=0)
                undo.setEnabled(false);
            if(board.size()==0)                             
            {
                endGame();
                return;
            }
            boolean free[]=game.freeTiles();                
            boolean finished = true;
            for(boolean b:free){
                if(b==true){
                    finished=false;
                    break;
                }
            }
            if(finished){
                endGame();
                return;
            }
            BufferedImage imgs[]=new BufferedImage[35];     
            int i;
            try{
                for(i=1; i<10; i++)
                    imgs[i-1]=ImageIO.read(getClass().getResource("Images/CharacterTiles"+i+".png"));
                for(i=1; i<10; i++)
                    imgs[i+8]=ImageIO.read(getClass().getResource("Images/BabouTiles"+i+".png"));
                for(i=1; i<10; i++)
                    imgs[i+17]=ImageIO.read(getClass().getResource("Images/CircleTiles"+i+".png"));
                for(i=1; i<5; i++)
                    imgs[i+26]=ImageIO.read(getClass().getResource("Images/SeasonTiles"+i+".png"));
                for(i=1; i<5; i++)
                    imgs[i+30]=ImageIO.read(getClass().getResource("Images/FlowerTiles"+i+".png"));
            }catch(Exception e){
                e.printStackTrace();
            }
            free=game.getIndicated();
            
            Graphics2D g2 = (Graphics2D)g;
            float xmin = game.getMinX();
            float ymin = game.getMinY();
            int xsize = (int) (this.getWidth()/(game.getMaxX()-xmin));      
            
            int ysize = (int) (this.getHeight()/(game.getMaxY()-ymin));
            int xpos, ypos;
            g2.setPaint(new Color(0, 0, 255));                                  
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());               
            for(i=0;i<board.size(); i++){
                Tiles t = board.get(i);
                xpos = (int) ((t.getX()-xmin)*xsize);
                ypos = (int) ((t.getY()-ymin)*ysize);
               
                if(t==game.getSelected())                                       
                    g2.setPaint(new Color(200, 0, 0));                          
                else if(free!=null && free[i]==true)
                    g2.setPaint(new Color(0, 220, 0));
                else
                    g2.setPaint(new Color(0, 0, 0));                            
                g2.fillRect(xpos, ypos, xsize, ysize);
                g2.drawImage(imgs[getTilePicture(t)], xpos+3, ypos+3, xsize-6, ysize-6, this);  
            }
             
            
        }
        
    }
    private void addMenus(){            
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem newg = new JMenuItem("New game");
        newg.addActionListener((ActionEvent ev) -> {
            newGame();
        });
        JMenuItem exitg = new JMenuItem("Exit");
        exitg.addActionListener((ActionEvent ev) -> {
            quitGame();
        });
        file.add(newg);
        file.add(exitg);
        
        JMenu about = new JMenu("About");
        JMenuItem aboutus = new JMenuItem("About us");
        aboutus.addActionListener((ActionEvent ev) -> {
            JOptionPane.showMessageDialog(this, "Group data:\nΘωμάς Δάσιος, 321/2016032\nΘοδωρής Λεβογιάννης, 321/2011082", "About us", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JMenuItem rules = new JMenuItem("Rules");
        rules.addActionListener((ActionEvent ev) -> {
            JOptionPane.showMessageDialog(this, gamerules, "Rules", JOptionPane.DEFAULT_OPTION);
        });
        
        menu.add(file);
        menu.add(about);
        
        about.add(aboutus);
        about.add(rules);
        setJMenuBar(menu);
        p = new GamePanel();
        p.setPreferredSize(new Dimension(900, 800));
        
        
        buttons = new JPanel();
        score = new JLabel();
        tiles_left = new JLabel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setPreferredSize(new Dimension(200, 400));
        buttons.add(tiles_left);
        buttons.add(score);
        rearrange=new JButton();
        rearrange.addActionListener((ActionEvent ev) -> {           
            if(re_times>0){
                re_times--;
                game.rearrangement();
                p.repaint();
                JOptionPane.showMessageDialog(this, "You have "+re_times+" rearrangements left.", "Rules", JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showMessageDialog(this, "You have no rearrangements left.", "Rules", JOptionPane.DEFAULT_OPTION);
        });
        suggest=new JButton();
        suggest.addActionListener((ActionEvent ev) -> {
            if(suggest_times>0){
                suggest_times--;
                game.suggestPair();
                p.repaint();
                JOptionPane.showMessageDialog(this, "You have "+suggest_times+" suggestions left.", "Rules", JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showMessageDialog(this, "You have no suggestions left.", "Rules", JOptionPane.DEFAULT_OPTION);
        });
        viewfree = new JButton();
        viewfree.addActionListener((ActionEvent ev) -> {
            if(free_times>0){
                free_times--;
                game.indicate();
                p.repaint();
                JOptionPane.showMessageDialog(this, "You have "+free_times+" views left.", "Rules", JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showMessageDialog(this, "You have no views left.", "Rules", JOptionPane.DEFAULT_OPTION);
        });
        remove = new JButton();
        remove.addActionListener((ActionEvent ev) -> {
            if(remove_times>0){
                remove_times--;
                game.removePair();
                p.repaint();
                JOptionPane.showMessageDialog(this, "You have "+remove_times+" removals left.", "Rules", JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showMessageDialog(this, "You have no removals left.", "Rules", JOptionPane.DEFAULT_OPTION);
        });
        undo=new JButton();
        undo.addActionListener((ActionEvent ev) -> {
            if(undo_times>0){
                if(game.undo()==false)
                    return;
                undo_times--;
                p.repaint();
                JOptionPane.showMessageDialog(this, "You have "+undo_times+" undos left.", "Rules", JOptionPane.DEFAULT_OPTION);
            }
            else
                JOptionPane.showMessageDialog(this, "You have no undos left.", "Rules", JOptionPane.DEFAULT_OPTION);
        });
        
        setLayout(new FlowLayout());
        add(p);
        add(buttons);
    }
    private void endGame(){     
        try {
        
            PrintWriter out = new PrintWriter(name+".txt");
            out.println(score.getText());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        int n = JOptionPane.showConfirmDialog(
                this,
                "There are no other pairs to match.\nYour score was saved in '"+name+".txt'.\nWould you like to play again ?",
                "Game Over",
                JOptionPane.YES_NO_OPTION);
        if(n==JOptionPane.YES_OPTION)
            newGame();
        else
            System.exit(0);
    }
    private void quitGame(){        
        int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit ?",
                "Quit",
                JOptionPane.YES_NO_OPTION);
        if(n==JOptionPane.YES_OPTION)
            System.exit(0);
    }
    private void newGame(){             
        JPanel opts = new JPanel();
        opts.setLayout(new BoxLayout(opts, BoxLayout.Y_AXIS));
        JTextField tname = new JTextField();
        Object[] difficulties = {"easy", "medium", "hard"};
        JComboBox tdifficulty = new JComboBox(difficulties);
        Object[] stages = {"Default", "Vertical", "Cross"};
        JComboBox tstage = new JComboBox(stages);
        opts.add(new JLabel("Name:"));
        opts.add(tname);
        opts.add(new JLabel("Difficulty:"));
        opts.add(tdifficulty);
        opts.add(new JLabel("Stage:"));
        opts.add(tstage);
        int result = JOptionPane.showConfirmDialog(null,opts, 
               "Please Enter Game Options", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            name = tname.getText();
            
            setTitle("Mahjong - "+name);        
            difficulty = tdifficulty.getSelectedIndex();
            stage = tstage.getSelectedIndex();
            game.Dash(stage);
            game.rearrangement();
            p.setPreferredSize(new Dimension((int)(game.getMaxX()-game.getMinX())*40, (int)(game.getMaxY()-game.getMinY())*60));
        
            re_times=4-2*difficulty;
            suggest_times=4-2*difficulty;
            free_times=4-2*difficulty;
            remove_times=4-2*difficulty;
            undo_times=4-2*difficulty;
            ingame=true;
            buttons.add(rearrange);     
            buttons.add(suggest);
            buttons.add(viewfree);
            buttons.add(remove);
            buttons.add(undo);
            p.repaint();
            pack();
        }
    }
    public Gui(String title){           
        super(title);
        ingame=false;
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        game = new Game();
        addMenus();
        pack();
        super.setVisible(true);
    }
}
