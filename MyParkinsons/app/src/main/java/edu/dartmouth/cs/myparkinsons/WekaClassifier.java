package edu.dartmouth.cs.myparkinsons;

/**
 * Created by Andrew on 2/15/15.
 */
class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N2bdd91140(i);
        return p;
    }
    static double N2bdd91140(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 91.925129) {
            p = WekaClassifier.N5f5fce8b1(i);
        } else if (((Double) i[0]).doubleValue() > 91.925129) {
            p = WekaClassifier.N12b730b96(i);
        }
        return p;
    }
    static double N5f5fce8b1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 49.878993) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 49.878993) {
            p = WekaClassifier.Naf138982(i);
        }
        return p;
    }
    static double Naf138982(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 2.483494) {
            p = WekaClassifier.N62ba88c43(i);
        } else if (((Double) i[5]).doubleValue() > 2.483494) {
            p = WekaClassifier.N44692e7f4(i);
        }
        return p;
    }
    static double N62ba88c43(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() <= 0.322761) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() > 0.322761) {
            p = 1;
        }
        return p;
    }
    static double N44692e7f4(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 6.897199) {
            p = WekaClassifier.N556bb6ef5(i);
        } else if (((Double) i[1]).doubleValue() > 6.897199) {
            p = 0;
        }
        return p;
    }
    static double N556bb6ef5(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 58.015981) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 58.015981) {
            p = 1;
        }
        return p;
    }
    static double N12b730b96(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 460.074767) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 460.074767) {
            p = WekaClassifier.N38dfcb487(i);
        }
        return p;
    }
    static double N38dfcb487(Object []i) {
        double p = Double.NaN;
        if (i[24] == null) {
            p = 2;
        } else if (((Double) i[24]).doubleValue() <= 10.046378) {
            p = 2;
        } else if (((Double) i[24]).doubleValue() > 10.046378) {
            p = WekaClassifier.N6c9b01238(i);
        }
        return p;
    }
    static double N6c9b01238(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 947.172567) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 947.172567) {
            p = 2;
        }
        return p;
    }
}