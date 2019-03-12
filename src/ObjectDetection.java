
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.mxnet.infer.javaapi.ObjectDetector;
import org.apache.mxnet.infer.javaapi.ObjectDetectorOutput;
import org.apache.mxnet.javaapi.Context;
import org.apache.mxnet.javaapi.DType;
import org.apache.mxnet.javaapi.DataDesc;
import org.apache.mxnet.javaapi.Shape;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sisnet
 */
public class ObjectDetection {

    ArrayList<String> detectedObject = new ArrayList<String>();

    public ObjectDetection(String inputImg) {

        String modelPathPrefix = "/home/sisnet/Documents/resnet/res";

        String inputImagePath = "/home/sisnet/Documents/NetBeansProjects/VirtualVM/" + inputImg;

        List<Context> context = getContext();

        Shape inputShape = new Shape(new int[]{1, 3, 640, 480});

        List<DataDesc> inputDescriptors = new ArrayList<DataDesc>();
        inputDescriptors.add(new DataDesc("data", inputShape, DType.Float32(), "NCHW"));

        BufferedImage img = ObjectDetector.loadImageFromFile(inputImagePath);
        ObjectDetector objDet = new ObjectDetector(modelPathPrefix, inputDescriptors, context, 0);
        List<List<ObjectDetectorOutput>> output = objDet.imageObjectDetect(img, 25);

        printOutput(output, inputShape);
    }
    
    public ArrayList<String> getDetectedObject(){
        return this.detectedObject;
    }

    private List<Context> getContext() {
        List<Context> ctx = new ArrayList<>();
        ctx.add(Context.cpu());

        return ctx;
    }

    private void printOutput(List<List<ObjectDetectorOutput>> output, Shape inputShape) {

        StringBuilder outputStr = new StringBuilder();

        int width = inputShape.get(3);
        int height = inputShape.get(2);

        for (List<ObjectDetectorOutput> ele : output) {
            for (ObjectDetectorOutput i : ele) {
                if (i.getProbability() >= 0.6) {
                    this.detectedObject.add(i.getClassName());
                    //outputStr.append("Class: " + i.getClassName() + "\n");
                    //outputStr.append("Probabilties: " + i.getProbability() + "\n");
                    /**
                     * List<Float> coord = Arrays.asList(i.getXMin() * width,
                     * i.getXMax() * height, i.getYMin() * width, i.getYMax() *
                     * height); StringBuilder sb = new StringBuilder(); for
                     * (float c : coord) { sb.append(", ").append(c); }
                     * outputStr.append("Coord:" + sb.substring(2) + "\n");
                     *
                     */
                }
            }
        }
        //System.out.println(outputStr);
    }
}
