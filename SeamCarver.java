import edu.princeton.cs.algs4.*;


public class SeamCarver {

    private Picture picture;
    private int vcount;
    private int hcount;

    public SeamCarver(Picture picture) {

        if (picture == null) throw new IllegalArgumentException("xxx");

        this.picture = new Picture(picture);
        vcount = 0;
        hcount = 0;


    }

    public Picture picture() {

        if (vcount == 0 && hcount == 0) return picture;

        Picture newpic = new Picture(width() - vcount, height() - hcount);

        for (int i = 0; i < newpic.width(); i++){
            for (int j = 0; j < newpic.height(); j++){
                newpic.setRGB(i, j, picture.getRGB(i, j));
            }
        }

        picture = newpic;
        vcount = 0;
        hcount = 0;

        return picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int x, int y) {

        if (x >= width() || y >= height() || x < 0 || y < 0) throw new IllegalArgumentException("out of bounds");

        if (x == 0 || y ==0 || x == width() - 1 || y == height() - 1) return 1000.00;

        double deltax = delta(x + 1, y, x - 1, y);
        double deltay = delta(x, y + 1, x, y - 1);

        return Math.sqrt(deltax + deltay);

    }

    private double delta(int x1, int y1, int x2, int y2) {

        int rgb1 = picture.getRGB(x1,y1);

        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >>  8) & 0xFF;
        int b1 = rgb1 & 0xFF;

        int rgb2 = picture.getRGB(x2,y2);

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >>  8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        double deltaR = (r1-r2) * (r1-r2);
        double deltaG = (g1-g2) * (g1-g2);
        double deltaB = (b1-b2) * (b1-b2);

        return deltaR + deltaG + deltaB;


    }

    public int[] findVerticalSeam2() {

        int w = width() - vcount;
        int h = height();

        int[] seam = new int[h];
        int top = h * w;
        int bottom = h * w + 1;




        EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(h * w + 2);

        // create edges betwin first to every one in first row, and betwin last and every one in last row
        for (int i = 0; i < w; i++) {
            ewd.addEdge(new DirectedEdge(top, i, 0 ));
            ewd.addEdge(new DirectedEdge(i + (h - 1) * w, bottom, 0 ));
        }

        // create all other edges
        for (int i = 0; i < w * h; i++) {

            // left column
            if (i % w == 0 && i < (h - 1) * w) {
                ewd.addEdge(new DirectedEdge(i, i + w, energy(i % w, (i + w) / w)));
                ewd.addEdge(new DirectedEdge(i, i + w + 1, energy((i + 1) % w, (i + w) / w)));
            } else
            // rigth column
            if (i % w == w - 1 && i < (h - 1) * w) {
                ewd.addEdge(new DirectedEdge(i, i + w, energy(i % w, (i + w) / w)));
                ewd.addEdge(new DirectedEdge(i, i + w - 1, energy((i - 1) % w, (i + w) / w)));
            } else
            // all other middle columns
            if ( i < (h - 1) * w) {
                ewd.addEdge(new DirectedEdge(i, i + w, energy(i % w, (i + w) / w)));
                ewd.addEdge(new DirectedEdge(i, i + w - 1, energy((i - 1) % w, (i + w) / w)));
                ewd.addEdge(new DirectedEdge(i, i + w + 1, energy((i + 1) % w, (i + w) / w)));
            }


        }

        DijkstraSP sp = new DijkstraSP(ewd, top);

        int i = 0;
        for (DirectedEdge e : sp.pathTo(bottom)) {

            int a = e.to();

            if (a != bottom)  seam[i] = a % w;

            i++;
        }



        return seam;
    }

    public int[] findVerticalSeam() {

        int w = width() - vcount;
        int h = height();

        int[] seam = new int[h];
        int top = h * w;
        int bottom = h * w + 1;




        //EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(h * w + 2);
        int[] vertex = new int[h * w + 2];
        double[] distTo = new double[h * w + 2];
        int[] lastV = new int[h * w + 2];

        for (int i = 0; i < (w * h + 2); i++) {
            distTo[i] = h * 1000.00;
        }
        // create edges betwin first to every one in first row, and betwin last and every one in last row
        for (int i = 0; i < w; i++) {
            //ewd.addEdge(new DirectedEdge(top, i, 0 ));
            distTo[i] = 0;
            lastV[i] = top;
            //
            // ewd.addEdge(new DirectedEdge(i + (h - 1) * w, bottom, 0 ));
        }
        lastV[top] = -1;




        // create all other edges
        for (int i = 0; i < w * h; i++) {

            // relax  ewd.addEdge(new DirectedEdge(i, i + w, energy(i % w, (i + w) / w)));


            // left column
            if (i % w == 0 && i < (h - 1) * w) {
                // relax  ewd.addEdge(new DirectedEdge(i, i + w + 1, energy((i + 1) % w, (i + w) / w)));
                if (distTo[i + w + 1] > distTo[i] + energy((i + 1) % w, (i + w) / w)){
                    distTo[i + w + 1] = distTo[i] + energy((i + 1) % w, (i + w) / w);
                    lastV[i + w + 1] = i;
                }
                if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                    distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                    lastV[i + w] = i;
                }

            } else
                // rigth column
            if (i % w == w - 1 && i < (h - 1) * w) {
                    if (distTo[i + w - 1] > distTo[i] + energy((i - 1) % w, (i + w) / w)){
                        distTo[i + w - 1] = distTo[i] + energy((i - 1) % w, (i + w) / w);
                        lastV[i + w - 1] = i;
                    }
                    if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                        distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                        lastV[i + w] = i;
                    }

            } else
                    // all other middle columns
                    if ( i < (h - 1) * w) {
                        if (distTo[i + w + 1] > distTo[i] + energy((i + 1) % w, (i + w) / w)){
                            distTo[i + w + 1] = distTo[i] + energy((i + 1) % w, (i + w) / w);
                            lastV[i + w + 1] = i;
                        }
                        if (distTo[i + w - 1] > distTo[i] + energy((i - 1) % w, (i + w) / w)){
                            distTo[i + w - 1] = distTo[i] + energy((i - 1) % w, (i + w) / w);
                            lastV[i + w - 1] = i;
                        }
                        if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                            distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                            lastV[i + w] = i;
                        }



                    }


        }

        for (int i = 0; i < w; i++) {

            if (distTo[bottom] > distTo[i + (h - 1) * w]){
                distTo[bottom] = distTo[i + (h - 1) * w];
                lastV[bottom] = i + (h - 1) * w;
            }

        }


        int j = h-1;
        for (int i = lastV[bottom]; lastV[i] != -1; i = lastV[i]) {

            seam[j] = i % w;

            j--;
        }




        return seam;
    }

    public int[] findHorizontal2Seam() {

        int w = width() - vcount;
        int h = height();

        int[] seam = new int[h];
        int top = h * w;
        int bottom = h * w + 1;




        //EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(h * w + 2);
        int[] vertex = new int[h * w + 2];
        double[] distTo = new double[h * w + 2];
        int[] lastV = new int[h * w + 2];

        for (int i = 0; i < (w * h + 2); i++) {
            distTo[i] = h * 1000.00;
        }
        // create edges betwin first to every one in first row, and betwin last and every one in last row
        for (int i = 0; i < w; i++) {
            //ewd.addEdge(new DirectedEdge(top, i, 0 ));
            distTo[i] = 0;
            lastV[i] = top;
            //
            // ewd.addEdge(new DirectedEdge(i + (h - 1) * w, bottom, 0 ));
        }
        lastV[top] = -1;




        // create all other edges
        for (int i = 0; i < w * h; i++) {

            // relax  ewd.addEdge(new DirectedEdge(i, i + w, energy(i % w, (i + w) / w)));


            // left column
            if (i % w == 0 && i < (h - 1) * w) {
                // relax  ewd.addEdge(new DirectedEdge(i, i + w + 1, energy((i + 1) % w, (i + w) / w)));
                if (distTo[i + w + 1] > distTo[i] + energy((i + 1) % w, (i + w) / w)){
                    distTo[i + w + 1] = distTo[i] + energy((i + 1) % w, (i + w) / w);
                    lastV[i + w + 1] = i;
                }
                if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                    distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                    lastV[i + w] = i;
                }

            } else
                // rigth column
                if (i % w == w - 1 && i < (h - 1) * w) {
                    if (distTo[i + w - 1] > distTo[i] + energy((i - 1) % w, (i + w) / w)){
                        distTo[i + w - 1] = distTo[i] + energy((i - 1) % w, (i + w) / w);
                        lastV[i + w - 1] = i;
                    }
                    if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                        distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                        lastV[i + w] = i;
                    }

                } else
                    // all other middle columns
                    if ( i < (h - 1) * w) {
                        if (distTo[i + w + 1] > distTo[i] + energy((i + 1) % w, (i + w) / w)){
                            distTo[i + w + 1] = distTo[i] + energy((i + 1) % w, (i + w) / w);
                            lastV[i + w + 1] = i;
                        }
                        if (distTo[i + w - 1] > distTo[i] + energy((i - 1) % w, (i + w) / w)){
                            distTo[i + w - 1] = distTo[i] + energy((i - 1) % w, (i + w) / w);
                            lastV[i + w - 1] = i;
                        }
                        if (distTo[i + w] > distTo[i] + energy(i % w, (i + w) / w)){
                            distTo[i + w] = distTo[i] + energy(i % w, (i + w) / w);
                            lastV[i + w] = i;
                        }



                    }


        }

        for (int i = 0; i < w; i++) {

            if (distTo[bottom] > distTo[i + (h - 1) * w]){
                distTo[bottom] = distTo[i + (h - 1) * w];
                lastV[bottom] = i + (h - 1) * w;
            }

        }


        int j = h-1;
        for (int i = lastV[bottom]; lastV[i] != -1; i = lastV[i]) {

            seam[j] = i % w;

            j--;
        }




        return seam;
    }






    public int[] findHorizontalSeam() {

        int h = height() - hcount;


        int[] seam = new int[width()];
        int left = h * width();
        int right = h * width() + 1;




        EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(h * width() + 2);

        // create edges betwin first to every one in first row, and betwin last and every one in last row
        for (int i = 0; i < h; i++) {
            ewd.addEdge(new DirectedEdge(left, i * width(), 0));
            ewd.addEdge(new DirectedEdge(i * width() + (width() - 1) , right, 0));
        }

        // create all other edges
        for (int i = 0; i < width() * h; i++) {

            // top row
            if (i < width() && i % width() != width() - 1) {
                ewd.addEdge(new DirectedEdge(i, i + 1, energy((i + 1) % width(), (i + 1) / width())));
                ewd.addEdge(new DirectedEdge(i, i + width() + 1, energy((i + 1) % width(), (i + width()) / width())));
            } else
                // bottom row
                if (i >= (h - 1) * width() && i % width() != width() - 1) {
                    ewd.addEdge(new DirectedEdge(i, i + 1, energy((i + 1) % width(), (i + 1) / width())));
                    ewd.addEdge(new DirectedEdge(i, i - width() + 1, energy((i + 1) % width(), (i - width()) / width())));
                } else
                    // all other middle rows
                    if ( i < (h - 1) * width()) {
                        ewd.addEdge(new DirectedEdge(i, i + 1, energy((i + 1) % width(), (i + 1) / width())));
                        ewd.addEdge(new DirectedEdge(i, i + width() + 1, energy((i + 1) % width(), (i + width()) / width())));
                        ewd.addEdge(new DirectedEdge(i, i - width() + 1, energy((i + 1) % width(), (i - width()) / width())));
                    }


        }

        DijkstraSP sp = new DijkstraSP(ewd, left);

        int i = 0;
        for (DirectedEdge e : sp.pathTo(right)) {

            int a = e.to();

            if (a != right)  seam[i] = a / width();

            i++;
        }



        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {

        if (seam == null) throw new IllegalArgumentException("seam is null");
        if (seam.length != width()) throw new IllegalArgumentException("seam size not correct");
        if (height() <= 1) throw new IllegalArgumentException("height < 1");

        for (int i = 0; i < seam.length; i++) {

            if (seam[i] >= height() || seam[i] < 0) throw new IllegalArgumentException("seam went out of y bounds");
            if (i < seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1 ) throw new IllegalArgumentException("y index not continues");


            for (int j = seam[i]; j < height() - 1; j++) {

                picture.setRGB(i, j, picture.getRGB(i, j + 1) );

            }
        }

        hcount++;
    }

    public void removeVerticalSeam(int[] seam) {

        if (seam == null) throw new IllegalArgumentException("seam is null");
        if (seam.length != height()) throw new IllegalArgumentException("seam size not correct");
        if (width() <= 1) throw new IllegalArgumentException("width < 1");

        for (int i = 0; i < seam.length; i++) {

            if (seam[i] >= width() || seam[i] < 0) throw new IllegalArgumentException("seam went out of x bounds");
            if (i < seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1 ) throw new IllegalArgumentException("x index not continues");

            for (int j = seam[i]; j < width() - 1; j++) {

               picture.setRGB(j, i, picture.getRGB(j + 1, i) );
             //   picture.setRGB(j, i, Color.BLACK.getRGB());
            }
        }

        vcount++;

    }


    public static void main(String[] args) {


        Picture pic = new Picture("chameleon.png");
        SeamCarver sc = new SeamCarver(pic);


    //    int[] first = sc.findVerticalSeam();
   //     int[] second = sc.findVerticalSeam2();

      //  for (int i = 0; i < 1; i++) {
      //      for (int j = 0; j < 300; j++) {
             //   System.out.println(sc.findVerticalSeam()[j]);
      //          System.out.print(first[j]+ ",  ");
      //          System.out.println(second[j]);

         //   }
         //   sc.removeVerticalSeam(sc.findVerticalSeam());
     //  }
        for (int i = 0; i< 250; i++) {
      //      sc.removeHorizontalSeam(sc.findHorizontalSeam());
        sc.removeVerticalSeam(sc.findVerticalSeam());
       }

        sc.picture().show();


    }


}
