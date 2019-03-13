
import com.github.sarxos.webcam.Webcam;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author SISNET
 */
public class VirtualVM {

    List<Webcam> webcams = Webcam.getWebcams();
    ArrayList<String> availableObject=new ArrayList<String>();
    
    ObjectDetection od;
    ObjectDetection od1;
    
    int person=0;
    int diningtable=0;
    int tvmonitor=0;

    public void openDoor() throws IOException {
        try (Socket socket = new Socket("192.168.1.203", 9000)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            do {
                System.out.println(reader.readLine());
            } while (!reader.readLine().equals("close"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        }
    }

    public void takePictureBefore() throws IOException {
        for (Webcam webcam : webcams) {
            //System.out.format("Opening %s\n", webcam.getName());
            webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length - 1]);
            webcam.open();
        }

        // capture picture from all of them
        for (int i = 0; i < webcams.size(); i++) {
            Webcam webcam = webcams.get(i);
            //System.out.format("Capturing %s\n", webcam.getName());
            ImageIO.write(webcam.getImage(), "PNG", new File(String.format("before.png", i)));
        }

        // close all
        for (Webcam webcam : webcams) {
            //System.out.format("Closing %s\n", webcam.getName());
            webcam.close();
        }
    }
    
    public void takePictureAfter() throws IOException {
        for (Webcam webcam : webcams) {
            //System.out.format("Opening %s\n", webcam.getName());
            webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length - 1]);
            webcam.open();
        }

        // capture picture from all of them
        for (int i = 0; i < webcams.size(); i++) {
            Webcam webcam = webcams.get(i);
            //System.out.format("Capturing %s\n", webcam.getName());
            ImageIO.write(webcam.getImage(), "PNG", new File(String.format("after.png", i)));
        }

        // close all
        for (Webcam webcam : webcams) {
            //System.out.format("Closing %s\n", webcam.getName());
            webcam.close();
        }
    }
    
    public void setAvailableObject(ArrayList<String>obj){
        this.availableObject=obj;
    }

    public void run() throws IOException, Exception {
        takePictureBefore();
        od=new ObjectDetection();
        od.getDetectedObject("before.png");
        
        //this.setAvailableObject(od.getDetectedObject());
        for (int i = 0; i < this.availableObject.size(); i++) {
            if (this.availableObject.get(i).equals("person")) {
                person++;
            }
            if (this.availableObject.get(i).equals("diningtable")) {
                diningtable++;
            }
            if (this.availableObject.get(i).equals("tvmonitor")) {
                tvmonitor++;
            }
        }
        System.out.println("Current available object:");
        System.out.println("Person = "+this.person);
        System.out.println("Dining Table = "+this.diningtable);
        System.out.println("Tv Monitor = "+this.tvmonitor);
        this.availableObject=null;
        
        openDoor();
        
        takePictureAfter();
        od1=new ObjectDetection();
        od1.getDetectedObject("after.png");
        //this.setAvailableObject(od1.getDetectedObject());
        int tempPerson=0;
        int tempDiningTable=0;
        int tempTvMonitor=0;
        for (int i = 0; i < this.availableObject.size(); i++) {
            if (this.availableObject.get(i).equals("person")) {
                tempPerson++;
            }
            if (this.availableObject.get(i).equals("diningtable")) {
                tempDiningTable++;
            }
            if (this.availableObject.get(i).equals("tvmonitor")) {
                tempTvMonitor++;
            }
        }
        int minusPerson=this.person-tempPerson;
        int minusDiningTable=this.diningtable-tempDiningTable;
        int minusTvMonitor=this.tvmonitor-tempTvMonitor;
        
        this.person-=minusPerson;
        this.diningtable-=minusDiningTable;
        this.tvmonitor-=minusTvMonitor;
        
        System.out.println("Current available object:");
        System.out.println("Person = "+this.person+" (minus "+minusPerson+" person)");
        System.out.println("Dining Table = "+this.diningtable+" (minus "+minusDiningTable+" dining table)");
        System.out.println("Tv Monitor = "+this.tvmonitor+" (minus "+minusTvMonitor+" tvmonitor)");
    }
}
