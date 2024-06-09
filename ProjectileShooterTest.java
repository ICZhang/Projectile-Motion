package JavaGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class ProjectileShooterTest
{
	
	private static JLabel label = new JLabel("Time:  0     ");
	private static JLabel label2 = new JLabel("Max height:  0     ");
	private static JLabel label3 = new JLabel("Horizontal distance:  0     ");
	
	
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600,600);
        f.setBackground(Color.cyan);

        final ProjectileShooter projectileShooter = 
            new ProjectileShooter();
        ProjectileShooterPanel projectileShooterPanel = 
            new ProjectileShooterPanel(projectileShooter);
        projectileShooter.setPaintingComponent(projectileShooterPanel);

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        controlPanel.setLayout(new GridLayout(0, 4));

        controlPanel.add(new JLabel("Angle (Degrees)"));
        final JSlider angleSlider = new JSlider(0, 90, 45);
        controlPanel.add(angleSlider);
        angleSlider.setMinorTickSpacing(2);  
        angleSlider.setMajorTickSpacing(45);  
        angleSlider.setPaintTicks(true);  
        angleSlider.setPaintLabels(true); 
 
        controlPanel.setBackground(Color.gray);

        controlPanel.add(new JLabel("Initial Velocity (m/s)"));
        final JSlider powerSlider = new JSlider(0, 100, 50);
        controlPanel.add(powerSlider);
        powerSlider.setMinorTickSpacing(2);  
        powerSlider.setMajorTickSpacing(50);  
        powerSlider.setPaintTicks(true);  
        powerSlider.setPaintLabels(true);
        
        controlPanel.add(new JLabel("Initial Height (m)"));
        JSlider heightSlider = new JSlider(0, 300, 150);
        controlPanel.add(heightSlider);
        heightSlider.setMinorTickSpacing(2);  
        heightSlider.setMajorTickSpacing(100);  
        heightSlider.setPaintTicks(true);  
        heightSlider.setPaintLabels(true);
        
        controlPanel.add(new JLabel("Starting x (m)"));
        JSlider rSlider = new JSlider(0, 300, 150);
        controlPanel.add(rSlider);
        rSlider.setMinorTickSpacing(2);  
        rSlider.setMajorTickSpacing(100);  
        rSlider.setPaintTicks(true);  
        rSlider.setPaintLabels(true);
        
        
        controlPanel.add(label);
        controlPanel.add(label2);
        controlPanel.add(label3);
        

        JButton shootButton = new JButton("Shoot");
      
        shootButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int angleDeg = angleSlider.getValue();
                int power = powerSlider.getValue();
                projectileShooter.setAngle(Math.toRadians(angleDeg));
                projectileShooter.setPower(power);
                projectileShooter.setHeight(heightSlider.getValue());
                projectileShooter.setX(rSlider.getValue());
                projectileShooter.shoot();
                int height = heightSlider.getValue();
                
                double g = 9.81;
                
                DecimalFormat df = new DecimalFormat("##.##");
               
                
                double airTime = (power * Math.sin(angleDeg*Math.PI/180) + Math.sqrt(Math.pow(power * Math.sin(angleDeg*Math.PI/180),2) + 2 * g * height))/g;
                label.setText("Air Time:  " + df.format(airTime) + " s");
                
                double mT = height + ((Math.pow(power, 2) * Math.pow(Math.sin(angleDeg*Math.PI/180), 2))/((2 * g)));
                label2.setText("Max height:  " + Math.round(mT));
                
                double angleD = angleDeg*Math.PI/180;
                double hD = power * Math.cos(angleD) * ((power * Math.sin(angleD) + (Math.sqrt(Math.pow(power * Math.sin(angleD), 2) + 2 * g * height)))/g);
                label3.setText("Horizontal distance:  " + df.format(hD));
                
                
            }
        });
        controlPanel.add(shootButton);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(controlPanel, BorderLayout.NORTH);
        f.getContentPane().add(projectileShooterPanel, BorderLayout.CENTER);
        f.setVisible(true);
        f.setBackground(Color.cyan);
        
    }
}

class ProjectileShooter
{
	private double angleRad = Math.toRadians(45);
    private double power = 50;
    private double height = 50.0;
    private double x = 50.0;
    private Projectile projectile;
    private JComponent paintingComponent;

    void setPaintingComponent(JComponent paintingComponent)
    {
        this.paintingComponent = paintingComponent;
    }

    void setAngle(double angleRad)
    {
        this.angleRad = angleRad;
    }

    void setPower(double power)
    {
        this.power = power;
    }
    void setHeight(double h) {
    	this.height = h;
    }
    void setX(double x) {
    	this.x = x;
    }

    void shoot()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                executeShot();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void executeShot()
    {
        if (projectile != null)
        {
            return;
        }
        projectile = new Projectile();

        Point2D velocity = AffineTransform.getRotateInstance(angleRad).transform(new Point2D.Double(1,0), null);velocity.setLocation(velocity.getX() * power * 0.5, velocity.getY() * power * 0.5);
        Point2D height2 = new Point2D.Double(x,height);
        projectile.setVelocity(velocity);
        projectile.setHeight(height2);

        long prevTime = System.nanoTime();
        while (projectile.getPosition().getY() >= 0)
        {
            long currentTime = System.nanoTime();
            double dt = 3 * (currentTime - prevTime) / 1e8;
            projectile.performTimeStep(dt);
            
            prevTime = currentTime;
            paintingComponent.repaint();
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

        projectile = null;
        paintingComponent.repaint();
    }

    Projectile getProjectile()
    {
        return projectile;
    }
}

class Projectile
{
    private final Point2D ACCELERATION = new Point2D.Double(0, -9.81 * 0.1);
    private final Point2D position = new Point2D.Double();
    private final Point2D velocity = new Point2D.Double();

    public Point2D getPosition()
    {
        return new Point2D.Double(position.getX(), position.getY());
    }
    public void setPosition(Point2D point)
    {
        position.setLocation(point);
    }

    public void setVelocity(Point2D point)
    {
        velocity.setLocation(point);
    }
    
    public void setHeight(Point2D point) {
    	
    	position.setLocation(point);
    }

    void performTimeStep(double dt)
    {
        scaleAddAssign(velocity, dt, ACCELERATION);
        scaleAddAssign(position, dt, velocity);
    }

    private static void scaleAddAssign(Point2D result, double factor, Point2D addend)
    {
        double x = result.getX() + factor * addend.getX();
        double y = result.getY() + factor * addend.getY();
        result.setLocation(x, y);
    }

}

class ProjectileShooterPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final ProjectileShooter projectileShooter;

    public ProjectileShooterPanel(ProjectileShooter pS)
    {
        projectileShooter = pS;
    }

    @Override
    protected void paintComponent(Graphics gr)
    {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;

        Projectile projectile = projectileShooter.getProjectile();
        if (projectile != null)
        {
            g.setColor(Color.BLUE);
            Point2D position = projectile.getPosition();
            int x = (int)position.getX();
            int y = getHeight() - (int)position.getY();
            
            g.fillOval(x, y, 20, 20);
        }
    }
}
