import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SquareConstructor {
    private ArrayList<HoughLine> lines;
    private int squareLength;
    private double changeOfTheta;

    public SquareConstructor(ArrayList<HoughLine> lines, int squareLength, double changeOfTheta) {
        this.lines = lines;
        this.squareLength = squareLength;
        this.changeOfTheta = changeOfTheta;
    }

    public class Pair {
        public HoughLine line1;
        public HoughLine line2;

        public Pair (HoughLine line1, HoughLine line2){
            this.line1 = line1;
            this.line2 = line2;
        }
    }

    private List<Pair> findPairs() {
        List<Pair> pairList = new ArrayList<>();

        for (HoughLine line1 : lines) {
            for (HoughLine line2 : lines) {
                if (line2.theta > line1.theta) break; // not possible to find pair anymore as theta for line 2 is bigger.
                if (line1.theta == line2.theta && isCorrectLength(line1, line2)) {
                    Pair pair = new Pair(line1, line2);
                    pairList.add(pair);
                }
            }
        }

        return pairList;
    }

    private boolean isCorrectLength(HoughLine line1, HoughLine line2) {

        double distanceLine1 = Math.cos(line1.theta) * Math.abs(line1.r);
        double distanceLine2 = Math.cos(line2.theta) * Math.abs(line2.r);

        double totalDistance = Math.abs(distanceLine1) + Math.abs(distanceLine2);

        return totalDistance > squareLength;
    }

    private List<Square> findSquares(List<Pair> pairs) {
        List<Square> squares = new ArrayList<>();

        for (Pair pair1 : pairs) {
            for (Pair pair2 : pairs) {

                if (isOrthogonal(pair1, pair2)) {
                    Square square = new Square(pair1, pair2);
                    squares.add(square);
                }
            }
        }

        return squares;
    }

    private boolean isOrthogonal(Pair pair1, Pair pair2) {

        double theta1 = pair1.line1.theta;
        double theta2 = pair2.line1.theta;

        double difference = Math.abs(theta1) - Math.abs(theta2);
        return difference == (Math.PI * 0.50);
    }

    public class Square {
        public Pair pair1;
        public Pair pair2;

        public Square(Pair pair1, Pair pair2) {
            this.pair1 = pair1;
            this.pair2 = pair2;
        }

        public List<HoughLine> getLines() {
            List<HoughLine> lines = new ArrayList<>();

            lines.add(pair1.line1);
            lines.add(pair1.line2);
            lines.add(pair2.line1);
            lines.add(pair2.line2);

            return lines;
        }
    }

    public void draw(ImagePPM image, List<Square> squares) {
        for (Square square : squares) {
            for (HoughLine line : square.getLines()) {
                line.draw(image, Color.RED.getRGB(), changeOfTheta);
            }
        }

        image.WritePPM("squares.ppm");
    }

    public void drawCandidates(ImagePPM image) {
//        System.out.println(sideLength);
//        for (Line line : lines) System.out.println("Theta: " + (int) Math.toDegrees(line.theta) + " p = " + line.p);

        List<Pair> pairs = findPairs();
        List<Square> candidates = findSquares(pairs);

        draw(image, candidates);
    }
}
