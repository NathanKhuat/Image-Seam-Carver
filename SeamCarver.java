package seamcarver;

import energy.GradientEnergyFunction;
import energy.EnergyFunction;
import seamfinding.DynamicProgrammingSeamFinder;
import Picture;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SeamCarver {

    private static final String INPUT_PATH = "data/seamcarving/HJoceanSmall.horizontal.png";
    private static final String OUTPUT_PATH = "result.png";

    private final EnergyFunction f;
    private final SeamFinder seamFinder;
    private Picture picture;

    /**
     * Constructs a seam carver by reading the {@link Picture} from the file, using the given {@link EnergyFunction} and
     * {@link SeamFinder} implementations.
     *
     * @param file       the file path to the image.
     * @param f          the {@link EnergyFunction}.
     * @param seamFinder the {@link SeamFinder}.
     * @throws IOException if an error occurs during reading.
     */
    public SeamCarver(File file, EnergyFunction f, SeamFinder seamFinder) throws IOException {
        if (file == null || f == null || seamFinder == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.picture = new Picture(file);
        this.f = f;
        this.seamFinder = seamFinder;
    }

    public static void main(String[] args) throws IOException {
        EnergyFunction f = new DualGradientEnergyFunction();
        // SeamFinder seamFinder = new GenerativeSeamFinder(DijkstraSolver::new);
        SeamFinder seamFinder = new DynamicProgrammingSeamFinder();
        SeamCarver seamCarver = new SeamCarver(new File(INPUT_PATH), f, seamFinder);
        seamCarver.resizeImage(seamCarver);
        //System.out.println(seamCarver.removeVertical());
        //seamCarver.picture().save(new File(OUTPUT_PATH));
    }

    /**
 * Reduces image dimensions based on user input.
 *
 * @param seamCarver   a seam carver reading from the given input file.
 * @throws IOException if an error occurs during reading.
 */
private void resizeImage(SeamCarver seamCarver) throws IOException {
    int currWidth = picture.width();
    int currHeight = picture.height();

    System.out.println("Current dimensions of (" + INPUT_PATH + ") are " + currWidth + "x" + currHeight + ".");

    int newWidth = getNewDims("width", currWidth);
    int newHeight = getNewDims("height", currHeight);

    System.out.println();
    System.out.print("Reducing width... ");
    for (int i = 0; i < currWidth - newWidth; i++) {
        seamCarver.removeVertical();
        if (i % 10 == 0) {
            System.out.print(currWidth - i + " ");
        }
    }
    System.out.println();

    System.out.print("Reducing height... ");
    for (int i = 0; i < currHeight - newHeight; i++) {
        seamCarver.removeHorizontal();
        if (i % 10 == 0) {
            System.out.print(currHeight - i  + " ");
        }
    }
    System.out.println();
    System.out.println();

    seamCarver.picture().save(new File(OUTPUT_PATH));
    System.out.println("Your image has been resized to " + picture.width() + "x"
                       + picture.height() + " and saved at (" + OUTPUT_PATH + ").");
}

    /**
    * Gets new dimensions for resizing image.
    *
    * @param dimType the type of dimension (width or height).
    * @param oldDim  the current dimension of this type.
    * @return        the new dimension to resize to.
    */
    private int getNewDims(String dimType, int oldDim) {
        Scanner console = new Scanner(System.in);

        System.out.print("Enter a new " + dimType + " (integer between 0 and " + oldDim + "): ");
        int newDim = console.nextInt();
        boolean dimIsValid = (newDim <= oldDim) && (newDim > 0);
        while (!dimIsValid) {
            System.out.println();
            System.out.println("Please enter a " + dimType + " between 0 and " + oldDim + ".");
            System.out.print("Enter a new " + dimType + ": ");
            newDim = console.nextInt();
            dimIsValid = (newDim <= oldDim) && (newDim > 0);
        }
        return newDim;
    }

    /**
     * Checks that the seam is a valid horizontal seam in the picture.
     *
     * @param picture the input picture.
     * @param seam    the seam to remove.
     */
    private static void validate(Picture picture, List<Integer> seam) {
        if (seam == null) {
            throw new NullPointerException("Seam cannot be null");
        } else if (seam.size() == 1) {
            throw new IllegalArgumentException("Cannot remove seam of size 1");
        } else if (seam.size() != picture.width()) {
            throw new IllegalArgumentException("Seam length does not match image size");
        }
        for (int i = 0; i < seam.size() - 2; i++) {
            if (Math.abs(seam.get(i) - seam.get(i + 1)) > 1) {
                throw new IllegalArgumentException("Seam values too far from neighbors at index " + i);
            }
        }
    }

    /**
     * Returns a copy of the current picture.
     *
     * @return a copy of the current picture.
     */
    private Picture picture() {
        return new Picture(picture);
    }

    /**
     * Removes and returns a minimum-cost horizontal seam from the picture.
     *
     * @return a minimum-cost horizontal seam.
     */
    public List<Integer> removeHorizontal() {
        List<Integer> seam = seamFinder.findHorizontal(picture, f);
        validate(picture, seam);
        Picture result = new Picture(picture.width(), picture.height() - 1);
        for (int x = 0; x < picture.width(); x += 1) {
            for (int y = 0; y < seam.get(x); y += 1) {
                result.set(x, y, picture.get(x, y));
            }
            for (int y = seam.get(x); y < picture.height() - 1; y += 1) {
                result.set(x, y, picture.get(x, y + 1));
            }
        }
        picture = result;
        return seam;
    }

    /**
     * Removes and returns a minimum-cost vertical seam from the picture.
     *
     * @return a minimum-cost vertical seam.
     */
    public List<Integer> removeVertical() {
        // Transpose the picture by flipping the x/y and width/height access.
        Picture transposed = new Picture() {
            @Override
            public int get(int x, int y) {
                return picture.image.getRGB(y, x); // (x, y) -> (y, x)
            }

            @Override
            public void set(int x, int y, int rgb) {
                throw new UnsupportedOperationException("Transposed picture is immutable");
            }

            @Override
            public int width() {
                return picture.image.getHeight();
            }

            @Override
            public int height() {
                return picture.image.getWidth();
            }

            @Override
            public void save(File file) {
                throw new UnsupportedOperationException("Transposed picture cannot be saved");
            }
        };
        List<Integer> seam = seamFinder.findHorizontal(transposed, f);
        validate(transposed, seam);
        Picture result = new Picture(picture.width() - 1, picture.height());
        for (int y = 0; y < picture.height(); y += 1) {
            for (int x = 0; x < seam.get(y); x += 1) {
                result.set(x, y, picture.get(x, y));
            }
            for (int x = seam.get(y); x < picture.width() - 1; x += 1) {
                result.set(x, y, picture.get(x + 1, y));
            }
        }
        picture = result;
        return seam;
    }
}
