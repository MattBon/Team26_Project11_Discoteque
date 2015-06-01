package com.hueemulator.emulator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.hueemulator.gui.View;
import com.hueemulator.model.PHBridgeConfiguration;
import com.hueemulator.model.PHLight;
import com.hueemulator.model.PHLightState;
import com.hueemulator.utils.OpenFileFilter;

public class Controller {
 
    // Start up the Emulator
    private Emulator emulator;
    
    private Model model;

    private View view;
    public String consoleText="";
    private DateFormat inputFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private String ipAddress;
    
    private MutableAttributeSet sas;
    private StyleContext context;
    public boolean hasBridgeBeenPushLinked=false;
    
    public Controller(Model model, View view){
        this.model = model;
        this.view = view;  
        
        this.sas = new SimpleAttributeSet();
        this.context = new StyleContext();
        this.context.addStyle("test", null);
        
        emulator = new Emulator(this);        
        
        String introText  = "Welcome to the Hue Emulator.  Choose a port and click the Start Button"; 
        addTextToConsole(introText, Color.YELLOW, true);   
    }
    
    public void addPropertiesListeners() {
     view.getPropertiesFrame().getIncludeTime().addActionListener(new ActionListener() {  
     public void actionPerformed(ActionEvent e) {
       boolean isSelected = view.getPropertiesFrame().getIncludeTime().isSelected();
       model.setShowConsoleTime(isSelected);  
   }
    });
    }
    
    public void addMenuListeners(){
     // Add Listeners For Stop and Start Buttons.
     view.getMenuBar().getStartButton().addActionListener(new ActionListener() {             
         public void actionPerformed(ActionEvent e)
         {             
             emulator.startServers();
             view.getMenuBar().getStartButton().setEnabled(false);
             view.getMenuBar().getStopButton().setEnabled(true);             
         }
        });       
     
     view.getMenuBar().getStopButton().addActionListener(new ActionListener() {             
         public void actionPerformed(ActionEvent e)
         {
             emulator.stopServer();
             view.getMenuBar().getStartButton().setEnabled(true);
             view.getMenuBar().getStopButton().setEnabled(false);
         }
        });    
        view.getMenuBar().getClearConsoleMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                clearConsole();
                addTextToConsole("Console Cleared", Color.WHITE, true);
            }
        });        
     
        
        view.getMenuBar().getViewGraphicsMenuItems().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                if (view.getMenuBar().getViewGraphicsMenuItems().isSelected()) {                     
                    view.getGraphicsPanel().setVisible(true);
                }
                else {
                    view.getGraphicsPanel().setVisible(false);
                }
            }
        });
        view.getMenuBar().getLoadConfigMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                final JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new OpenFileFilter("json","JSON Config file") );
                //In response to a button click:
                int returnVal = fc.showOpenDialog(view.getMenuBar());
  
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String fileName = fc.getSelectedFile().getAbsoluteFile().getAbsolutePath();
                    boolean loadedNewConfig = emulator.loadConfiguration( fileName, true);

                    if (loadedNewConfig) {
                        view.getGraphicsPanel().repaint();
                        repaintBulbs();                        
                    }
                    else {
                        JOptionPane.showMessageDialog(view.getConsole(), "Config file (" + fileName + ") could not be loaded.  Is it a valid config.json file?");                       
                    }
                } 
            }
        });         
        view.getMenuBar().getSaveConfigMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
                final JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new OpenFileFilter("json","JSON Config file") );
                //In response to a button click:
                int returnVal = fc.showSaveDialog(view.getMenuBar());
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                     String fileName = fc.getSelectedFile().getAbsoluteFile().getAbsolutePath();
                     
                     String extension = "";

                     int i = fileName.lastIndexOf('.');
                     if (i > 0) {
                         extension = fileName.substring(i);
                     }
                     
                     if (!extension.equals("") && !extension.equals(".json")) {
                         JOptionPane.showMessageDialog(view.getConsole(), "Please save the with a .json file extension, or leave blank.");  
                     }
                     else {
                         if (extension.equals(""))fileName += ".json";
                         
                         ObjectMapper mapper = new ObjectMapper();
                         try {
                            mapper.writeValue(new File(fileName), model.getBridgeConfiguration());
                        } catch (JsonGenerationException e1) {
                            e1.printStackTrace();
                        } catch (JsonMappingException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                     }

                } 
            }
        });         
        
      
        view.getMenuBar().getAboutMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {
               view.getAbout().setLocation(300,300);               
               view.getAbout().setVisible(true);
            }
        }); 
        
        view.getMenuBar().getHelpMenuItem().addActionListener(new ActionListener() {             
            public void actionPerformed(ActionEvent e)
            {

              view.getHelp().setLocation(300,300);               
              view.getHelp().setVisible(true);
            }
        });        
        
 
    } 
 
    
     
     public Controller() {
     
     }
     
     
     public void addTextToConsole(String text, Color textColour, boolean appendText) {   
      
      if (view==null) {
          return; // No View for JUnit tests, so this is null.
      }
      
         Date currentDate = new Date();
         String dateString = inputFormat.format(currentDate);
        
         if (!model.isShowConsoleTime()) {   // Don't show date string if user has de-selected it (in propertiesFrame)
          dateString="";
         }
         
         if (appendText) {   // Can now be disabled in menus
             append(dateString, text, textColour, view.getConsole());
         }
         
         repaintBulbs();

     }
     
     public void repaintBulbs() {
         // Repaint the Light Bulbs after Every Command.
         view.getGraphicsPanel().repaint(); 
         
         // Repaint the Separate Frame version (showing large bulbs), if this is being used.
         if (view.getMenuBar().getLightFrame() != null) {
            view.getMenuBar().getLightFrame().repaint();
         }
     }
     
     /**
      * When the emulator starts we should update the Bridge Config with the IP Address and PORT this Emulator is running on.
      * This is needed as JSON responses are sent using the IP Address.
      * 
      * @throws UnknownHostException 
      */
     public void setIPAddress() throws UnknownHostException {
         PHBridgeConfiguration bridgeConfiguration = model.getBridgeConfiguration();

         InetAddress ip = InetAddress.getLocalHost();

         String ipAddressAndPort = ip.getHostAddress() + ":" + view.getMenuBar().getPort().getText();
         bridgeConfiguration.getConfig().setIpaddress(ipAddressAndPort);
         this.ipAddress = ipAddressAndPort;
     }
     
     public String getPort() {
      return view.getMenuBar().getPort().getText();
     }
     
     public Model getModel() {
   return model;
  }

  public void setModel(Model model) {
   this.model = model;
  }   
  
  public void paintGraphicsPanel() {
   
  }
  
    public void addNewBulb() {
        PHBridgeConfiguration bridgeConfiguration = model.getBridgeConfiguration();
        
        int numberOfLights = bridgeConfiguration.getLights().size();
        int newIdentifer = numberOfLights+1;
        String newLightId = "" + newIdentifer;
        
        PHLight light = new PHLight();
        light.setName("New Light - " + newLightId);
        light.setModelid("LCT001");
        
        PHLightState lightState = new PHLightState();
        lightState.setHue(5000);
        lightState.setBri(254);
        lightState.setSat(254);
        lightState.setOn(true);
        lightState.setReachable(true);
        lightState.setEffect("none");
        lightState.setAlert("none");  
        light.setState(lightState);
        
        bridgeConfiguration.getLights().put(newLightId, light);
        addTextToConsole("New Bulb Created: " + newLightId, Color.ORANGE, true); 
      }

    public String getIpAddress() {
        return ipAddress;
    }
    
    public void clearConsole() {
    //    System.out.println("Clear Console " +  int docLength = document.getLength(););
        try {
            view.getConsole().getDocument().remove(0,  view.getConsole().getDocument().getLength());
        } catch (BadLocationException e) {}
    }
    
    public void append(final String dateString, final String s, final Color color,  final JEditorPane console) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    append(dateString, s, color, console);
                }
            });
            return;
        }
        Document document = console.getDocument();

        Style style = context.addStyle("test", null);
        // set some style properties
        StyleConstants.setForeground(style, color);
 
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
       
       
        try {
            StyleConstants.setForeground(sas, Color.yellow);
            int docLength = document.getLength();
            document.insertString(docLength, dateString, null);
            docLength = document.getLength();
            document.insertString(docLength, "   " + s + "\n", style);
            console.setCaretPosition( console.getDocument().getLength());
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isHasBridgeBeenPushLinked() {
        return hasBridgeBeenPushLinked;
    }

    public void setHasBridgeBeenPushLinked(boolean hasBridgeBeenPushLinked) {
        this.hasBridgeBeenPushLinked = hasBridgeBeenPushLinked;
    }

    public boolean showRequestJson() {
        return model.isShowRequestJSON();
    }
    
    public void setShowRequestJson(boolean showRequestJSON) {
        model.setShowRequestJSON(showRequestJSON);
    }
    
    public boolean showResponseJson() {
        return model.isShowResponseJSON();
    }
    
    public void setShowResponseJson(boolean showResponseJSON) {
        model.setShowResponseJSON(showResponseJSON);
    }
    
    public void setShowFullConfigJson(boolean showFullConfigJSON) {
        model.setShowFullConfigJSON(showFullConfigJSON);
    }
}
