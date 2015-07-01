/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Sunish
 */
public class KMeans {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length < 3) {
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try {
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k = Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k) {
        HashMap<Integer, Integer> DataPoints = new HashMap<Integer, Integer>(); // RGB value, Count in arrayrgb
        HashMap<Integer, Integer> PointsInCluster = new HashMap<Integer, Integer>(); // RGB value, in which cluster
        HashMap<Integer, ArrayList<Integer>> clusters;// Cluster number, points in the cluster
        for (int i = 0; i < rgb.length; i++) {
            //System.out.print(rgb[i] + "\t");
            if (DataPoints.containsKey(rgb[i])) {
                int count = DataPoints.get(rgb[i]);
                count++;
                DataPoints.put((rgb[i]), new Integer(count));
            } else {
                DataPoints.put((rgb[i]), new Integer(1));
            }
            if (!PointsInCluster.containsKey(rgb[i])) {
                PointsInCluster.put((rgb[i]), 0);
            }
        }
        //System.out.println();
        clusters = new HashMap<Integer, ArrayList<Integer>>();
        for (int i = 0; i < k; i++) {
            int value = (int) (Math.random() * rgb.length);
            ArrayList<Integer> row = new ArrayList<Integer>();
            if (!clusters.containsKey(rgb[value])) {
                clusters.put(rgb[value], row);
            } else {
                i--;
            }
//                            clusters.put(-5793138, row);
//                clusters.put(-15067631, row);
//                clusters.put(-1648177, row);
//                clusters.put(-11779528, row);
//                clusters.put(-8621730, row);
////            clusters.put(-22493,row);
////            clusters.put(-6547,row);
////            clusters.put(-2131,row);

        }
        if (clusters.size() != k) {
            System.out.print("Error... Try again");
            System.exit(1);
        }
        int loopbreak = 0;
        while (true) {
            //loopbreak = 0;
            loopbreak++;
            boolean valueChanged = false;
//            for (int i = 0; i < rgb.length; i ++) {
//                int rbg_value = rgb[i];
            for (Integer rbg_value : DataPoints.keySet()) {
                double similarity = Double.POSITIVE_INFINITY;
                int clusterIndex = 0;
                for (Integer clusterId : clusters.keySet()) {
                    Color datapoint = new Color(rbg_value);
                    Color center = new Color(clusterId);
                    double blue = Math.pow((datapoint.getBlue() - center.getBlue()), 2);
                    double red = Math.pow((datapoint.getRed() - center.getRed()), 2);
                    double green = Math.pow((datapoint.getGreen() - center.getGreen()), 2);
                    double distance =  Math.sqrt((blue + red + green));
//                    double blue = Math.abs((datapoint.getBlue() - center.getBlue()));
//                    double red = Math.abs((datapoint.getRed() - center.getRed()));
//                    double green = Math.abs((datapoint.getGreen() - center.getGreen()));
//                    double distance =  (blue + red + green)/3;
                    if (distance < similarity) {
                        similarity = distance;
                        clusterIndex = clusterId;
                    }
                }
                int originalClusterId = PointsInCluster.get(rbg_value);
                if (originalClusterId != clusterIndex) {
                    //loopbreak++;
                    valueChanged = true;
                    PointsInCluster.put(rbg_value, new Integer(clusterIndex));
//                    if (clusters.containsKey(originalClusterId)) {
//                        ArrayList<Integer> row2 = clusters.get(originalClusterId);
//                        if (row2.contains(rbg_value)) {
//                            System.out.println("Found");
//                            System.exit(1);
//                        }
//                    }
                    ArrayList<Integer> row = clusters.get(clusterIndex);
                    row.add(rbg_value);
                    clusters.remove(clusterIndex);
                    clusters.put(clusterIndex, row);
                }
            }
            if (loopbreak > 100) {
                System.out.println("Loop terminated after " + loopbreak + " iterations");
                break;
                //System.out.println("Loop break after " + loopbreak + " iterations");
                //kmeans(rgb, k);
            }
            if (!valueChanged) {
                System.out.println("Found Convergence");
                break;
            }
            ArrayList<Integer> newCluster = new ArrayList<Integer>();
            for (Integer clusterId : clusters.keySet()) {
                ArrayList<Integer> row = clusters.get(clusterId);
                int meanRed = 0;
                int meanBlue = 0;
                int meanGreen = 0;
                int count = 0;
                
                for (Integer rgb_value : row) {
                    Color temp = new Color(rgb_value);
                    meanRed = meanRed + (temp.getRed() * DataPoints.get(rgb_value));
                    meanBlue = meanBlue + (temp.getBlue() * DataPoints.get(rgb_value));
                    meanGreen = meanGreen + (temp.getGreen() * DataPoints.get(rgb_value));
                    //mean = mean + (rgb_value);
                    count = count + DataPoints.get(rgb_value);
                }
                if (count == 0) {
                    int value = (int) (Math.random() * rgb.length);
                    //System.out.println("Error");
                    newCluster.add(rgb[value]);
                } else {
                    Color temp = new Color((int)meanRed/count, (int)meanBlue/count, (int)meanGreen/count);
                    int ans = temp.getRGB();
                    newCluster.add((int) ans);
                }
            }
            
            clusters.clear();
            for (Integer clusterId : newCluster) {
                ArrayList<Integer> rowEmpty = new ArrayList<Integer>();
                if (clusters.containsKey(clusterId)) {
                    int value = (int) (Math.random() * rgb.length);
                    clusters.put(rgb[value], rowEmpty);
                } else {
                    clusters.put(clusterId, rowEmpty);
                }
//                clusters.put(-5793138, rowEmpty);
//                clusters.put(-15067631, rowEmpty);
//                clusters.put(-1648177, rowEmpty);
//                clusters.put(-11779528, rowEmpty);
//                clusters.put(-8621730, rowEmpty);
                
            }
            for (Integer clusterId : clusters.keySet()) {
                System.out.print(clusterId + "\t");
            }
            System.out.println();
            if (newCluster.size() != k) {
                System.out.print("Error... Try again");
                System.exit(1);
            }
        }

        for (int i = 0; i < rgb.length; i++) {
            int clusterValue = PointsInCluster.get(rgb[i]);
            rgb[i] = clusterValue;
        }
    }
}
