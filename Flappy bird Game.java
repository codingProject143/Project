import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

class Bird extends JPanel implements ActionListener,KeyListener{
    //screen size 1540x800
    static int frameWidth=1000; 
    static int frameHeight=600;

    //decalring images(creating varables)
    Image Bgimg;
    Image Parrotimg;
    Image Toppipeimg;
    Image Bottompipeimg;

    // parrot variables
    int parrotX = frameWidth/4;     //parrot in x positiion ---
    int parrotY = frameHeight/2;    //paoort in y pos |
    int parrotWidth = 24;           //parrot sizex ----
    int parrotHeight = 30;          //parrot height y |
    int velocityY = -9;             //jump height
    int gravity = 1;
    boolean gameover=false;
    double score = 0;
    int highscore=0;

    class Parrot{
        int x=parrotX;
        int y=parrotY;
        int width=parrotWidth;
        int height=parrotHeight;
        Image img;

        Parrot(Image img){
            this.img=img;
        }
    }

    // pipe variables
    int pipeX = frameWidth;         //pipetop in x positiion ---
    int pipeY = 0;                  //pipey in y pos |
    int pipeWidth = 64;             //parrot sizex ----     64
    int pipeHeight = 512;           //parrot height y |     512
    int velocityX = -5;             //pipe speed

    class Pipe{
        int x=pipeX;
        int y=pipeY;
        int width=pipeWidth;
        int height=pipeHeight;
        Image img;
        boolean passed=false;
        Pipe(Image img){
            this.img=img;
        }
    }

    //loops
    Parrot parrot;
    Timer gameLoop;
    Timer pipeTimer;
    ArrayList<Pipe> pipes;
    int openSpace = 100 ;          //<-- the more you increase the hard it becomes.
    String name;

    Bird(String name){
        this.name=name;

        setPreferredSize(new Dimension(frameWidth, frameHeight));
        Bgimg = new ImageIcon(getClass().getResource("./background1.png")).getImage();
        Parrotimg = new ImageIcon(getClass().getResource("./bird.png")).getImage();
        Toppipeimg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        Bottompipeimg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        parrot = new Parrot(Parrotimg);     
        pipes = new ArrayList<Pipe>();
    
        pipeTimer = new Timer(1500, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipeTimer.start();

        gameLoop = new Timer(1000/60,this);     //which runs 60fps per 1 sec
        gameLoop.start();

        // for keyListener
        setFocusable(true);
        addKeyListener(this);
    }
    

    public void placePipes(){
        //Top pipes
        Pipe topPipes = new Pipe(Toppipeimg);
        topPipes.y= (int)(pipeY - pipeHeight/4 -Math.random()*pipeHeight/2);
        pipes.add(topPipes);        

        //Bottom pipes
        Pipe bottomPipe = new Pipe(Bottompipeimg);
        bottomPipe.y= topPipes.y + pipeHeight + openSpace; 
        pipes.add(bottomPipe);        
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw (Graphics g){
        g.drawImage(Bgimg, 0, 0, frameWidth ,frameHeight ,null);
        g.drawImage(Parrotimg, parrot.x , parrot.y , parrot.width , parrot.height ,null);

        //Drawing top + bottom pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x , pipe.y , pipe.width , pipe.height ,null);
        }

        //Gameover display
        g.setFont(new Font("", Font.BOLD ,25));     
        g.setColor(Color.black);
        if(gameover){
            g.setFont(new Font("", Font.BOLD ,45));     
            g.drawString("GameOver \n "+ name +" : "+ (int)score, frameWidth/3, frameHeight/2);
            g.setFont(new Font("", Font.BOLD ,30));     
            g.drawString("Press M to Restart" ,frameWidth/3+10,frameHeight/2+40);
        }else{
            g.drawString(name+" Score : "+ (int) score, 10, 20);
            g.drawString("High Score : "+ (int) highscore, 10, 40);
        }
    }

    public void actionPerformed(ActionEvent e) {
        move();          
        repaint();       
        
        if(gameover){
            pipeTimer.stop();
            gameLoop.stop();
        }
    }

    public void move (){
        //bird movement
        velocityY += gravity;
        parrot.y += velocityY;
        parrot.y = Math.max(0,parrot.y);
        
        //pipe movement
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x+=velocityX;

            //Score calculation
            if(!pipe.passed && parrot.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;

                //High Score
                highscore=Math.max(highscore,(int)score);
            } 

            //Gameover Condition
            if(Collision(parrot, pipe)) gameover=true;
        }
        if(parrot.y > frameHeight) gameover=true; //down Gameover
        // if(parrot.y <= 0 ) gameover=true;         //top gameover
    }
    
    //Collision formule
    public boolean Collision(Parrot a,Pipe b){
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }


    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY = -9;
        }

        if(e.getKeyCode()==KeyEvent.VK_M && gameover){
            parrot.y= parrotY;
            velocityY=0;
            gameover=false;
            score=0;
            pipes.clear();
            gameLoop.start();
            pipeTimer.start();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}

class Game extends JPanel implements ActionListener{
    //Login page
    static JFrame frame;
    static JPanel loginpanel;
    static JLabel label;
    static JButton button;
    static JTextField text;
    static String playerName;
    static Bird bird;

    Game(){
        loginpanel = this;
        text = new JTextField(10);
        button = new JButton("Adthava Bro");
        label = new JLabel("What is Your Name");
        button.addActionListener(this);

        loginpanel.add(label);
        loginpanel.add(text);
        loginpanel.add(button);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==button){
            playerName=text.getText();
            if(!playerName.isEmpty()){
                bird = new Bird(playerName);
                frame.remove(loginpanel);
                frame.add(bird);
                frame.pack();
                bird.requestFocus();
                frame.repaint();
            }
        }
    }
    public static void main(String adf[]){
        frame=new JFrame("Bird");
        frame.setSize(Bird.frameWidth, Bird.frameHeight);    
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setLocationRelativeTo(null);  
        frame.setResizable(false);   

        //logo
        ImageIcon logo=new ImageIcon("bird.png");
        frame.setIconImage(logo.getImage());

        // login + Game;
        Game in = new Game();
        frame.add(in);
        frame.setVisible(true);    
    }

}

